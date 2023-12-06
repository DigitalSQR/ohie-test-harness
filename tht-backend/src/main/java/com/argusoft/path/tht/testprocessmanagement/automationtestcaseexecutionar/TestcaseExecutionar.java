package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.reportmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.VersionMismatchException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implemantation of the CR Testing.
 * Reference
 *
 * @author dhruv
 * @since 2023-09-25
 */
@Component
public class TestcaseExecutionar {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private SpecificationService specificationService;
    @Autowired
    private TestcaseService testcaseService;
    @Autowired
    private TestcaseResultService testcaseResultService;

    public void executeAutomationTestingByTestRequest(
            String testRequestId,
            ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        try {
            createDraftTestcaseResultsByTestRequest(testRequestId, contextInfo);
        } catch (DataValidationErrorException e) {
            throw new OperationFailedException(e);
        } catch (InvalidParameterException | OperationFailedException e) {
            throw new OperationFailedException(e.getMessage(), e);
        }
        executorService.execute(() -> testRequest(testRequestId, contextInfo));
    }

    private void createDraftTestcaseResultsByTestRequest(String testRequestId,
                                                         ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        Integer counter = 1;
        //TODO: Update TestCaseResult name based on the TestRequest Name
        createDraftTestCaseResultByValidationResults(
                TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                testRequestId,
                testRequestId,
                "TestRequest",
                counter,
                contextInfo);
        counter++;

        List<ComponentEntity> activeComponents = fetchActiveComponents(contextInfo);
        for (ComponentEntity componentEntity : activeComponents) {
            createDraftTestCaseResultByValidationResults(
                    ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                    componentEntity.getId(),
                    testRequestId,
                    componentEntity.getName(),
                    counter,
                    contextInfo);
            counter++;

            List<SpecificationEntity> activeSpecifications = fetchActiveSpecifications(componentEntity.getId(), contextInfo);
            for (SpecificationEntity specificationEntity : activeSpecifications) {
                createDraftTestCaseResultByValidationResults(
                        SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                        specificationEntity.getId(),
                        testRequestId,
                        specificationEntity.getName(),
                        counter,
                        contextInfo);
                counter++;

                List<TestcaseEntity> activeTestcases = fetchActiveTestcases(specificationEntity.getId(), contextInfo);
                for (TestcaseEntity testcaseEntity : activeTestcases) {
                    createDraftTestCaseResultByValidationResults(
                            TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                            testcaseEntity.getId(),
                            testRequestId,
                            testcaseEntity.getName(),
                            counter,
                            contextInfo);
                    counter++;
                }
            }
        }
    }

    private List<ComponentEntity> fetchActiveComponents(ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        return componentService.searchComponents(
                null,
                new ComponentSearchFilter(null,
                        SearchType.CONTAINING,
                        ComponentServiceConstants.COMPONENT_STATUS_ACTIVE,
                        SearchType.EXACTLY),
                Constant.FULL_PAGE_SORT_BY_RANK,
                contextInfo).getContent();
    }

    private List<SpecificationEntity> fetchActiveSpecifications(String componentId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        return specificationService.searchSpecifications(
                null,
                new SpecificationSearchFilter(null,
                        SearchType.CONTAINING,
                        SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE,
                        SearchType.EXACTLY,
                        componentId),
                Constant.FULL_PAGE_SORT_BY_RANK,
                contextInfo).getContent();
    }

    private List<TestcaseEntity> fetchActiveTestcases(String specificationId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        return testcaseService.searchTestcases(
                null,
                new TestcaseSearchFilter(null,
                        SearchType.EXACTLY,
                        TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE,
                        SearchType.EXACTLY,
                        specificationId),
                Constant.FULL_PAGE_SORT_BY_RANK,
                contextInfo).getContent();
    }

    private void testRequest(String testRequestId,
                             ContextInfo contextInfo) {
        try {
            makeTestCaseResultInProgress(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI, testRequestId, testRequestId, contextInfo);

            List<ComponentEntity> activeComponents = fetchActiveComponents(contextInfo);
            //TODO: Filter activeComponents based on the testRequest as well

            //TODO: Create IGenericClient Based on the component's baseUrl instead of fix value
            IGenericClient client = getClient(null, null, null, null);

            List<ValidationResultInfo> validationResultInfos = new ArrayList<>();
            for (ComponentEntity componentEntity : activeComponents) {
                validationResultInfos.add(
                        this.testComponent(componentEntity.getId(),
                                testRequestId,
                                client,
                                contextInfo));
            }

            updateTestCaseResultByValidationResults(
                    TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                    testRequestId,
                    testRequestId,
                    validationResultInfos,
                    contextInfo);

        } catch (Exception e) {
            //TODO: add system failure for testRequest
        }
    }

