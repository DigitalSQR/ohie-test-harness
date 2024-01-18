package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

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
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of CRWF2TestCase1.
 *
 * @author Dhruv
 */
@Component
public class CRWF2TestCase1 implements TestCase {

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_CLIENT_REGISTRY_ID);
            if(client == null) {
                return new ValidationResultInfo("testCRWF2Case1", ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            // Create a new patient resource with all demographic information
            Patient patient = FHIRUtils.createPatient("MOHR", "ALISSA", "female", "1958-01-30",
                    "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "alissa.mohr@example.com", client);

            MethodOutcome outcome = client.create()
                    .resource(patient)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF2Case1", ErrorLevel.ERROR, "Failed to create patient");
            }

            String patientId = outcome.getResource().getIdElement().getIdPart();

            patient = client.read()
                    .resource(Patient.class)
                    .withId(patientId)
                    .execute();

            patient.getName().get(0).getGiven().get(0).setValue("ALICE");

            //update patient data.
            outcome = client.update()
                    .resource(patient)
                    .execute();

            if (!patientId.equals(outcome.getResource().getIdElement().getIdPart())) {
                return new ValidationResultInfo("testCRWF2Case1", ErrorLevel.ERROR, "Instead of Update, Server has created new Patient");
            }

            patient = client.read()
                    .resource(Patient.class)
                    .withId(patientId)
                    .execute();

            //check if patient got updated or not
            if (patient.getName().get(0).getGiven().get(0).getValue().equals("ALICE")) {
                return new ValidationResultInfo("testCRWF2Case1", ErrorLevel.OK, "Passed");
            } else {
                return new ValidationResultInfo("testCRWF2Case1", ErrorLevel.ERROR, "Failed to update patient");
            }
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
