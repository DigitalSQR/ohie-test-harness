package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.DateClientParam;
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
 * Implementation of CRWF4TestCase1.
 *
 * @author Dhruv
 */
@Component
public class CRWF4TestCase1 implements TestCase {

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            List<String> patientIds = new ArrayList<>();
            // Create a new patient resource with all demographic information
            Patient patient1 = FHIRUtils.createPatient("John", "Doe", "M", "1990-01-01", "00006", "555-555-5555", "john.doe@example.com");

            MethodOutcome outcome = client.create()
                    .resource(patient1)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, "Failed to create patient");
            }
            patientIds.add(outcome.getResource().getIdElement().getIdPart());

            Patient patient2 = FHIRUtils.createPatient("Jane", "Doe", "F", "1992-02-02", "00007", "555-555-1234", "jane.doe@example.com");

            outcome = client.create()
                    .resource(patient2)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, "Failed to create patient");
            }
            patientIds.add(outcome.getResource().getIdElement().getIdPart());

            //Verify patient by demographics
            Bundle bundle = client.search()
                    .forResource(Patient.class)
                    .where(new DateClientParam("birthdate").afterOrEquals().day("1990-01-01")) // Replace with the actual start date
                    .where(new DateClientParam("birthdate").beforeOrEquals().day("1992-02-02")) // Replace with the actual end date
                    .returnBundle(Bundle.class)
                    .execute();
            List<Patient> patients = FHIRUtils.processBundle(Patient.class, bundle);

            if (patients.size() != 2) {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, "Failed to search patients by demographics");
            }

            //Verify patient by demographics and IDs
            bundle = client.search()
                    .forResource(Patient.class)
                    .where(Patient.RES_ID.exactly().codes(patientIds))
                    .where(new DateClientParam("birthdate").afterOrEquals().day("1990-01-01")) // Replace with the actual start date
                    .where(new DateClientParam("birthdate").beforeOrEquals().day("1992-02-01")) // Replace with the actual end date
                    .returnBundle(Bundle.class)
                    .execute();

            patients = FHIRUtils.processBundle(Patient.class, bundle);

            if (patients.size() == 1) {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.OK, "Passed");
            } else {
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, "Failed to search patients by Identifiers and demographics");
            }
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
