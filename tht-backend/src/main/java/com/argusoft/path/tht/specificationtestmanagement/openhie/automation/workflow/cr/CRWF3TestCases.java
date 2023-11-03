package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/query-patient-demographic-records-by-identifier-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF3TestCases {

    public static ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) {
            System.out.println("Started CRWF3TestCases");
            //code to add entry that started process for CRWF3 testing
            List<ValidationResultInfo> allTestCasesResults = new ArrayList<>();
            allTestCasesResults.add(CRWF3TestCases.testCRWF3Case1(client, contextInfo));

            //make entry for whole CRWF3 and return response.
            if (ValidationUtils.containsErrors(allTestCasesResults, ErrorLevel.ERROR)) {
                return new ValidationResultInfo("testCRWF3", ErrorLevel.OK,"Failed");
            } else {
                return new ValidationResultInfo("testCRWF3", ErrorLevel.OK,"Passed");
            }

    }

        private static ValidationResultInfo testCRWF3Case1(IGenericClient client, ContextInfo contextInfo) {
                System.out.println("Started testCRWF3Case1");
                //read by id
                //https://hapi.fhir.org/baseDstu3/Patient/20909
                Patient patient = client.read()
                        .resource(Patient.class)
                        .withId("20909")
                        .execute();
                System.out.println("=>" + patient);
                System.out.println("=>" +patient.getName());
                System.out.println("Finished testCRWF3Case1");
                //add entry for separate testcase.
                return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.OK, "Passed");
        }
}
