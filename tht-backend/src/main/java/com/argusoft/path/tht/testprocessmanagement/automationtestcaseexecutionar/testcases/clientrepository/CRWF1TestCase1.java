package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of the CRWF1TestCase1.
 *
 * @author Dhruv
 */
@Component
public class CRWF1TestCase1 implements TestCase {

    public static final Logger LOGGER =  LoggerFactory.getLogger(CRWF1TestCase1.class);

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            LOGGER.info("Start testing CRWF1TestCase1");

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
                return new ValidationResultInfo("testCRWF1Case1", ErrorLevel.OK, "Passed");
            } else {
                LOGGER.error("Testcase Failed");
                return new ValidationResultInfo("testCRWF1Case1", ErrorLevel.ERROR, "Failed to create patient");
            }

        } catch (Exception ex) {
            LOGGER.error("Exception while CRWF1TestCase1 ",ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
