package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the CRWF1 Testcase2 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow-1.
 *
 * @author dhruv
 * @since 2023-09-25
 */
@Component
public class CRWF1TestCase2 implements TestCase {

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            // Create a new patient resource with all demographic information
            Patient patient = FHIRUtils.createPatient("John", "Doe", "M", "1990-01-01", "00002", "555-555-5555", "john.doe@example.com");

            MethodOutcome outcome = client.create()
                    .resource(patient)
                    .execute();

            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, "Failed to create patient");
            }

            String patientId = outcome.getResource().getIdElement().getIdPart();
            patient = client.read()
                    .resource(Patient.class)
                    .withId(patientId)
                    .execute();

            // Create a new patient resource with all demographic information
            outcome = client.create()
                    .resource(patient)
                    .execute();

            // Check if the patient was created twice?
            if (outcome.getCreated()) {
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, "Was able to create duplicate patient");
            } else {
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.OK, "Passed");
            }
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
