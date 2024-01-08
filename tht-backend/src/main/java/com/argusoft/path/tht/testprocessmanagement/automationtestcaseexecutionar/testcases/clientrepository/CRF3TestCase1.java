package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;



@Component
public class CRF3TestCase1 implements TestCase{

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                             ContextInfo contextInfo) throws OperationFailedException {
        try {
            // Create Patient resource
            Patient newPatient = FHIRUtils.createPatient("John", "Doe", "M", "1990-01-01", "00002", "555-555-5555", "john.doe@example.com");

            // Create the Patient
            MethodOutcome patientOutcome = client.create()
                    .resource(newPatient)
                    .execute();

            // Retrieving the Patient's ID
            String PatientId = patientOutcome.getResource().getIdElement().getIdPart();

            // Searching for AuditEvent related to the created Patient
            Bundle auditEventBundle = (Bundle) client.search().forResource(AuditEvent.class)
                    .where(new ReferenceClientParam("patient").hasId(PatientId))
                    .prettyPrint()
                    .execute();

            if (auditEventBundle.hasEntry()) {

                // Reading Patient
                Patient retrievedPatient = client.read()
                        .resource(Patient.class)
                        .withId(PatientId)
                        .execute();

                // Searching for specific AuditEvent related to the Read Patient
                Bundle specificAuditEventBundle = (Bundle) client.search().forResource(AuditEvent.class)
                        .where(new ReferenceClientParam("patient").hasId(PatientId))
                        .where(new TokenClientParam("action").exactly().code("R"))
                        .prettyPrint().execute();

                if (specificAuditEventBundle.hasEntry()) {
                    return new ValidationResultInfo("testCRF3", ErrorLevel.OK, "Passed");
                } else {
                    return new ValidationResultInfo("testCRF3", ErrorLevel.ERROR, "Failed due to failure of outbound transaction");
                }
            } else {
                return new ValidationResultInfo("testCRF3", ErrorLevel.ERROR, "Failed due to failure of inbound transaction");
            }
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }

    }

}
