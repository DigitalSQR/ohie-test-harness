package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CRWF3TestCase1.
 *
 * @author Dhruv
 */
@Component
public class CRWF3TestCase1 implements TestCase {

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            List<String> patientIds = new ArrayList<>();
            // Create a new patient resource with all demographic information
            Patient patient1 = FHIRUtils.createPatient("John", "Doe", "M", "1990-01-01", "00004", "555-555-5555", "john.doe@example.com");

            MethodOutcome outcome = client.create()
                    .resource(patient1)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, "Failed to create patient");
            }
            patientIds.add(outcome.getResource().getIdElement().getIdPart());

            //verify patient by ID
            Patient createdPatient = client.read()
                    .resource(Patient.class)
                    .withId(outcome.getResource().getIdElement().getIdPart())
                    .execute();

            if (!patient1.getBirthDate().equals(createdPatient.getBirthDate())) {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, "Failed to get patient by ID");
            }

            //verify patients by IDs
            Patient patient2 = FHIRUtils.createPatient("Jane", "Doe", "F", "1992-02-02", "00005", "555-555-1234", "jane.doe@example.com");

            outcome = client.create()
                    .resource(patient2)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, "Failed to create patient");
            }
            patientIds.add(outcome.getResource().getIdElement().getIdPart());

            Bundle bundle = client.search()
                    .forResource(Patient.class)
                    .where(Patient.RES_ID.exactly().codes(patientIds))
                    .returnBundle(Bundle.class)
                    .execute();

            List<Patient> patients = FHIRUtils.processBundle(Patient.class, bundle);

            if (patients.size() == 2) {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.OK, "Passed");
            } else {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, "Failed to get patients by IDs");
            }

        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
