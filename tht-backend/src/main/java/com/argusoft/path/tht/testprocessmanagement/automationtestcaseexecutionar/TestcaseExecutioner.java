package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


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
    private TestcaseService testcaseService;
    @Autowired
    private TestcaseResultService testcaseResultService;
    @Autowired
    private TestRequestService testRequestService;

    public void executeTestingProcess(String testRequestId, String refObjUri, String refId, Boolean isManual, ContextInfo contextInfo) throws OperationFailedException {
        try {
            List<TestcaseResultEntity> testcaseResultEntities = fetchTestcaseResultsByInputs(testRequestId, refObjUri, refId, isManual, contextInfo);
            changeTestcaseResultsState(testcaseResultEntities, TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING, contextInfo);

            if (Objects.equals(Boolean.TRUE, isManual)) {
                return;
            }

            TestRequestEntity testRequestEntity = testRequestService.getTestRequestById(testRequestId, contextInfo);
            List<ComponentEntity> activeComponents = fetchActiveComponents(contextInfo).stream().filter(componentEntity ->
                    testRequestEntity.getTestRequestUrls().stream().anyMatch(testRequestUrlEntity -> testRequestUrlEntity.getComponent().getId().equals(componentEntity.getId()))
            ).collect(Collectors.toList());

            Map<String, IGenericClient> iGenericClientMap = new HashMap<>();
            for (ComponentEntity componentEntity : activeComponents) {
                TestRequestUrlEntity testRequestUrlEntity = testRequestEntity.getTestRequestUrls().stream().filter(testRequestUrl -> testRequestUrl.getComponent().getId().equals(componentEntity.getId())).findFirst().get();
                IGenericClient client = getClient(testRequestUrlEntity.getFhirVersion(), testRequestUrlEntity.getBaseUrl(), testRequestUrlEntity.getUsername(), testRequestUrlEntity.getPassword());
                iGenericClientMap.put(componentEntity.getId(), client);
            }

            executorService.execute(() -> execute(testcaseResultEntities, iGenericClientMap, Constant.SUPER_USER_CONTEXT));
        } catch (DataValidationErrorException e) {
            throw new OperationFailedException(e);
        } catch (InvalidParameterException | OperationFailedException | VersionMismatchException |
                 DoesNotExistException e) {
            throw new OperationFailedException(e.getMessage(), e);
        }
    }

    public void reinitializeTestingProcess(String testRequestId, String refObjUri, String refId, Boolean isManual, ContextInfo contextInfo) throws OperationFailedException {
        try {
            List<TestcaseResultEntity> testcaseResultEntities = fetchTestcaseResultsByInputs(testRequestId, refObjUri, refId, isManual, contextInfo);
            changeTestcaseResultsState(testcaseResultEntities, TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT, contextInfo);
        } catch (DoesNotExistException | InvalidParameterException | OperationFailedException |
                 VersionMismatchException ex) {
            throw new OperationFailedException("Operation failed while updating testcaseResults", ex);
        } catch (DataValidationErrorException ex) {
            throw new OperationFailedException(ex);
        }
    }

    @Transactional
    private void execute(List<TestcaseResultEntity> testcaseResultEntities,
                         Map<String, IGenericClient> iGenericClientMap,
                         ContextInfo contextInfo) {
        for (TestcaseResultEntity testcaseResult : testcaseResultEntities) {
            this.executeTestcase(testcaseResult, iGenericClientMap, contextInfo);
        }
    }

    private void executeTestcase(TestcaseResultEntity testcaseResult, Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) {
        try {
            testcaseResultService.changeState(testcaseResult.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS, contextInfo);
            TestcaseEntity testcaseEntity = testcaseService.getTestcaseById(testcaseResult.getRefId(), contextInfo);

            TestCase testCaseExecutionService = (TestCase) applicationContext.getBean(testcaseEntity.getBeanName());
            ValidationResultInfo validationResultInfo = testCaseExecutionService.test(iGenericClientMap, contextInfo);

            updateTestCaseResultByValidationResult(testcaseResult, validationResultInfo, contextInfo);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: add system failure log and connect it with testResult by refObjUri/refId.
            try {
                updateTestCaseResultForSystemError(testcaseResult, contextInfo);
            } catch (InvalidParameterException | DataValidationErrorException | OperationFailedException |
                     VersionMismatchException | DoesNotExistException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateTestCaseResultForSystemError(TestcaseResultEntity testcaseResultEntity, ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException, DoesNotExistException {
        testcaseResultEntity = testcaseResultService.getTestcaseResultById(testcaseResultEntity.getId(), contextInfo);
        testcaseResultEntity.setSuccess(Boolean.FALSE);
        testcaseResultEntity.setMessage("System failure");
        testcaseResultEntity.setHasSystemError(true);
        testcaseResultService.updateTestcaseResult(testcaseResultEntity, contextInfo);

        testcaseResultService.changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);
    }

    private void updateTestCaseResultByValidationResult(TestcaseResultEntity testcaseResultEntity, ValidationResultInfo validationResultInfo, ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, VersionMismatchException, DoesNotExistException {
        testcaseResultEntity = testcaseResultService.getTestcaseResultById(testcaseResultEntity.getId(), contextInfo);

        testcaseResultEntity.setSuccess(Objects.equals(validationResultInfo.getLevel(), ErrorLevel.OK));
        testcaseResultEntity.setMessage(validationResultInfo.getMessage());
        testcaseResultService.updateTestcaseResult(testcaseResultEntity, contextInfo);

        testcaseResultService.changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);
    }

    private IGenericClient getClient(String contextType, String serverBaseURL, String username, String password) throws OperationFailedException {
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

        //Commenting this code as adding certificate is not user requirement.
        //Add keyStore and trustStore
        /*try {
            String certificateKeyStorePath = "keystore.p12";
            String certificateKeyStorePassword = "1234";

            String certificateTrustStorePath = "keystore.p12";
            String certificateTrustStorePassword = "1234";

            FileInputStream keyStoreFileInputStream = new FileInputStream(certificateKeyStorePath);
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(keyStoreFileInputStream, certificateKeyStorePassword.toCharArray());



            FileInputStream trustStoreFileInputStream = new FileInputStream(certificateTrustStorePath);
            KeyStore truststore = KeyStore.getInstance("PKCS12");
            truststore.load(trustStoreFileInputStream, certificateTrustStorePassword.toCharArray());

            SSLContext sslContext =
                    SSLContexts
                            .custom()
                            .loadKeyMaterial(keystore, certificateKeyStorePassword.toCharArray())
                            .loadTrustMaterial(truststore, new TrustSelfSignedStrategy()).build();

            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslFactory).build();
            context.getRestfulClientFactory().setHttpClient(httpClient);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to add certificates for the keyStore/trustStore", e);
        }*/

        IGenericClient client = context.newRestfulGenericClient(serverBaseURL);

        //Log details of HTTP requests and responses made by the FHIR client
        client.registerInterceptor(new LoggingInterceptor());

        //Add username/password authentication credentials to the client from test Request
        client.registerInterceptor(new BasicAuthInterceptor(username, password));

        //Add token authentication credentials to the client from test Request
        //String token = "";
        //client.registerInterceptor(new BearerTokenAuthInterceptor(token));

        //GZipContentInterceptor will handle compression
        //client.registerInterceptor(new GZipContentInterceptor());

        //Add sessionCookie authentication credentials to the client from test Request
        //String sessionCookie = "";
        //client.registerInterceptor(new CookieInterceptor(sessionCookie));

        //Add Header parameters for all requests
        //client.registerInterceptor(new SimpleRequestHeaderInterceptor("Custom-Header", "123"));

        //The concept of "ThreadLocalCapturingInterceptor" suggests an interceptor that captures information specific to the current thread.
        //client.registerInterceptor(new ThreadLocalCapturingInterceptor());

        //This could be useful for tracking or managing requests based on user-related criteria on the server side.
        //String theUserId = "";
        //String theUserName = "";
        //String theAppName = "";
        //client.registerInterceptor(new UserInfoInterceptor(theUserId, theUserName, theAppName));

        return client;
    }

    private void changeTestcaseResultsState(List<TestcaseResultEntity> testcaseResultEntities, String newState, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        for (TestcaseResultEntity testcaseResult : testcaseResultEntities) {
            testcaseResultService.changeState(testcaseResult.getId(), newState, contextInfo);
        }
    }

    private List<TestcaseResultEntity> fetchTestcaseResultsByInputs(String testRequestId, String refObjUri, String refId, Boolean isManual, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        List<TestcaseResultEntity> filteredTestcaseResults;

        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
        testcaseResultCriteriaSearchFilter.setStates(Collections.singletonList(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT));
        testcaseResultCriteriaSearchFilter.setTestRequestId(testRequestId);
        testcaseResultCriteriaSearchFilter.setManual(Objects.equals(isManual, Boolean.TRUE));

        List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter, Constant.FULL_PAGE_SORT_BY_RANK, contextInfo).getContent();

        Optional<TestcaseResultEntity> optionalTestcaseResultEntity = testcaseResultEntities.stream().filter(testcaseResultEntity -> {
            return (testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT) || testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP)) && testcaseResultEntity.getRefObjUri().equals(refObjUri) && testcaseResultEntity.getRefId().equals(refId);
        }).findFirst();

        if (!optionalTestcaseResultEntity.isPresent()) {
            throw new OperationFailedException("No TestcaseResult found for the inputs.");
        }
        TestcaseResultEntity testcaseResultEntity = optionalTestcaseResultEntity.get();
        if (refObjUri.equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI)) {
            filteredTestcaseResults = Arrays.asList(testcaseResultEntity);
        } else if (refObjUri.equals(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI)) {
            filteredTestcaseResults
                    = testcaseResultEntities.stream()
                    .filter(tcre -> {
                        return tcre.getParentTestcaseResult() != null
                                && tcre.getParentTestcaseResult().getId().equals(testcaseResultEntity.getId());
                    }).collect(Collectors.toList());
        } else if (refObjUri.equals(ComponentServiceConstants.COMPONENT_REF_OBJ_URI)) {
            List<String> specificationTestcaseResultIds = testcaseResultEntities.stream()
                    .filter(tcre -> {
                        return tcre.getParentTestcaseResult() != null
                                && tcre.getParentTestcaseResult().getId().equals(testcaseResultEntity.getId());
                    }).map(tcre -> tcre.getId()).collect(Collectors.toList());
            filteredTestcaseResults
                    = testcaseResultEntities.stream()
                    .filter(tcre -> {
                        return tcre.getParentTestcaseResult() != null
                                && specificationTestcaseResultIds.contains(tcre.getParentTestcaseResult().getId());
                    }).collect(Collectors.toList());
        } else {
            filteredTestcaseResults
                    = testcaseResultEntities.stream()
                    .filter(tcre -> {
                        return tcre.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI);
                    })
                    .collect(Collectors.toList());
        }

        return filteredTestcaseResults;
    }

    private List<ComponentEntity> fetchActiveComponents(ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
        componentCriteriaSearchFilter.setState(Collections.singletonList(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE));
        return componentService.searchComponents(componentCriteriaSearchFilter, Constant.FULL_PAGE_SORT_BY_RANK, contextInfo).getContent();
    }

}