package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.sharedhealthrecord;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SHRF8TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(SHRF8TestCase1.class);
    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) throws OperationFailedException {
        try {

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            // Create a new patient resource with all demographic information
            Patient newPatient = FHIRUtils.createPatient("John", "Doe", "M", "1990-01-01", "00002", "555-555-5555", "john.doe@example.com");

            MethodOutcome outcome = client.create()
                    .resource(newPatient)
                    .execute();


            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create patient");
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
                    return new ValidationResultInfo(ErrorLevel.ERROR, "Patient resource is incomplete or missing required fields.");
                }
            } else {
                // Handle cases where the resource is not a FHIR Patient
                return new ValidationResultInfo(ErrorLevel.ERROR, "Resource is not a FHIR Patient Resource");
            }
            // Return patient to be a FHIR Patient resource
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");

        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + SHRF8TestCase1.class.getSimpleName(), e);
            return new ValidationResultInfo(ErrorLevel.ERROR, "OPERATION FAILED");
        }
    }


}
