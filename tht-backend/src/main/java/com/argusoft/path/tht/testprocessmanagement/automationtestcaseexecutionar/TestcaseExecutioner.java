package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
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
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * This TestcaseExecutioner can start automation process by running testcases based on the testRequest.
 *
 * @author Dhruv
 */

@Component
public class TestcaseExecutioner {

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
    @Autowired
    private TestRequestService testRequestService;

    public void executeAutomationTestingByTestRequest(
            String testRequestId,
            ContextInfo contextInfo) throws OperationFailedException {
        try {
            Map<String, Map<String, TestcaseResultEntity>> testcaseResultMap = createDraftTestcaseResultsByTestRequest(testRequestId, Constant.START_AUTOMATION_PROCESS_VALIDATION, contextInfo);
            executorService.execute(() -> execute(testRequestId, testcaseResultMap, Constant.SUPER_USER_CONTEXT));
        } catch (DataValidationErrorException e) {
            throw new OperationFailedException(e);
        } catch (InvalidParameterException | OperationFailedException | VersionMismatchException |
                 DoesNotExistException e) {
            throw new OperationFailedException(e.getMessage(), e);
        }
    }

    public void reinitializeAutomationTestingByTestRequest(
            String testRequestId,
            ContextInfo contextInfo) throws OperationFailedException {
        try {
            updateTestcaseResultsToDraftByTestRequest(testRequestId, Constant.START_AUTOMATION_PROCESS_VALIDATION, contextInfo);
            updateTestRequestToInProgress(testRequestId, contextInfo);
        } catch (DoesNotExistException | InvalidParameterException |
                 OperationFailedException | VersionMismatchException ex) {
            throw new OperationFailedException("Operation failed while updating testcaseResults", ex);
        } catch (DataValidationErrorException ex) {
            throw new OperationFailedException(ex);
        }
    }

    private void updateTestRequestToInProgress(String testRequestId,
                                               ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        TestRequestEntity testRequestEntity = testRequestService.getTestRequestById(testRequestId, contextInfo);
        testRequestEntity.setState(TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS);
        testRequestService.updateTestRequest(testRequestEntity, contextInfo);
    }

