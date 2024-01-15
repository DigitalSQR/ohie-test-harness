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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CRF3TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(CRF3TestCase1.class);

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {

        try {
            LOGGER.info("Start testing CRF3TestCase1");

            // Create Patient resource
            Patient newPatient = FHIRUtils.createPatient("Doe", "John", "M", "1990-01-01", "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "john.doe@example.com", client);

            // Create the Patient
            MethodOutcome patientOutcome = client.create()
                    .resource(newPatient)
                    .execute();

            // Checking whether patient is created or not
            if (!patientOutcome.getCreated()) {
                return new ValidationResultInfo("testCRF3", ErrorLevel.ERROR, "Failed to create patient");
            }

            // Retrieving the Patient's ID
            String PatientId = patientOutcome.getResource().getIdElement().getIdPart();

            // Searching for AuditEvent related to the created Patient
            Bundle auditEventBundle = (Bundle) client.search().forResource(AuditEvent.class)
                    .where(new ReferenceClientParam("patient").hasId(PatientId))
                    .where(new TokenClientParam("action").exactly().code("C"))
                    .prettyPrint()
                    .execute();

            if (!auditEventBundle.hasEntry()) {
                return new ValidationResultInfo("testCRF3", ErrorLevel.ERROR, "Failed due to non presence of inbound transaction");
            }

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

            if (!specificAuditEventBundle.hasEntry()) {
                return new ValidationResultInfo("testCRF3", ErrorLevel.ERROR, "Failed due to non presence of outbound transaction");
            }

            return new ValidationResultInfo("testCRF3", ErrorLevel.OK, "Passed");
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
