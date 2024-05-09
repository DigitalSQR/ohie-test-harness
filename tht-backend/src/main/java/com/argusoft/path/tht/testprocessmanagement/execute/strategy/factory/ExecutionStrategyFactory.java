package com.argusoft.path.tht.testprocessmanagement.execute.strategy.factory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestResultRelationService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.testbed.services.TestSessionManagementRestService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.util.TestcaseExecutioner;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.ExecutionStrategy;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.EuTestBedEnvironmentStrategy;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.JavaInBuiltEnvironmentStrategy;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.UpdateSessionAndMarkTestcaseResultInProgress;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.TestSessionStarter;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.VerifyTestcaseSessionStatusAndUpdateAccordingly;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.StatusCallback;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.util.EuTestBedCommonUtil;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENT_EU_TESTBED;
import static com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENT_JAVA_THT;

@Component
public class ExecutionStrategyFactory {

    public static final Logger LOGGER = LoggerFactory.getLogger(ExecutionStrategyFactory.class);
    private TestcaseResultService testcaseResultService;

    private ComponentService componentService;

    private TestcaseExecutioner testcaseExecutioner;

    private TestSessionStarter testSessionStarter;

    /*@Value("${testbed.testsuite-id}")
    private String testsuiteId;*/

    private StatusCallback statusCallback;

    private UpdateSessionAndMarkTestcaseResultInProgress updateSessionAndMarkTestcaseResultInProgress;

    private VerifyTestcaseSessionStatusAndUpdateAccordingly verifyTestcaseSessionStatusAndUpdateAccordingly;

    private TestResultRelationService testResultRelationService;

    private EuTestBedCommonUtil euTestBedCommonUtil;


    public ExecutionStrategy createExecutionStrategyBasedOnTestcaseResultId(String testcaseResultId, ContextInfo contextInfo)
            throws InvalidParameterException, DoesNotExistException, OperationFailedException, DataValidationErrorException {

        TestcaseResultEntity testcaseResultById = testcaseResultService.getTestcaseResultById(testcaseResultId, contextInfo);
        String executionEnvironment = testcaseResultById.getTestcase().getTestcaseRunEnvironment();

        return switch (executionEnvironment) {
            case TESTCASE_RUN_ENVIRONMENT_JAVA_THT -> {
                Map<String, IGenericClient> iGenericClientMap = prepareIgenericClientMap(testcaseResultById.getTestRequest(), contextInfo);
                yield new JavaInBuiltEnvironmentStrategy(testcaseResultId, iGenericClientMap, testcaseExecutioner, contextInfo);
            }
            case TESTCASE_RUN_ENVIRONMENT_EU_TESTBED -> {
                Map<String,String> inputParameters = getInputParametersForTestcaseResult(testcaseResultById);
                List<Object> testResultRelationEntitiesFromAuditMapping = testResultRelationService.getTestResultRelationEntitiesFromAuditMapping(testcaseResultId, TestcaseServiceConstants.TESTCASE_REF_OBJ_URI, contextInfo);
                Optional<TestcaseEntity> testcaseEntityOptional = testResultRelationEntitiesFromAuditMapping.stream().findFirst().map(TestcaseEntity.class::cast);

                if(testcaseEntityOptional.isEmpty()){
                    LOGGER.error("Testcase not found in testcase result relation for testcaseResultId ->"+testcaseResultId);
                    throw new DoesNotExistException("Testcase not found in testcase result relation for testcaseResultId ->"+testcaseResultId);
                }

                TestcaseEntity testcase = testcaseEntityOptional.get();
                String testSuiteId = testcase.getTestSuiteId();
                String sutActorApiKey = testcase.getSutActorApiKey();
                yield  new EuTestBedEnvironmentStrategy(testcaseResultId, testcaseResultService,
                        updateSessionAndMarkTestcaseResultInProgress,
                        verifyTestcaseSessionStatusAndUpdateAccordingly, testSessionStarter, testSuiteId, statusCallback, inputParameters, euTestBedCommonUtil,sutActorApiKey ,contextInfo);
            }

            default -> throw new IllegalArgumentException("Unsupported execution environment found while running testcases");

        };
    }