    private void updateTestcaseResultsToDraftByTestRequest(String testRequestId,
                                                           String processTypeKey,
                                                           ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(
                null,
                new TestcaseResultSearchFilter(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        testRequestId,
                        Constant.START_MANUAL_PROCESS_VALIDATION.equals(processTypeKey) ? Boolean.TRUE : Boolean.FALSE,
                        null),
                Constant.FULL_PAGE,
                contextInfo).getContent();

        for (TestcaseResultEntity testcaseResult : testcaseResultEntities) {
            testcaseResult.setSuccess(null);
            testcaseResult.setTestcaseOption(null);
            testcaseResult.setHasSystemError(null);
            testcaseResult.setMessage(null);
            testcaseResult.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING);
            testcaseResultService.updateTestcaseResult(testcaseResult, contextInfo);
        }
    }

    public void executeManualTestingByTestRequest(
            String testRequestId,
            ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DoesNotExistException, DataValidationErrorException, VersionMismatchException {
        createDraftTestcaseResultsByTestRequest(testRequestId, Constant.START_MANUAL_PROCESS_VALIDATION, contextInfo);
    }

    private Map<String, Map<String, TestcaseResultEntity>> createDraftTestcaseResultsByTestRequest(String testRequestId,
                                                                                                   String processTypeKey,
                                                                                                   ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        TestRequestEntity testRequestEntity = testRequestService.getTestRequestById(testRequestId, contextInfo);
        Map<String, Map<String, TestcaseResultEntity>> testcaseResultMap = new HashMap<>();
        testcaseResultMap.put(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI, new HashMap<>());
        testcaseResultMap.put(ComponentServiceConstants.COMPONENT_REF_OBJ_URI, new HashMap<>());
        testcaseResultMap.put(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI, new HashMap<>());
        testcaseResultMap.put(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI, new HashMap<>());
        Integer counter = 1;

        List<ComponentEntity> activeComponents = fetchActiveComponents(processTypeKey, contextInfo);

        TestcaseResultEntity testRequestTestcaseResult = createOrFetchDraftTestCaseResultByValidationResults(
                TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                testRequestEntity.getId(),
                testRequestEntity.getId(),
                testRequestEntity.getName(),
                counter,
                processTypeKey,
                null,
                contextInfo);
        counter++;
        testcaseResultMap.get(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI)
                .put(testRequestEntity.getId(), testRequestTestcaseResult);

        for (ComponentEntity componentEntity : activeComponents) {
            TestcaseResultEntity componentTestcaseResult = createOrFetchDraftTestCaseResultByValidationResults(
                    ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                    componentEntity.getId(),
                    testRequestEntity.getId(),
                    componentEntity.getName(),
                    counter,
                    processTypeKey,
                    testRequestTestcaseResult.getId(),
                    contextInfo);
            testcaseResultMap.get(ComponentServiceConstants.COMPONENT_REF_OBJ_URI)
                    .put(componentEntity.getId(), componentTestcaseResult);
            counter++;

            List<SpecificationEntity> activeSpecifications = fetchActiveSpecifications(componentEntity.getId(), processTypeKey, contextInfo);
            for (SpecificationEntity specificationEntity : activeSpecifications) {
                TestcaseResultEntity specificationTestcaseResult = createOrFetchDraftTestCaseResultByValidationResults(
                        SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                        specificationEntity.getId(),
                        testRequestEntity.getId(),
                        specificationEntity.getName(),
                        counter,
                        processTypeKey,
                        componentTestcaseResult.getId(),
                        contextInfo);
                testcaseResultMap.get(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI)
                        .put(specificationEntity.getId(), specificationTestcaseResult);
                counter++;

                List<TestcaseEntity> activeTestcases = fetchActiveTestcases(specificationEntity.getId(), processTypeKey, contextInfo);
                for (TestcaseEntity testcaseEntity : activeTestcases) {
                    TestcaseResultEntity testcaseTestcaseResult = createOrFetchDraftTestCaseResultByValidationResults(
                            TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                            testcaseEntity.getId(),
                            testRequestEntity.getId(),
                            testcaseEntity.getName(),
                            counter,
                            processTypeKey,
                            specificationTestcaseResult.getId(),
                            contextInfo);
                    testcaseResultMap.get(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI)
                            .put(testcaseEntity.getId(), testcaseTestcaseResult);
                    counter++;
                }
            }
        }
        return testcaseResultMap;
    }

    private List<ComponentEntity> fetchActiveComponents(String processTypeKey, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        return componentService.searchComponents(
                null,
                new ComponentSearchFilter(null,
                        SearchType.CONTAINING,
                        ComponentServiceConstants.COMPONENT_STATUS_ACTIVE,
                        SearchType.EXACTLY,
                        Constant.START_MANUAL_PROCESS_VALIDATION.equals(processTypeKey) ? Boolean.TRUE : Boolean.FALSE),
                Constant.FULL_PAGE_SORT_BY_RANK,
                contextInfo).getContent();
    }

    private List<SpecificationEntity> fetchActiveSpecifications(String componentId, String processTypeKey, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        return specificationService.searchSpecifications(
                null,
                new SpecificationSearchFilter(null,
                        SearchType.CONTAINING,
                        SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE,
                        SearchType.EXACTLY,
                        componentId,
                        Constant.START_MANUAL_PROCESS_VALIDATION.equals(processTypeKey) ? Boolean.TRUE : Boolean.FALSE),
                Constant.FULL_PAGE_SORT_BY_RANK,
                contextInfo).getContent();
    }

    private List<TestcaseEntity> fetchActiveTestcases(String specificationId, String processTypeKey, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        return testcaseService.searchTestcases(
                null,
                new TestcaseSearchFilter(null,
                        SearchType.EXACTLY,
                        TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE,
                        SearchType.EXACTLY,
                        specificationId,
                        Constant.START_MANUAL_PROCESS_VALIDATION.equals(processTypeKey) ? Boolean.TRUE : Boolean.FALSE),
                Constant.FULL_PAGE_SORT_BY_RANK,
                contextInfo).getContent();
    }

    @Transactional
    private void execute(String testRequestId,
                         Map<String, Map<String, TestcaseResultEntity>> testcaseResultMap,
                         ContextInfo contextInfo) {
        try {
            TestRequestEntity testRequestEntity = testRequestService.getTestRequestById(testRequestId, contextInfo);

            testcaseResultMap.get(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI)
                    .put(testRequestEntity.getId(),
                            makeTestCaseResultInProgress(
                                    testcaseResultMap.get(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI).get(testRequestEntity.getId()),
                                    contextInfo)
                    );
            List<ComponentEntity> activeComponents = fetchActiveComponents(Constant.START_AUTOMATION_PROCESS_VALIDATION, contextInfo);
            List<ValidationResultInfo> validationResultInfos = new ArrayList<>();
            for (ComponentEntity componentEntity : activeComponents) {
                if (!testcaseResultMap.get(ComponentServiceConstants.COMPONENT_REF_OBJ_URI).containsKey(componentEntity.getId())) {
                    continue;
                }
                TestRequestUrlEntity testRequestUrlEntity = testRequestEntity.getTestRequestUrls().stream().filter(testRequestUrl -> testRequestUrl.getComponent().getId().equals(componentEntity.getId())).findFirst().get();
                //TODO: Move FhirVersion in testRequestUrlEntity
                IGenericClient client = getClient(testRequestEntity.getFhirVersion(), testRequestUrlEntity.getBaseUrl(), testRequestUrlEntity.getUsername(), testRequestUrlEntity.getPassword());

                validationResultInfos.add(
                        this.testComponent(componentEntity.getId(),
                                testcaseResultMap,
                                client,
                                contextInfo));
            }

            updateTestCaseResultByValidationResults(
                    testcaseResultMap.get(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI).get(testRequestEntity.getId()),
                    validationResultInfos,
                    contextInfo);

        } catch (Exception e) {
            e.printStackTrace();
            ValidationResultInfo validationResultInfo = new ValidationResultInfo(null, ErrorLevel.ERROR, "SYSTEM_FAILURE");
            //TODO: add system failure log and connect it with testResult by refObjUri/refId.
            try {
                updateTestCaseResultForSystemError(
                        testcaseResultMap.get(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI).get(testRequestId),
                        validationResultInfo,
                        contextInfo);
            } catch (InvalidParameterException | DataValidationErrorException | OperationFailedException |
                     VersionMismatchException | DoesNotExistException ex) {
                ex.printStackTrace();
            }
        }
    }

    private ValidationResultInfo testComponent(String componentId,
                                               Map<String, Map<String, TestcaseResultEntity>> testcaseResultMap,
                                               IGenericClient client,
                                               ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException, DoesNotExistException {
        try {
            testcaseResultMap.get(ComponentServiceConstants.COMPONENT_REF_OBJ_URI)
                    .put(componentId,
                            makeTestCaseResultInProgress(testcaseResultMap.get(ComponentServiceConstants.COMPONENT_REF_OBJ_URI).get(componentId), contextInfo)
                    );

            List<SpecificationEntity> activeSpecifications = fetchActiveSpecifications(componentId, Constant.START_AUTOMATION_PROCESS_VALIDATION, contextInfo);
            List<ValidationResultInfo> validationResultInfos = new ArrayList<>();
            for (SpecificationEntity specificationEntity : activeSpecifications) {
                if (!testcaseResultMap.get(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI).containsKey(specificationEntity.getId())) {
                    continue;
                }
                validationResultInfos.add(
                        this.testSpecification(specificationEntity.getId(),
                                testcaseResultMap,
                                client,
                                contextInfo));
            }

            return updateTestCaseResultByValidationResults(
                    testcaseResultMap.get(ComponentServiceConstants.COMPONENT_REF_OBJ_URI).get(componentId),
                    validationResultInfos,
                    contextInfo);

        } catch (Exception e) {
            e.printStackTrace();
            ValidationResultInfo validationResultInfo = new ValidationResultInfo(null, ErrorLevel.ERROR, "SYSTEM_FAILURE");
            //TODO: add system failure log and connect it with testResult by refObjUri/refId.
            try {
                updateTestCaseResultForSystemError(
                        testcaseResultMap.get(ComponentServiceConstants.COMPONENT_REF_OBJ_URI).get(componentId),
                        validationResultInfo,
                        contextInfo);
            } catch (InvalidParameterException | DataValidationErrorException | OperationFailedException |
                     VersionMismatchException | DoesNotExistException ex) {
                ex.printStackTrace();
            }
            return validationResultInfo;
        }
    }

    private ValidationResultInfo testSpecification(String specificationId,
                                                   Map<String, Map<String, TestcaseResultEntity>> testcaseResultMap,
                                                   IGenericClient client,
                                                   ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException, DoesNotExistException {
        try {
            testcaseResultMap.get(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI).put(specificationId,
                    makeTestCaseResultInProgress(testcaseResultMap.get(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI).get(specificationId), contextInfo)
            );

            List<TestcaseEntity> activeTestcases = fetchActiveTestcases(specificationId, Constant.START_AUTOMATION_PROCESS_VALIDATION, contextInfo);

            List<ValidationResultInfo> validationResultInfos = new ArrayList<>();
            for (TestcaseEntity testcaseEntity : activeTestcases) {
                if (!testcaseResultMap.get(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI).containsKey(testcaseEntity.getId())) {
                    continue;
                }

                validationResultInfos.add(this.executeTestcase(
                        testcaseEntity,
                        testcaseResultMap,
                        client,
                        contextInfo));
            }

            return updateTestCaseResultByValidationResults(
                    testcaseResultMap.get(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI).get(specificationId),
                    validationResultInfos,
                    contextInfo);

        } catch (Exception e) {
            e.printStackTrace();
            ValidationResultInfo validationResultInfo = new ValidationResultInfo(null, ErrorLevel.ERROR, "SYSTEM_FAILURE");
            //TODO: add system failure log and connect it with testResult by refObjUri/refId.
            try {
                updateTestCaseResultForSystemError(
                        testcaseResultMap.get(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI).get(specificationId),
                        validationResultInfo,
                        contextInfo);
            } catch (InvalidParameterException | DataValidationErrorException | OperationFailedException |
                     VersionMismatchException | DoesNotExistException ex) {
                ex.printStackTrace();
            }
            return validationResultInfo;
        }
    }

    private ValidationResultInfo executeTestcase(TestcaseEntity testcaseEntity,
                                                 Map<String, Map<String, TestcaseResultEntity>> testcaseResultMap,
                                                 IGenericClient client,
                                                 ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException, DoesNotExistException {
        try {
            testcaseResultMap.get(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI).put(testcaseEntity.getId(),
                    makeTestCaseResultInProgress(testcaseResultMap.get(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI).get(testcaseEntity.getId()), contextInfo)
            );

            TestCase testCaseExecutionService = (TestCase) applicationContext.getBean(testcaseEntity.getBeanName());
            ValidationResultInfo validationResultInfo = testCaseExecutionService.test(client, contextInfo);

            updateTestCaseResultByValidationResult(
                    testcaseResultMap.get(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI).get(testcaseEntity.getId()),
                    validationResultInfo,
                    contextInfo);

            return validationResultInfo;
        } catch (Exception e) {
            e.printStackTrace();
            ValidationResultInfo validationResultInfo = new ValidationResultInfo(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI + "~" + testcaseEntity.getId(), ErrorLevel.ERROR, "SYSTEM_FAILURE");
            //TODO: add system failure log and connect it with testResult by refObjUri/refId.
            try {
                updateTestCaseResultForSystemError(
                        testcaseResultMap.get(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI).get(testcaseEntity.getId()),
                        validationResultInfo,
                        contextInfo);
            } catch (InvalidParameterException | DataValidationErrorException | OperationFailedException |
                     VersionMismatchException | DoesNotExistException ex) {
                ex.printStackTrace();
            }
            return validationResultInfo;
        }
    }

    private TestcaseResultEntity makeTestCaseResultInProgress(TestcaseResultEntity testcaseResultEntity,
                                                              ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException,
            DataValidationErrorException,
            VersionMismatchException,
            DoesNotExistException {
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS);
        return testcaseResultService.updateTestcaseResult(testcaseResultEntity, contextInfo);
    }

    private TestcaseResultEntity createOrFetchDraftTestCaseResultByValidationResults(String refObjUri,
                                                                                     String refId,
                                                                                     String testRequestId,
                                                                                     String name,
                                                                                     Integer counter,
                                                                                     String processTypeKey,
                                                                                     String parentTestcaseResultId,
                                                                                     ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {
        List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(
                null,
                new TestcaseResultSearchFilter(
                        null,
                        null,
                        TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING,
                        SearchType.EXACTLY,
                        null,
                        refObjUri,
                        refId,
                        testRequestId,
                        Constant.START_MANUAL_PROCESS_VALIDATION.equals(processTypeKey) ? Boolean.TRUE : Boolean.FALSE,
                        null),
                Constant.FULL_PAGE,
                contextInfo).getContent();

        if (!testcaseResultEntities.isEmpty()) {
            return testcaseResultEntities.get(0);
        }

        TestcaseResultEntity testcaseResultEntity = new TestcaseResultEntity();
        testcaseResultEntity.setRefObjUri(refObjUri);
        testcaseResultEntity.setRefId(refId);
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING);
        testcaseResultEntity.setTestRequestId(testRequestId);
        testcaseResultEntity.setRank(counter);
        testcaseResultEntity.setName(name);
        testcaseResultEntity.setManual(Constant.START_MANUAL_PROCESS_VALIDATION.equals(processTypeKey) ? Boolean.TRUE : Boolean.FALSE);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(contextInfo.getUsername());
        testcaseResultEntity.setTester(userEntity);
        if (!StringUtils.isEmpty(parentTestcaseResultId)) {
            TestcaseResultEntity parentTestcaseResult = new TestcaseResultEntity();
            parentTestcaseResult.setId(parentTestcaseResultId);
            testcaseResultEntity.setParentTestcaseResult(parentTestcaseResult);
        }
        return testcaseResultService.createTestcaseResult(testcaseResultEntity, contextInfo);
    }

    private ValidationResultInfo updateTestCaseResultByValidationResults(TestcaseResultEntity testcaseResultEntity,
                                                                         List<ValidationResultInfo> validationResultInfos,
                                                                         ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException, DoesNotExistException {
        ValidationResultInfo validationResultInfo;
        if (ValidationUtils.containsErrors(validationResultInfos, ErrorLevel.ERROR)) {
            validationResultInfo = new ValidationResultInfo(null, ErrorLevel.ERROR, "Failed");
        } else {
            validationResultInfo = new ValidationResultInfo(null, ErrorLevel.OK, "Passed");
        }
        updateTestCaseResultByValidationResult(testcaseResultEntity, validationResultInfo, contextInfo);
        return validationResultInfo;
    }

    private void updateTestCaseResultForSystemError(TestcaseResultEntity testcaseResultEntity,
                                                    ValidationResultInfo validationResultInfo,
                                                    ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException, DoesNotExistException {
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED);
        testcaseResultEntity.setSuccess(Boolean.FALSE);
        testcaseResultEntity.setMessage(validationResultInfo.getMessage());
        testcaseResultEntity.setHasSystemError(true);
        testcaseResultService.updateTestcaseResult(testcaseResultEntity, contextInfo);
    }

    private void updateTestCaseResultByValidationResult(TestcaseResultEntity testcaseResultEntity,
                                                        ValidationResultInfo validationResultInfo,
                                                        ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException, DoesNotExistException {
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED);
        testcaseResultEntity.setSuccess(Objects.equals(validationResultInfo.getLevel(), ErrorLevel.OK) ? Boolean.TRUE : Boolean.FALSE);
        testcaseResultEntity.setMessage(validationResultInfo.getMessage());
        testcaseResultService.updateTestcaseResult(testcaseResultEntity, contextInfo);
    }

    private IGenericClient getClient(String contextType, String serverBaseURL, String username, String password) {
        FhirContext context;
        switch (contextType) {
            case "D2":
                context = FhirContext.forDstu2();
                break;
            case "D3":
                context = FhirContext.forDstu3();
                break;
            default:
                //Default is for R4
                context = FhirContext.forR4();
        }

        context.getRestfulClientFactory().setConnectTimeout(60 * 1000);
        context.getRestfulClientFactory().setSocketTimeout(60 * 1000);
        IGenericClient client = context.newRestfulGenericClient(serverBaseURL);

        // TODO: Add authentication credentials to the client from test Request
        // client.registerInterceptor(new BasicAuthInterceptor(username, password));

        return client;
    }

}
