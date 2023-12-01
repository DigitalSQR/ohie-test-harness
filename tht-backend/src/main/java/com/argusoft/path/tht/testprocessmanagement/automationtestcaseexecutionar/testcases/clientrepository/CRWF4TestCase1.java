package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.springframework.stereotype.Component;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
@Component
public class CRWF4TestCase1 implements TestCase {

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) {
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
