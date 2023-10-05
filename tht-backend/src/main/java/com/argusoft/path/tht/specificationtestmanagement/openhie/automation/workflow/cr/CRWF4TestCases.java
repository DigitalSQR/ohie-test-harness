package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF4TestCases {

        public static CompletableFuture<ValidationResultInfo> test(IGenericClient client, ContextInfo contextInfo) {

            return CompletableFuture.supplyAsync(() -> {
                //code to add entry that started process for CRWF4 testing
                List<CompletableFuture<ValidationResultInfo>> testCases = new ArrayList<>();
                testCases.add(CRWF4TestCases.testCRWF4Case1(client, contextInfo));

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

                    //make entry for whole CRWF4 and return response.
                    return new ValidationResultInfo("testCRWF4", ErrorLevel.OK,"Passed");
                } catch (InterruptedException | ExecutionException e) {
                    //create error validation response.
                    return new ValidationResultInfo("testCRWF4", ErrorLevel.ERROR,e.getMessage());
                }
            });
        }

        private static CompletableFuture<ValidationResultInfo> testCRWF4Case1(IGenericClient client, ContextInfo contextInfo) {
            return CompletableFuture.supplyAsync(() -> {
                try {
        //            UriDt uriDt = new UriDt();
        //            Map<String, List<IQueryParameterType>> stringListMap = new HashMap<>();
        //            client.search(Patient.class, uriDt);
        //            List<Patient> patients = client.search(Patient.class, stringListMap).getResources(Patient.class);
        //            // critQuery
        //            Bundle bundle = client.search().forResource(Patient.class).execute();
        //            List<Patient> patients = bundle.getResources(Patient.class);
        //            System.out.println(patients.size());
        //            patients.stream().forEach(patient->{
        //                System.out.println(patient.toString());
        //            });
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
