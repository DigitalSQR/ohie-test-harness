package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.context.FhirContext;
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

import java.util.Collections;

@Component
public class CRF10TestCase1 implements TestCase {
    @Override
    public ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) throws OperationFailedException {
        try {

            Patient patient = FHIRUtils.createPatient("John", "Doe", "M", "1990-01-01", "100", "555-555-5555", "john.doe@example.com");

            patient.setBirthDateElement(new DateType("2001-01-05"
            ));
            patient.addContact()
                    .setRelationship(Collections.singletonList(new CodeableConcept().setText("mother")))
                    .setName(new HumanName().addGiven("Clarke").setFamily("Doe")).addTelecom(new ContactPoint().
                            setSystem(ContactPoint.ContactPointSystem.PHONE).setValue("937-439-343"));

            MethodOutcome outcome = client.create().resource(patient).execute();
            String patientId = outcome.getId().getIdPart();
            if (!outcome.getCreated()) {
                return new ValidationResultInfo("testCRF10case1", ErrorLevel.ERROR, "Failed to create patient");
            }
            Patient infant = client.read().resource(Patient.class).withId(patientId).execute();
                if (!infant.hasContact()) {
                    return new ValidationResultInfo("testCRF10case1",ErrorLevel.ERROR,"Failed because test case has no contact");
                }
                Patient.ContactComponent contact = infant.getContactFirstRep();
                if (contact.hasRelationship() && contact.getRelationship().stream()
                        .anyMatch(codeableConcept -> "mother".equals(codeableConcept.getText()))) {
                    return new ValidationResultInfo("testCRF10case1",ErrorLevel.OK,"Passed");
                }
                return new ValidationResultInfo("testCRF10case1",ErrorLevel.ERROR,"Failed because test case has no relationship ");




        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }

    }
}
