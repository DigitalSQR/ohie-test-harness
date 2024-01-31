package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Assert;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class PatientSteps {

    public static final Logger LOGGER = LoggerFactory.getLogger(PatientSteps.class);

    private String serverBaseURL;
    private String fhirJsonString;
    private MethodOutcome outcome;

    private IGenericClient getClient(String contextType, String serverBaseURL, String username, String password) throws OperationFailedException {
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
//        client.registerInterceptor(new BasicAuthInterceptor(username, password));

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

    @Given("Client Repository URL {string}")
    public void setBaseUrl(String serverBaseURL) {
        this.serverBaseURL = serverBaseURL;
    }

    @Given("Patient data")
    public void setPatientData(String fhirJsonString) {
        this.fhirJsonString = fhirJsonString;
    }

    @When("The POS sends the Patient")
    public void sendPatientRequest() throws Exception {
        try {
            FhirContext fhirContext = FhirContext.forR4();
            IParser parser = fhirContext.newJsonParser();
            Patient patient = parser.parseResource(Patient.class, fhirJsonString);

            IGenericClient iGenericClient = getClient("R4", "https://hapi.fhir.org/baseR4", null, null);

            outcome = iGenericClient.create()
                    .resource(patient)
                    .execute();
        } catch (Exception ex) {
//            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }

    @Then("Then The added Patient Location and CRID are returned")
    public void verifyStatusCode(int expectedStatusCode) {
        try {
            Assert.assertNotNull(outcome);
            Assert.assertFalse(outcome.getCreated());
        } catch (AssertionError e) {
            String failureReason = "Not able to create patient";
            throw new AssertionError(failureReason, e);
        }
    }
}