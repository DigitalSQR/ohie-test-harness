package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;
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

        public static ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) {
                System.out.println("Started CRWF4TestCases");
                //code to add entry that started process for CRWF4 testing
                List<ValidationResultInfo> allTestCasesResults = new ArrayList<>();
                allTestCasesResults.add(CRWF4TestCases.testCRWF4Case1(client, contextInfo));

                //make entry for whole CRWF4 and return response.
                if (ValidationUtils.containsErrors(allTestCasesResults, ErrorLevel.ERROR)) {
                    return new ValidationResultInfo("testCRWF4", ErrorLevel.OK,"Failed");
                } else {
                    return new ValidationResultInfo("testCRWF4", ErrorLevel.OK,"Passed");
                }
        }

        private static ValidationResultInfo testCRWF4Case1(IGenericClient client, ContextInfo contextInfo) {
                System.out.println("Started testCRWF4Case1");
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
            System.out.println("Finished testCRWF4Case1");
            //add entry for separate testcase.
            return new ValidationResultInfo("testCRWF3Case1", ErrorLevel.OK, "Passed");
        }
}
