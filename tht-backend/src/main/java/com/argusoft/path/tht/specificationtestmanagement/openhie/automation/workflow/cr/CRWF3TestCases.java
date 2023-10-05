package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
//import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.hl7.fhir.dstu3.model.Patient;

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

    public static CompletableFuture<ValidationResultInfo> test(IGenericClient client, ContextInfo contextInfo) {

        return CompletableFuture.supplyAsync(() -> {
            //code to add entry that started process for CRWF3 testing
            List<CompletableFuture<ValidationResultInfo>> testCases = new ArrayList<>();
            testCases.add(CRWF3TestCases.testCRWF3Case1(client, contextInfo));

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

                //make entry for whole CRWF3 and return response.
                return new ValidationResultInfo("testCRWF3", ErrorLevel.OK,"Passed");
            } catch (InterruptedException | ExecutionException e) {
                //create error validation response.
                return new ValidationResultInfo("testCRWF3", ErrorLevel.ERROR, e.getMessage());
            }
        });
    }

        private static CompletableFuture<ValidationResultInfo> testCRWF3Case1(IGenericClient client, ContextInfo contextInfo) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    //read by id
                    //https://hapi.fhir.org/baseDstu3/Patient/20909
                    Patient patient = client.read()
                            .resource(Patient.class)
                            .withId("20909")
                            .execute();

                    System.out.println(patient);
                    System.out.println(patient.getName());
                    return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.OK, "Passed");
                } catch (Exception ex) {
                    return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.ERROR, ex.getMessage());
                }
            }).thenApply(validationResultInfo -> {
                //add entry for separate testcase.
                return validationResultInfo;
            });
        }
}