    private ValidationResultInfo testComponent(String componentId,
                                               String testRequestId,
                                               IGenericClient client,
                                               ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        try {
            makeTestCaseResultInProgress(ComponentServiceConstants.COMPONENT_REF_OBJ_URI, componentId, testRequestId, contextInfo);

            List<SpecificationEntity> activeSpecifications = fetchActiveSpecifications(componentId, contextInfo);

            List<ValidationResultInfo> validationResultInfos = new ArrayList<>();
            for (SpecificationEntity specificationEntity : activeSpecifications) {
                validationResultInfos.add(
                        this.testSpecification(specificationEntity.getId(),
                                testRequestId,
                                client,
                                contextInfo));
            }

            return updateTestCaseResultByValidationResults(
                    ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                    componentId,
                    testRequestId,
                    validationResultInfos,
                    contextInfo);

        } catch (Exception e) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo(ComponentServiceConstants.COMPONENT_REF_OBJ_URI + "~" + componentId, ErrorLevel.ERROR, "SYSTEM_FAILURE");
            //TODO: add system failure log and connect it with testResult by refObjUri/refId.
            updateTestCaseResultForSystemError(
                    ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                    componentId,
                    testRequestId,
                    validationResultInfo,
                    contextInfo);
            return validationResultInfo;
        }
    }

    private ValidationResultInfo testSpecification(String specificationId,
                                                   String testRequestId,
                                                   IGenericClient client,
                                                   ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        try {
            makeTestCaseResultInProgress(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI, specificationId, testRequestId, contextInfo);

            List<TestcaseEntity> activeTestcases = fetchActiveTestcases(specificationId, contextInfo);

            List<ValidationResultInfo> validationResultInfos = new ArrayList<>();
            for (TestcaseEntity testcaseEntity : activeTestcases) {
                validationResultInfos.add(this.executeTestcase(
                        testcaseEntity,
                        testRequestId,
                        client,
                        contextInfo));
            }

            return updateTestCaseResultByValidationResults(
                    SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                    specificationId,
                    testRequestId,
                    validationResultInfos,
                    contextInfo);

        } catch (Exception e) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI + "~" + specificationId, ErrorLevel.ERROR, "SYSTEM_FAILURE");
            //TODO: add system failure log and connect it with testResult by refObjUri/refId.
            updateTestCaseResultForSystemError(
                    SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                    specificationId,
                    testRequestId,
                    validationResultInfo,
                    contextInfo);
            return validationResultInfo;
        }
    }

    private ValidationResultInfo executeTestcase(TestcaseEntity testcaseEntity,
                                                 String testRequestId,
                                                 IGenericClient client,
                                                 ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        try {
            makeTestCaseResultInProgress(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI, testcaseEntity.getId(), testRequestId, contextInfo);

            TestCase testCaseExecutionService = (TestCase) applicationContext.getBean(testcaseEntity.getBeanName());
            ValidationResultInfo validationResultInfo = testCaseExecutionService.test(client, contextInfo);

            updateTestCaseResultByValidationResult(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                    testcaseEntity.getId(),
                    testRequestId,
                    validationResultInfo,
                    contextInfo);

            return validationResultInfo;
        } catch (Exception e) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI + "~" + testcaseEntity.getId(), ErrorLevel.ERROR, "SYSTEM_FAILURE");
            //TODO: add system failure log and connect it with testResult by refObjUri/refId.
            updateTestCaseResultForSystemError(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                    testcaseEntity.getId(),
                    testRequestId,
                    validationResultInfo,
                    contextInfo);
            return validationResultInfo;
        }
    }

    private void makeTestCaseResultInProgress(String refObjUri,
                                              String refId,
                                              String testRequestId,
                                              ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException, DataValidationErrorException, VersionMismatchException {

        List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(null,
                new TestcaseResultSearchFilter(
                        null,
                        null,
                        TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING,
                        SearchType.EXACTLY,
                        null,
                        refObjUri,
                        refId,
                        testRequestId),
                Constant.SINGLE_VALUE_PAGE,
                contextInfo).getContent();

        if (!testcaseResultEntities.isEmpty()) {
            TestcaseResultEntity testcaseResultEntity = testcaseResultEntities.get(0);
            testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS);
            testcaseResultService.updateTestcaseResult(testcaseResultEntity, contextInfo);
        } else {
            //TODO: handle if testcaseResult don't exists
        }
    }

    private void createDraftTestCaseResultByValidationResults(String refObjUri,
                                                              String refId,
                                                              String testRequestId,
                                                              String name,
                                                              Integer counter,
                                                              ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException {
        TestcaseResultEntity testcaseResultEntity = new TestcaseResultEntity();
        testcaseResultEntity.setRefObjUri(refObjUri);
        testcaseResultEntity.setRefId(refId);
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING);
        testcaseResultEntity.setTestRequestId(testRequestId);
        testcaseResultEntity.setRank(counter);
        testcaseResultEntity.setName(name);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(contextInfo.getUsername());
        testcaseResultEntity.setTester(userEntity);

        testcaseResultService.createTestcaseResult(testcaseResultEntity, contextInfo);
    }

    private ValidationResultInfo updateTestCaseResultByValidationResults(String refObjUri,
                                                                         String refId,
                                                                         String testRequestId,
                                                                         List<ValidationResultInfo> validationResultInfos,
                                                                         ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        ValidationResultInfo validationResultInfo;
        if (ValidationUtils.containsErrors(validationResultInfos, ErrorLevel.ERROR)) {
            validationResultInfo = new ValidationResultInfo(refObjUri + "~" + refId, ErrorLevel.ERROR, "Failed");
        } else {
            validationResultInfo = new ValidationResultInfo(refObjUri + "~" + refId, ErrorLevel.OK, "Passed");
        }
        updateTestCaseResultByValidationResult(refObjUri, refId, testRequestId, validationResultInfo, contextInfo);
        return validationResultInfo;
    }

    private void updateTestCaseResultForSystemError(String refObjUri,
                                                        String refId,
                                                        String testRequestId,
                                                        ValidationResultInfo validationResultInfo,
                                                        ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(null,
                new TestcaseResultSearchFilter(
                        null,
                        null,
                        TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS,
                        SearchType.EXACTLY,
                        null,
                        refObjUri,
                        refId,
                        testRequestId),
                Constant.SINGLE_VALUE_PAGE,
                contextInfo).getContent();
        if (!testcaseResultEntities.isEmpty()) {
            TestcaseResultEntity testcaseResultEntity = testcaseResultEntities.get(0);
            testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FAILED);
            testcaseResultEntity.setMessage(validationResultInfo.getMessage());
            testcaseResultEntity.setHasSystemError(true);
            testcaseResultService.updateTestcaseResult(testcaseResultEntity, contextInfo);
        } else {
            //TODO: handle if testcaseResult don't exists
        }
    }

    private void updateTestCaseResultByValidationResult(String refObjUri,
                                                        String refId,
                                                        String testRequestId,
                                                        ValidationResultInfo validationResultInfo,
                                                        ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(null,
                new TestcaseResultSearchFilter(
                        null,
                        null,
                        TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS,
                        SearchType.EXACTLY,
                        null,
                        refObjUri,
                        refId,
                        testRequestId),
                Constant.SINGLE_VALUE_PAGE,
                contextInfo).getContent();
        if (!testcaseResultEntities.isEmpty()) {
            TestcaseResultEntity testcaseResultEntity = testcaseResultEntities.get(0);
            testcaseResultEntity.setState(
                    Objects.equals(validationResultInfo.getLevel(), ErrorLevel.OK)
                            ? TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PASSED
                            : TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FAILED);
            testcaseResultEntity.setMessage(validationResultInfo.getMessage());
            testcaseResultService.updateTestcaseResult(testcaseResultEntity, contextInfo);
        } else {
            //TODO: handle if testcaseResult don't exists
        }
    }

    private IGenericClient getClient(String contextType, String serverBaseURL, String username, String password) {
        //TODO: Remove this
        contextType = "R4"; //create enum for this
        serverBaseURL = "https://hapi.fhir.org/baseR4";
        //http://hapi.fhir.org/baseR4
        //https://hapi.fhir.org/baseDstu2
        //https://hapi.fhir.org/baseDstu3
        //http://hapi.fhir.org/search?serverId=home_r4&pretty=true&_summary=&resource=Patient&param.0.0=&param.0.1=&param.0.2=&param.0.3=&param.0.name=birthdate&param.0.type=date&sort_by=&sort_direction=&resource-search-limit=
        //http://localhost:8080/fhir

        FhirContext context;
        switch (contextType) {
            case "D2":
                context = FhirContext.forDstu2();
                break;
            case "D3":
                context = FhirContext.forDstu3();
                break;
            default:
                context = FhirContext.forR4();
        }

        context.getRestfulClientFactory().setConnectTimeout(60 * 1000); //fix configuration
        context.getRestfulClientFactory().setSocketTimeout(60 * 1000); //fix configuration
        IGenericClient client = context.newRestfulGenericClient(serverBaseURL);

        // Add authentication credentials to the client from test Request
        // client.registerInterceptor(new BasicAuthInterceptor(username, password));

        return client;
    }

}
