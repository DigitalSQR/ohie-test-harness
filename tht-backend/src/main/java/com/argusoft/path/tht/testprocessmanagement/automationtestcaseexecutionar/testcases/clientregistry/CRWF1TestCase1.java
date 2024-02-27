package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of the CRWF1TestCase1.
 *
 * @author Dhruv
 */
@Component
public class CRWF1TestCase1 {

    public static final Logger LOGGER = LoggerFactory.getLogger(CRWF1TestCase1.class);

    public static ValidationResultInfo test(IGenericClient client) throws OperationFailedException {
        try {


            LOGGER.info("Creating patient");
            // Create a new patient resource with all demographic information
            Patient patient = FHIRUtils.createPatient("MOHR", "ALISSA", "female", "1958-01-30",
                    "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "alissa.mohr@example.com", client);

            MethodOutcome outcome = client.create()
                    .resource(patient)
                    .execute();

            // Check if the patient was created successfully
            if (outcome.getCreated()) {
                LOGGER.info("Testcase successfully passed!");
                return new ValidationResultInfo(ErrorLevel.OK, "Passed");
            } else {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create patient");
            }

        } catch (Exception ex) {
            LOGGER.error("Exception while CRWF1TestCase1 ", ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) throws OperationFailedException {
        IGenericClient client = getClient("R4","http://hapi.fhir.org/baseR4","root@intrahealth.com","intrahealth");
        System.out.println(test(client));
    }
    public static IGenericClient getClient(String contextType, String serverBaseURL, String username, String password) {
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
