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
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/update-patient-demographic-record-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF2TestCases {

        public static ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) {
                System.out.println("Started CRWF2TestCases");
                //code to add entry that started process for CRWF2 testing
                List<ValidationResultInfo> allTestCasesResults = new ArrayList<>();
                allTestCasesResults.add(CRWF2TestCases.testCRWF2Case1(client, contextInfo));

                //make entry for whole CRWF2 and return response.
                if (ValidationUtils.containsErrors(allTestCasesResults, ErrorLevel.ERROR)) {
                        return new ValidationResultInfo("testCRWF2", ErrorLevel.OK,"Failed");
                } else {
                        return new ValidationResultInfo("testCRWF2", ErrorLevel.OK,"Passed");
                }
        }

        private static ValidationResultInfo testCRWF2Case1(IGenericClient client, ContextInfo contextInfo) {
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
