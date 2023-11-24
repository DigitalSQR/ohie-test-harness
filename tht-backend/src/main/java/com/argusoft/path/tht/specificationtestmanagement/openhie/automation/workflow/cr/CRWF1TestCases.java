package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.RelatedPerson;

import java.util.ArrayList;
import java.util.List;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow-1.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF1TestCases {

    public static ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) {
        System.out.println("Started CRWF1TestCases");
        //code to add entry that started process for CRWF1 testing.
        List<ValidationResultInfo> allTestCasesResults = new ArrayList<>();
        allTestCasesResults.add(CRWF1TestCases.testCRWF1Case1(client, contextInfo));
        allTestCasesResults.add(CRWF1TestCases.testCRWF1Case2(client, contextInfo));
        //make entry for whole CRWF1 and return response.
        if (ValidationUtils.containsErrors(allTestCasesResults, ErrorLevel.ERROR)) {
            return new ValidationResultInfo("testCRWF1", ErrorLevel.OK, "Failed");
        } else {
            return new ValidationResultInfo("testCRWF1", ErrorLevel.OK, "Passed");
        }
    }

    private static ValidationResultInfo testCRWF1Case1(IGenericClient client, ContextInfo contextInfo) {
        System.out.println("Started testCRWF1Case1");
        System.out.println("Finished testCRWF1Case1");
        return new ValidationResultInfo("testCRWF1Case1", ErrorLevel.OK, "Passed");
    }

    private static ValidationResultInfo testCRWF1Case2(IGenericClient client, ContextInfo contextInfo) {
        System.out.println("Started testCRWF1Case2");
        Patient patient = new Patient();
        //Create Mock data for the patient
        MethodOutcome outcome = client.create()
                .resource(patient)
                .execute();
        RelatedPerson relatedPerson = new RelatedPerson();
//        relatedPerson.setPatient()
        Communication communication = new Communication();
        System.out.println("=>" + outcome.getId());
        if (Boolean.FALSE.equals(outcome.getCreated())) {
            //add entry for failure testcase.
            return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, "Not able to create patient");
        }
        //Standard: The workflow will not register a duplicate patent if the patient already exists in the Client Registry.
        outcome = client.create()
                .resource(patient)
                .execute();
        System.out.println("Finished testCRWF1Case2");
        //add entry for separate testcase.
        return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.OK, "Passed");
    }
}
