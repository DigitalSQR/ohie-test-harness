package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CRF10TestCase2 implements TestCase {

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) throws OperationFailedException {
        IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_CLIENT_REGISTRY_ID);
        if (client == null) {
            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
        }

        Patient patient = FHIRUtils.createPatient("Doe", "John", "male", "2001-01-05", "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "john.doe@example.com", client);
        patient.setMultipleBirth(new BooleanType(true));
        patient.setMultipleBirth(new IntegerType(1));

        MethodOutcome outcome = client.create().resource(patient).execute();
        String patientId = outcome.getId().getIdPart();
        if (!outcome.getCreated()) {
            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create patient");
        }
        Patient infant = client.read().resource(Patient.class).withId(patientId).execute();
        if (!infant.hasMultipleBirth()) {
            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to store patient with the multiple birth indicator");
        }
        return new ValidationResultInfo(ErrorLevel.OK, "Passed");
    }
}
