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
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/query-patient-demographic-records-by-identifier-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
@Component
public class CRWF3TestCase1 implements TestCase {

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) {
        System.out.println("Started testCRWF3Case1");
        //read by id
        //https://hapi.fhir.org/baseDstu3/Patient/20909
        Patient patient = client.read()
                .resource(Patient.class)
                .withId("20909")
                .execute();
        System.out.println("=>" + patient);
        System.out.println("=>" + patient.getName());
        System.out.println("Finished testCRWF3Case1");
        //add entry for separate testcase.
        return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.OK, "Passed");
    }
}
