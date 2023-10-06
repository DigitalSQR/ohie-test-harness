package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

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
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/update-patient-demographic-record-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF2TestCases {

        public static CompletableFuture<ValidationResultInfo> test(IGenericClient client, ContextInfo contextInfo) {

                return CompletableFuture.supplyAsync(() -> {
                        System.out.println("Started CRWF2TestCases");
                        //code to add entry that started process for CRWF2 testing
                        List<CompletableFuture<ValidationResultInfo>> testCases = new ArrayList<>();
                        testCases.add(CRWF2TestCases.testCRWF2Case1(client, contextInfo));

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
                                //make entry for whole CRWF2 and return response.
                                if (ValidationUtils.containsErrors(allTestCasesResults, ErrorLevel.ERROR)) {
                                        return new ValidationResultInfo("testCRWF2", ErrorLevel.OK,"Failed");
                                } else {
                                        return new ValidationResultInfo("testCRWF2", ErrorLevel.OK,"Passed");
                                }
                        } catch (InterruptedException|ExecutionException e) {
                                //create error validation response.
                                return new ValidationResultInfo("testCRWF2", ErrorLevel.ERROR, e.getMessage());
                        }
                });
        }

        private static CompletableFuture<ValidationResultInfo> testCRWF2Case1(IGenericClient client, ContextInfo contextInfo) {
                return CompletableFuture.supplyAsync(() -> {
                        System.out.println("Started testCRWF2Case1");
                        try {
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
                                return new ValidationResultInfo("testCRWF2Case1", ErrorLevel.OK, "Passed");
                        } catch (Exception ex) {
                                return new ValidationResultInfo("testCRWF2Case1", ErrorLevel.ERROR, ex.getMessage());
                        }
                }).thenApply(validationResultInfo -> {
                        System.out.println("Finished testCRWF2Case1");
                        //add entry for separate testcase.
                        return validationResultInfo;
                });
        }
}
