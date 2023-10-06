package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow-1.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF1TestCases {

        public static CompletableFuture<ValidationResultInfo> test(IGenericClient client, ContextInfo contextInfo) {
            return CompletableFuture.supplyAsync(() -> {
                System.out.println("Started CRWF1TestCases");
                //code to add entry that started process for CRWF1 testing.

                List<CompletableFuture<ValidationResultInfo>> testCases = new ArrayList<>();
                testCases.add(CRWF1TestCases.testCRWF1Case1(client, contextInfo));
                testCases.add(CRWF1TestCases.testCRWF1Case2(client, contextInfo));

                CompletableFuture<Void> allTestCases = CompletableFuture.allOf(
                        testCases.toArray(new CompletableFuture[testCases.size()])
                );

                CompletableFuture<List<ValidationResultInfo>> allTestCasesJoins = allTestCases.thenApply(v -> {
                    return testCases.stream()
                            .map(pageContentFuture -> pageContentFuture.join())
                            .collect(Collectors.toList());
                });

                try {
                    List<ValidationResultInfo> allTestCasesResults = allTestCasesJoins.thenApply(validationResultInfos -> {
                        return validationResultInfos;
                    }).get();
                    //make entry for whole CRWF1 and return response.
                    if (ValidationUtils.containsErrors(allTestCasesResults, ErrorLevel.ERROR)) {
                        return new ValidationResultInfo("testCRWF1", ErrorLevel.OK,"Failed");
                    } else {
                        return new ValidationResultInfo("testCRWF1", ErrorLevel.OK,"Passed");
                    }
                } catch (InterruptedException|ExecutionException e) {
                    //create error validation response.
                    return new ValidationResultInfo("testCRWF1", ErrorLevel.ERROR, e.getMessage());
                }
            });
        }

        private static CompletableFuture<ValidationResultInfo> testCRWF1Case1(IGenericClient client, ContextInfo contextInfo) {

            return CompletableFuture.supplyAsync(() -> {
                System.out.println("Started testCRWF1Case1");
                try {
                    return new ValidationResultInfo("testCRWF1Case1", ErrorLevel.OK,"Passed");
                } catch (Exception ex) {
                    return new ValidationResultInfo("testCRWF1Case1", ErrorLevel.ERROR, ex.getMessage());
                }
        }).thenApply(validationResultInfo -> {
            System.out.println("Finished testCRWF1Case1");
            //add entry for separate testcase.
            return validationResultInfo;
        });
    }

    private static CompletableFuture<ValidationResultInfo> testCRWF1Case2(IGenericClient client, ContextInfo contextInfo) {

        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Started testCRWF1Case2");
            try {
                Patient patient = new Patient();
                //Create Mock data for the patient
                MethodOutcome outcome = client.create()
                        .resource(patient)
                        .execute();

                System.out.println("=>" + outcome.getId());
                if (Boolean.FALSE.equals(outcome.getCreated())) {
                    return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, "Not able to create patient");
                }

                //Standard: The workflow will not register a duplicate patent if the patient already exists in the Client Registry.
                outcome = client.create()
                        .resource(patient)
                        .execute();

                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.OK, "Passed");
             } catch (Exception ex) {
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, ex.getMessage());
             }
        }).thenApply(validationResultInfo -> {
            System.out.println("Finished testCRWF1Case2");
            //add entry for separate testcase.
            return validationResultInfo;
        });
    }
}
