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
 * Implementation of the CRWF1TestCase2.
 *
 * @author Dhruv
 */
@Component
public class CRWF1TestCase2 implements TestCase {

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_CLIENT_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            // Create a new patient resource with all demographic information
            Patient patient = FHIRUtils.createPatient("MOHR", "ALISSA", "female", "1958-01-30",
                    "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "alissa.mohr@example.com", client);

            MethodOutcome outcome = client.create()
                    .resource(patient)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, "Failed to create patient");
            }

            Patient patientForConflict = FHIRUtils.createPatient("MOHR", "MAIDEN", "female", "1958-01-30",
                    "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-m94", false, "IHERED-994", "555-555-5555", "maiden.mohr@example.com", client);

            outcome = client.create()
                    .resource(patientForConflict)
                    .execute();

            // Check if the patient was created twice?
            if (outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.OK, "Passed");
            } else {
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, "Was not able to resolve patient conflict via linking patient");
            }
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
