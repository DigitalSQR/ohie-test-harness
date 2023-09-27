package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.hl7.fhir.dstu3.model.Patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF4TestCases {

        public static void testCRWF4(String serverBaseURL, ContextInfo contextInfo) throws OperationFailedException {
            testCRWF4Case1(serverBaseURL, contextInfo);
        }

        private static void testCRWF4Case1(String serverBaseURL,ContextInfo contextInfo) throws OperationFailedException {

            //https://hapi.fhir.org/baseDstu3/Patient/20909
            FhirContext context = FhirContext.forDstu3();
            IGenericClient client = context.newRestfulGenericClient(serverBaseURL);
//            UriDt uriDt = new UriDt();
            Map<String, List<IQueryParameterType>> stringListMap = new HashMap<>();
 //            client.search(Patient.class, uriDt);
//            List<Patient> patients = client.search(Patient.class, stringListMap).getResources(Patient.class);
            //critQuery
//            Bundle bundle = client.search().forResource(Patient.class).execute();
//            List<Patient> patients = bundle.getResources(Patient.class);
//            System.out.println(patients.size());
//            patients.stream().forEach(patient->{
//                System.out.println(patient.toString());
//            });
        }
}
