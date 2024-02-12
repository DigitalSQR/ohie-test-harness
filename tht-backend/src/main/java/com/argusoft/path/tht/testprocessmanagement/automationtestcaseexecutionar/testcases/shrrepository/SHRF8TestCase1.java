package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.shrrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SHRF8TestCase1 implements TestCase {

    @Override
    public ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) throws OperationFailedException {
        try {

            // Create a new patient resource with all demographic information
            Patient newPatient = FHIRUtils.createPatient("John", "Doe", "M", "1990-01-01", "00002", "555-555-5555", "john.doe@example.com");

            MethodOutcome outcome = client.create()
                    .resource(newPatient)
                    .execute();


            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testSHRF8TestCase1", ErrorLevel.ERROR, "Failed to create patient");
            }

            String tempPatientId = outcome.getResource().getIdElement().getIdPart();
            Resource resource = client.read()
                    .resource(Patient.class)
                    .withId(tempPatientId)
                    .execute();

            // Validate if the resource is a FHIR Patient
            if (resource instanceof Patient patient) {


                List<ContactPoint> telecomList = patient.getTelecom();
                boolean phoneNumberFound = false;
                boolean emailFound = false;

                for (ContactPoint contact : telecomList) {
                    if (contact.hasSystem() && contact.getSystem().equals(ContactPoint.ContactPointSystem.PHONE)) {
                        phoneNumberFound = true;
                    }
                    if (contact.hasSystem() && contact.getSystem().equals(ContactPoint.ContactPointSystem.EMAIL)) {
                        emailFound = true;
                    }
                }

                // Validate specific fields of the Patient resource
                if (patient.hasName() && patient.hasBirthDate() && patient.hasGender() && phoneNumberFound && emailFound) {

                    // Here, 'patient' is a valid FHIR Patient resource with expected fields

                } else {
                    // Handle case where Patient resource is missing required fields
                    return new ValidationResultInfo("testSHRF8TestCase1", ErrorLevel.ERROR, "Patient resource is incomplete or missing required fields.");
                }
            } else {
                // Handle cases where the resource is not a FHIR Patient
                return new ValidationResultInfo("testSHRF8TestCase1", ErrorLevel.ERROR, "Resource is not a FHIR Patient Resource");
            }
            // Return patient to be a FHIR Patient resource
            return new ValidationResultInfo("testSHRF8TestCase1", ErrorLevel.OK, "Passed");

        } catch (Exception e) {
            return new ValidationResultInfo("testSHRF8TestCase1", ErrorLevel.ERROR, "OPERATION FAILED");
        }
    }


}
