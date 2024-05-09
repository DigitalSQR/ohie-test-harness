package eu.europa.ec.fhir.gitb;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Implementation of the GITB ApiCall API to handle HTTP call.
 */
@Component
public class ApiCallServiceImpl implements MessagingService {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ApiCallServiceImpl.class);

    @Resource
    private WebServiceContext wsContext;
    @Autowired
    private StateManager stateManager;
    @Autowired
    private FhirRESTUtils fhirRESTUtils;
    private RestTemplate restTemplate = new RestTemplate();
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

        //HttpMethod Type
        String method = utils.getRequiredString(sendRequest.getInput(), "httpMethod");
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        if (httpMethod == null) {
            throw new IllegalArgumentException("Invalid HTTP method: " + method);
        }

        //URL
        String url = utils.getRequiredString(sendRequest.getInput(), "url");

        //Media Type
        var mediaType = MediaType.APPLICATION_JSON_VALUE;
        var mediaTypeOptional = utils.getOptionalString(sendRequest.getInput(), "mediaType");
        if (mediaTypeOptional.isPresent()) {
            mediaType = mediaTypeOptional.get();
        }

        //Request Body
        String requestBody = null;
        Optional<String> requestBodyOptional = utils.getOptionalString(sendRequest.getInput(), "body");
        if (requestBodyOptional.isPresent()) {
            requestBody = requestBodyOptional.get();
        }

        //HTTP Headers parameters
        String headers = null;
        Optional<String> headersOptional = utils.getOptionalString(sendRequest.getInput(), "header");
        if (headersOptional.isPresent()) {
            headers = headersOptional.get();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null && !headers.isEmpty()) {
            // Parse headers string and set individual headers
            String[] headerEntries = headers.split(";");
            for (String headerEntry : headerEntries) {
                String[] parts = headerEntry.split(":");
                if (parts.length == 2) {
                    httpHeaders.add(parts[0].trim(), parts[1].trim());
                }
            }
        }

        //Get expected response type
        var responseTypeOptional = utils.getOptionalString(sendRequest.getInput(), "responseType");
        var responseType = JsonStringConverterServiceImpl.ANY_CONTENT;
        if (responseTypeOptional.isPresent()) {
            responseType = responseTypeOptional.get();
        }

        //create HTTP Request
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);

        //Make API call and get response
        var result = restTemplate.exchange(url, httpMethod, requestEntity, String.class);

        LOG.info("Response of the 'post' from test session [{}] and response is [{}].", sendRequest.getSessionId(), result);

        //Create report for this process and create response for ITB.
        var report = utils.createReport(TestResultType.SUCCESS);
        utils.addCommonReportData(report, url, requestBody, result, responseType);
        SendResponse response = new SendResponse();
        response.setReport(report);
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