    private Map<String, String> getInputParametersForTestcaseResult(TestcaseResultEntity testcaseResultById) {
        Map<String,String> inputParameters = new HashMap<>();
        ComponentEntity testcaseResultRelatedComponent = testcaseResultById.getTestcase().getSpecification().getComponent();
        Set<TestRequestUrlEntity> testRequestUrls = testcaseResultById.getTestRequest().getTestRequestUrls();
        Optional<TestRequestUrlEntity> optionalTestRequestUrlEntity = testRequestUrls.stream().filter(testRequestUrlEntity -> testRequestUrlEntity.getComponent().getId().equals(testcaseResultRelatedComponent.getId())).findFirst();
        if(optionalTestRequestUrlEntity.isPresent()){
            TestRequestUrlEntity testRequestUrlEntity = optionalTestRequestUrlEntity.get();
            inputParameters.put("componentURI", testRequestUrlEntity.getFhirApiBaseUrl());
            inputParameters.put("username",testRequestUrlEntity.getUsername());
            inputParameters.put("password",testRequestUrlEntity.getPassword());
        }
        return inputParameters;
    }

    private Map<String, IGenericClient> prepareIgenericClientMap(TestRequestEntity testRequestEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

            List<ComponentEntity> activeComponents = fetchActiveComponents(contextInfo).stream().filter(componentEntity
                    -> testRequestEntity.getTestRequestUrls().stream().anyMatch(testRequestUrlEntity -> testRequestUrlEntity.getComponent().getId().equals(componentEntity.getId()))
            ).collect(Collectors.toList());

            Map<String, IGenericClient> iGenericClientMap = new HashMap<>();
            for (ComponentEntity componentEntity : activeComponents) {
                Optional<TestRequestUrlEntity> testRequestUrlOptional = testRequestEntity.getTestRequestUrls().stream().filter(testRequestUrl -> testRequestUrl.getComponent().getId().equals(componentEntity.getId())).findFirst();
                if (testRequestUrlOptional.isPresent()) {
                    TestRequestUrlEntity testRequestUrlEntity = testRequestUrlOptional.get();
                    IGenericClient client = getClient(testRequestUrlEntity.getFhirVersion(), testRequestUrlEntity.getFhirApiBaseUrl(), testRequestUrlEntity.getUsername(), testRequestUrlEntity.getPassword());
                    iGenericClientMap.put(componentEntity.getId(), client);
                } else {
                    LOGGER.error("Unable to find testRequestUrl for {}", componentEntity.getId());
                    throw new OperationFailedException("Unable to find testRequestUrl for " + componentEntity.getId());
                }
            }
            return iGenericClientMap;
    }


    private List<ComponentEntity> fetchActiveComponents(ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
        componentCriteriaSearchFilter.setState(Collections.singletonList(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE));
        return componentService.searchComponents(componentCriteriaSearchFilter, Constant.FULL_PAGE_SORT_BY_RANK, contextInfo).getContent();
    }


    private IGenericClient getClient(String contextType, String serverBaseURL, String username, String password) {
        FhirContext context;
        if (contextType == null) {
            contextType = "R4";
        }
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

    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }

    @Autowired
    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    @Autowired
    public void setTestcaseExecutioner(TestcaseExecutioner testcaseExecutioner) {
        this.testcaseExecutioner = testcaseExecutioner;
    }

    @Autowired
    public void setTestSessionStarter(TestSessionStarter testSessionStarter) {
        this.testSessionStarter = testSessionStarter;
    }

    @Autowired
    public void setUpdateSessionAndMarkTestcaseResultInProgress(UpdateSessionAndMarkTestcaseResultInProgress updateSessionAndMarkTestcaseResultInProgress) {
        this.updateSessionAndMarkTestcaseResultInProgress = updateSessionAndMarkTestcaseResultInProgress;
    }

    @Autowired
    public void setVerifyTestcaseSessionStatusAndUpdateAccordingly(VerifyTestcaseSessionStatusAndUpdateAccordingly verifyTestcaseSessionStatusAndUpdateAccordingly) {
        this.verifyTestcaseSessionStatusAndUpdateAccordingly = verifyTestcaseSessionStatusAndUpdateAccordingly;
    }

    @Autowired
    public void setStatusCallback(@Qualifier("statusCallbackHandler") StatusCallback statusCallback) {
        this.statusCallback = statusCallback;
    }

    @Autowired
    public void setTestResultRelationService(TestResultRelationService testResultRelationService) {
        this.testResultRelationService = testResultRelationService;
    }

    @Autowired
    public void setEuTestBedCommonUtil(EuTestBedCommonUtil euTestBedCommonUtil) {
        this.euTestBedCommonUtil = euTestBedCommonUtil;
    }
}
