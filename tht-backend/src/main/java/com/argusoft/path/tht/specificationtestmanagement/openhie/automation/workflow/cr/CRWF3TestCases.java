package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
//import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.hl7.fhir.dstu3.model.Patient;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/query-patient-demographic-records-by-identifier-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF3TestCases {

        public static void testCRWF3(String serverBaseURL, ContextInfo contextInfo) throws OperationFailedException {
            testCRWF3Case1(serverBaseURL, contextInfo);
        }

        private static void testCRWF3Case1(String serverBaseURL,ContextInfo contextInfo) throws OperationFailedException {
            FhirContext context = FhirContext.forDstu3();
            context.getRestfulClientFactory().setConnectTimeout(60 * 1000);
            context.getRestfulClientFactory().setSocketTimeout(60 * 1000);

            IGenericClient client = context.newRestfulGenericClient(serverBaseURL);
            //read by id
            //https://hapi.fhir.org/baseDstu3/Patient/20909
            Patient patient = client.read()
                    .resource(Patient.class)
                    .withId("20909")
                    .execute();

            System.out.println(patient);
            System.out.println(patient.getName());
        }
}
