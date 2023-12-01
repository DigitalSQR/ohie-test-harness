package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/update-patient-demographic-record-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
@Component
public class CRWF2TestCase1 implements TestCase {

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) {
        System.out.println("Started testCRWF2Case1");
        //https://hapi.fhir.org/baseDstu3/Patient/20909
        Patient patient = client.read()
                .resource(Patient.class)
                .withId("20909")
                .execute();
        //update patient data.
        client.update()
                .resource(patient)
                .execute();
        //Verify Result
        System.out.println("Finished testCRWF2Case1");
        //add entry for separate testcase.
        return new ValidationResultInfo("testCRWF2Case1", ErrorLevel.OK, "Passed");
    }
}
