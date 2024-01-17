package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CRF10TestCase1 implements TestCase {
    @Override
    public ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) throws OperationFailedException {
        try {
            Patient patient = FHIRUtils.createPatient("Doe", "John", "male", "2001-01-05", "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "john.doe@example.com", client);
            patient.addContact()
                    .setRelationship(Collections.singletonList(new CodeableConcept().setText("mother")))
                    .setName(new HumanName().addGiven("Clarke").setFamily("Doe")).addTelecom(new ContactPoint().
                            setSystem(ContactPoint.ContactPointSystem.PHONE).setValue("937-439-343"));

            MethodOutcome outcome = client.create().resource(patient).execute();
            String patientId = outcome.getId().getIdPart();
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRF10case1", ErrorLevel.ERROR, "Failed to create patient with related person");
            }
            Patient infant = client.read().resource(Patient.class).withId(patientId).execute();

            if (!infant.hasContact()) {
                return new ValidationResultInfo("testCRF10case1", ErrorLevel.ERROR, "Failed to create patient with related person");
            }

            Patient.ContactComponent contact = infant.getContactFirstRep();
            if (contact.hasRelationship() && contact.getRelationship().stream()
                    .anyMatch(codeableConcept -> "mother".equals(codeableConcept.getText()))) {
                return new ValidationResultInfo("testCRF10case1", ErrorLevel.OK, "Passed");
            }

            return new ValidationResultInfo("testCRF10case1", ErrorLevel.ERROR, "Failed to create related person with given relationship ");
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
