package eu.europa.ec.fhir.gitb;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.gitb.ms.Void;
import com.gitb.ms.*;
import com.gitb.tr.TestResultType;
import eu.europa.ec.fhir.state.StateManager;
import eu.europa.ec.fhir.utils.FhirRESTUtils;
import eu.europa.ec.fhir.utils.Utils;
import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of the GITB FhirContext API to handle IGeneric client related operations.
 */
@Component
public class FhirContextServiceImpl implements MessagingService {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FhirContextServiceImpl.class);

    @Resource
    private WebServiceContext wsContext;
    @Autowired
    private StateManager stateManager;
    @Autowired
    private FhirRESTUtils fhirRESTUtils;
    @Autowired
    private Utils utils;

    /**
     * This method normally returns documentation on how the service is expected to be used. It is meaningful
     * to implement this if this service would be a published utility that other test developers would want to
     * query and reuse. As it is an internal service we can skip this and return an empty implementation.
     *
     * @param aVoid No parameters.
     * @return The result.
     */
    @Override
    public GetModuleDefinitionResponse getModuleDefinition(Void aVoid) {
        return new GetModuleDefinitionResponse();
    }

    /**
     * Called when a new test session is about to start.
     * <p/>
     * A typical task here is to record the session identifier in the list of active sessions (in case the service
     * needs to manage state across calls). This service also returns a unique identifier as part of the response that
     * the Test Bed will signal back in calls taking place within the same test session. If we don't generate and return
     * such an identifier, the Test Bed's own session identifier will be used (this is useful to cross-check potential
     * issues in test sessions).
     *
     * @param initiateRequest The call's request (we typically don't need to process any of its information).
     * @return The response.
     */
    @Override
    public InitiateResponse initiate(InitiateRequest initiateRequest) {
        /*
         * The session identifier is extracted here from the SOAP headers. In subsequent calls to other operations,
         * this identifier will be directly included in the calls' parameters.
         */
        var sessionId = utils.getTestSessionIdFromHeaders(wsContext).orElseThrow();
        LOG.info("Initiating new test session [{}].", sessionId);
        stateManager.recordSession(sessionId);
        return new InitiateResponse();
    }

    /**
     * Called when a "send" step is executed.
     * <p/>
     * This method is expected to retrieve inputs, trigger whatever processing is needed, and return a synchronous report.
     *
     * @param sendRequest The request's parameters.
     * @return The response.
     */
    @Override
    public SendResponse send(SendRequest sendRequest) {
        // We can access the test session ID from the request's parameters.
        LOG.info("Called 'send' from test session [{}].", sendRequest.getSessionId());

        // Get Type of the operation Create/update/delete/search/getById/History...
        var type = utils.getRequiredString(sendRequest.getInput(), "operationType");

        // Get FHIR Server Base URL of the operation
        var serverBaseURL = utils.getRequiredString(sendRequest.getInput(), "fhirServerBaseUrl");

        // Create context for the FhirContext
        FhirContext context;
        FhirVersionEnum fhirContextType = FhirVersionEnum.R4;
        var fhirContextTypeOptional = utils.getOptionalString(sendRequest.getInput(), "fhirVersion");
        if (fhirContextTypeOptional.isPresent()) {
            fhirContextType = FhirVersionEnum.forVersionString(fhirContextTypeOptional.get());
        }
        context = FhirContext.forCached(fhirContextType);

        // Add connectTimeout
        var connectTimeoutOptional = utils.getOptionalString(sendRequest.getInput(), "connectTimeout");
        Integer connectTimeout = 60000;
        if (connectTimeoutOptional.isPresent()) {
            try {
                connectTimeout = Integer.parseInt(connectTimeoutOptional.get());
            } catch (NumberFormatException ex) {
            }
        }
        context.getRestfulClientFactory().setConnectTimeout(connectTimeout);

        // Add socketTimeout
        var socketTimeoutOptional = utils.getOptionalString(sendRequest.getInput(), "socketTimeout");
        Integer socketTimeout = 60000;
        if (socketTimeoutOptional.isPresent()) {
            try {
                socketTimeout = Integer.parseInt(socketTimeoutOptional.get());
            } catch (NumberFormatException ex) {
            }
        }
        context.getRestfulClientFactory().setSocketTimeout(socketTimeout);

        // Get UserName of the operation
        var userNameOptional = utils.getOptionalString(sendRequest.getInput(), "userName");

        // Get URL of the operation
        var passwordOptional = utils.getOptionalString(sendRequest.getInput(), "password");

        // Get URL of the operation
        var tokenOptional = utils.getOptionalString(sendRequest.getInput(), "token");

        //Create IGenericClient based on context for the serverBaseURL
        IGenericClient client = context.newRestfulGenericClient(serverBaseURL);

        //Handle security for the FHIR server
        if (userNameOptional.isPresent() && passwordOptional.isPresent()) {
            client.registerInterceptor(new BasicAuthInterceptor(userNameOptional.get(), passwordOptional.get()));
        } else if (tokenOptional.isPresent()) {
            client.registerInterceptor(new BearerTokenAuthInterceptor(tokenOptional.get()));
        }

        //Adding interceptor to Log details of HTTP requests and responses made by the FHIR client
        client.registerInterceptor(new LoggingInterceptor());

        SendResponse response = new SendResponse();
        switch (type) {
            case "create" -> {
                //Get body type: anyContent/jsonString
                var bodyTypeOptional = utils.getOptionalString(sendRequest.getInput(), "bodyType");
                var bodyType = JsonStringConverterServiceImpl.ANY_CONTENT;
                if (bodyTypeOptional.isPresent()) {
                    bodyType = bodyTypeOptional.get();
                }

                //Get body as jsonString
                var body = "";
                if (JsonStringConverterServiceImpl.JSON_STRING.equals(bodyType)) {
                    body = utils.getRequiredString(sendRequest.getInput(), "body");
                } else {
                    var jsonString = utils.getJsonElementForAnyConnect(utils.getRequiredAnyContent(sendRequest.getInput(), "body")).toString();
                    int indexOfBody = jsonString.indexOf("\"body\":");
                    int lastIndex = jsonString.lastIndexOf("}");
                    body = jsonString.substring(indexOfBody + "\"body\":".length(), lastIndex);
                }

                //Get expected response type
                var responseTypeOptional = utils.getOptionalString(sendRequest.getInput(), "responseType");
                var responseType = JsonStringConverterServiceImpl.ANY_CONTENT;
                if (responseTypeOptional.isPresent()) {
                    responseType = responseTypeOptional.get();
                }

                LOG.info("Request of the 'create' from test session [{}] and bodyType: [{}], body: [{}]", sendRequest.getSessionId(), bodyType, body);

                //Create resource based on the jsonString body
                var resource = fhirRESTUtils.createResource(context, body);
                //Do the operation and get the response
                var result = fhirRESTUtils.create(client, context, resource);

                LOG.info("Response of the 'create' from test session [{}] and responseType: [{}], responseBody: [{}].", sendRequest.getSessionId(), responseType, result.methodOutcomeBody());

                //Create response for the ITB
                var report = utils.createReport(TestResultType.SUCCESS);
                utils.addCommonReportData(report, serverBaseURL, body, result, responseType);
                response.setReport(report);
            }
            case "update" -> {
                //Get body type: anyContent/jsonString
                var bodyTypeOptional = utils.getOptionalString(sendRequest.getInput(), "bodyType");
                var bodyType = JsonStringConverterServiceImpl.ANY_CONTENT;
                if (bodyTypeOptional.isPresent()) {
                    bodyType = bodyTypeOptional.get();
                }

                //Get body as jsonString
                var body = "";
                if (JsonStringConverterServiceImpl.JSON_STRING.equals(bodyType)) {
                    body = utils.getRequiredString(sendRequest.getInput(), "body");
                } else {
                    var jsonString = utils.getJsonElementForAnyConnect(utils.getRequiredAnyContent(sendRequest.getInput(), "body")).toString();
                    int indexOfBody = jsonString.indexOf("\"body\":");
                    int lastIndex = jsonString.lastIndexOf("}");
                    body = jsonString.substring(indexOfBody + "\"body\":".length(), lastIndex);
                }

                //Get expected response type
                var responseTypeOptional = utils.getOptionalString(sendRequest.getInput(), "responseType");
                var responseType = JsonStringConverterServiceImpl.ANY_CONTENT;
                if (responseTypeOptional.isPresent()) {
                    responseType = responseTypeOptional.get();
                }

                LOG.info("Request of the 'update' from test session [{}] and bodyType: [{}], body: [{}]", sendRequest.getSessionId(), bodyType, body);

                //Create resource based on the jsonString body
                var resource = fhirRESTUtils.createResource(context, body);
                //Do the operation and get the response
                var result = fhirRESTUtils.update(client, context, resource);

                LOG.info("Response of the 'update' from test session [{}] and responseType: [{}], responseBody: [{}].", sendRequest.getSessionId(), responseType, result.methodOutcomeBody());

                //Create response for the ITB
                var report = utils.createReport(TestResultType.SUCCESS);
                utils.addCommonReportData(report, serverBaseURL, body, result, responseType);
                response.setReport(report);
            }
            case "delete" -> {

                //Get expected response type
                var responseTypeOptional = utils.getOptionalString(sendRequest.getInput(), "responseType");
                var responseType = JsonStringConverterServiceImpl.ANY_CONTENT;
                if (responseTypeOptional.isPresent()) {
                    responseType = responseTypeOptional.get();
                }

                //Get fhirResourceType which needs to be deleted
                var resourceType = utils.getRequiredString(sendRequest.getInput(), "fhirResourceType");

                //Get Id of the resource which needs to be deleted
                var resourceId = utils.getRequiredString(sendRequest.getInput(), "fhirResourceId");

                LOG.info("Request of the 'delete' from test session [{}] responseType: [{}], resourceId: [{}]", sendRequest.getSessionId(), resourceType, resourceId);

                //Do the operation and get the response
                var result = fhirRESTUtils.delete(client, context, resourceType, resourceId);

                LOG.info("Response of the 'delete' from test session [{}] and responseType: [{}], responseBody: [{}].", sendRequest.getSessionId(), responseType, result.methodOutcomeBody());

                //Create response for the ITB
                var report = utils.createReport(TestResultType.SUCCESS);
                utils.addCommonReportData(report, serverBaseURL, null, result, responseType);
                response.setReport(report);
            }
            case "search" -> {

                //Get expected response type
                var responseTypeOptional = utils.getOptionalString(sendRequest.getInput(), "responseType");
                var responseType = JsonStringConverterServiceImpl.ANY_CONTENT;
                if (responseTypeOptional.isPresent()) {
                    responseType = responseTypeOptional.get();
                }

                //Get fhirResourceType which needs to be fetched
                var resourceType = utils.getRequiredString(sendRequest.getInput(), "fhirResourceType");

                //Get search criteria filter parameters
                var parametersAsString = utils.getRequiredString(sendRequest.getInput(), "parameters");

                LOG.info("Request of the 'search' from test session [{}] and responseType: [{}], parameters: [{}]", sendRequest.getSessionId(), responseType, parametersAsString);

                //Do the operation and get the response
                var result = fhirRESTUtils.search(client, resourceType, parametersAsString);

                LOG.info("Response of the 'search' from test session [{}] and responseType: [{}], responseBody: [{}].", sendRequest.getSessionId(), responseType, result.methodOutcomeBody());

                //Create response for the ITB
                var report = utils.createReport(TestResultType.SUCCESS);
                utils.addCommonReportData(report, serverBaseURL, null, result, responseType);
                response.setReport(report);
            }
            case "getById" -> {

                //Get expected response type
                var responseTypeOptional = utils.getOptionalString(sendRequest.getInput(), "responseType");
                var responseType = JsonStringConverterServiceImpl.ANY_CONTENT;
                if (responseTypeOptional.isPresent()) {
                    responseType = responseTypeOptional.get();
                }

                //Get fhirResourceType which needs to be fetched
                var resourceType = utils.getRequiredString(sendRequest.getInput(), "fhirResourceType");

                //Get resource Id
                var resourceId = utils.getRequiredString(sendRequest.getInput(), "fhirResourceId");

                LOG.info("Request of the 'search' from test session [{}] and responseType: [{}], responseId: [{}]", sendRequest.getSessionId(), responseType, resourceId);

                //Do the operation and get the response
                var result = fhirRESTUtils.getById(client, resourceType, resourceId);

                LOG.info("Response of the 'search' from test session [{}] and responseType: [{}], responseBody: [{}].", sendRequest.getSessionId(), responseType, result.methodOutcomeBody());

                //Create response for the ITB
                var report = utils.createReport(TestResultType.SUCCESS);
                utils.addCommonReportData(report, serverBaseURL, null, result, responseType);
                response.setReport(report);
            }
            case "getHistoryById" -> {

                //Get expected response type
                var responseTypeOptional = utils.getOptionalString(sendRequest.getInput(), "responseType");
                var responseType = JsonStringConverterServiceImpl.ANY_CONTENT;
                if (responseTypeOptional.isPresent()) {
                    responseType = responseTypeOptional.get();
                }

                //Get fhirResourceType which needs to be fetched
                var resourceType = utils.getRequiredString(sendRequest.getInput(), "fhirResourceType");

                //Get resource Id
                var resourceId = utils.getRequiredString(sendRequest.getInput(), "fhirResourceId");

                LOG.info("Request of the 'getHistoryById' from test session [{}] and responseType: [{}], responseId: [{}]", sendRequest.getSessionId(), resourceType, resourceId);

                //Do the operation and get the response
                var result = fhirRESTUtils.getHistoryById(client, resourceType, resourceId);

                LOG.info("Response of the 'getHistoryById' from test session [{}] and responseType: [{}], responseBody: [{}].", sendRequest.getSessionId(), responseType, result.methodOutcomeBody());

                //Create response for the ITB
                var report = utils.createReport(TestResultType.SUCCESS);
                utils.addCommonReportData(report, serverBaseURL, null, result, responseType);
                response.setReport(report);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported type [%s] for 'send' operation.".formatted(type));
        }
        return response;
    }

    /**
     * Called when a "receive" step is executed.
     * <p/>
     * We return from this method a synchronous response to the test session, however the
     * actual message for which we will complete the test session's receive step will be
     * received and handled asynchronously. The report for this message will be provided
     * through the Test Bed's callback API that is made available through the reply-to
     * SOAP header.
     *
     * @param receiveRequest The request's parameters.
     * @return An empty response (the eventual response message will come asynchronously).
     */
    @Override
    public Void receive(ReceiveRequest receiveRequest) {
        return new Void();
    }

    /**
     * Called when a transaction starts (if we use transactions in our test cases).
     * <p/>
     * As we don't use transactions we can keep this empty.
     *
     * @param beginTransactionRequest The request.
     * @return An empty response.
     */
    @Override
    public Void beginTransaction(BeginTransactionRequest beginTransactionRequest) {
        return new Void();
    }

    /**
     * Called when a transaction ends (if we use transactions in our test cases).
     * <p/>
     * As we don't use transactions we can keep this empty.
     *
     * @param basicRequest The request.
     * @return An empty response.
     */
    @Override
    public Void endTransaction(BasicRequest basicRequest) {
        return new Void();
    }

    /**
     * Called when a test session completes.
     * <p/>
     * This method is useful if you need to maintain any in-memory state for each test session. In our case we clear the
     * state for the current test session.
     *
     * @param finalizeRequest The request.
     * @return An empty response.
     */
    @Override
    public Void finalize(FinalizeRequest finalizeRequest) {
        LOG.info("Finalising test session [{}].", finalizeRequest.getSessionId());
        stateManager.destroySession(finalizeRequest.getSessionId());
        return new Void();
    }

}
