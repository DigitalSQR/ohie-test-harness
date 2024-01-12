package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;


@Component
public class CRF10TestCase2 implements TestCase {
    @Override
    public  ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) throws OperationFailedException
    {
        Patient patient = FHIRUtils.createPatient("John", "Doe", "M", "1990-01-01", "101", "555-555-5555", "john.doe@example.com");
        patient.setMultipleBirth(new BooleanType(true));
        patient.setMultipleBirth(new IntegerType(1));
        MethodOutcome outcome = client.create().resource(patient).execute();
        String patientId = outcome.getId().getIdPart();
        if(!outcome.getCreated())
        {
            return new ValidationResultInfo("testCRF10case2",ErrorLevel.ERROR,"Failed patient is not created");
        }
        Patient infant = client.read().resource(Patient.class).withId(patientId).execute();
        if(infant.hasMultipleBirth())
        {
            return new ValidationResultInfo("testCRF10case2", ErrorLevel.OK,"Passed");
        }
            return new ValidationResultInfo("testCRF10case2",ErrorLevel.ERROR,"Failed because test case has no multiple birth indicator");
    }


}
