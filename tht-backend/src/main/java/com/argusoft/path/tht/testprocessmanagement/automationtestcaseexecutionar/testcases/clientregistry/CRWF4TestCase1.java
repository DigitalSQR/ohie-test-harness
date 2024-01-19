package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.DateClientParam;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of CRWF4TestCase1.
 *
 * @author Dhruv
 */
@Component
public class CRWF4TestCase1 implements TestCase {

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_CLIENT_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo("testCRWF4Case1", ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            List<String> patientIds = new ArrayList<>();
            // Create a new patient resource with all demographic information
            Patient patient1 = FHIRUtils.createPatient("MOHR", "ALISSA", "female", "1958-01-30",
                    "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "alissa.mohr@example.com", client);

            MethodOutcome outcome = client.create()
                    .resource(patient1)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF4Case1", ErrorLevel.ERROR, "Failed to create patient");
            }
            patientIds.add(outcome.getResource().getIdElement().getIdPart());

            Patient patient2 = FHIRUtils.createPatient("MOHR", "ALICE", "male", "1958-01-30",
                    "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-995", true, "", "666-666-6666", "alice.mohr@example.com", client);

            outcome = client.create()
                    .resource(patient2)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF4Case1", ErrorLevel.ERROR, "Failed to create patient");
            }
            patientIds.add(outcome.getResource().getIdElement().getIdPart());

            //Verify patient by demographics
            Bundle bundle = client.search()
                    .forResource(Patient.class)
                    .where(Patient.RES_ID.exactly().codes(patientIds))
                    .where(new DateClientParam("birthdate").afterOrEquals().day("1958-01-29")) // Replace with the actual start date
                    .where(new DateClientParam("birthdate").beforeOrEquals().day("1958-02-01")) // Replace with the actual end date
                    .returnBundle(Bundle.class)
                    .execute();
            List<Patient> patients = FHIRUtils.processBundle(Patient.class, bundle);

            if (patients.size() != 2) {
                return new ValidationResultInfo("testCRWF4Case1", ErrorLevel.ERROR, "Failed to search patients by demographics");
            }
            return new ValidationResultInfo("testCRWF4Case1", ErrorLevel.OK, "Passed");
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
