package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.hl7.fhir.dstu3.model.Patient;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/update-patient-demographic-record-workflow.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF2TestCases {

        public static void testCRWF2(String serverBaseURL, ContextInfo contextInfo) throws OperationFailedException {
            testCRWF2Case1(serverBaseURL, contextInfo);
        }

        private static void testCRWF2Case1(String serverBaseURL,ContextInfo contextInfo) throws OperationFailedException {
                FhirContext context = FhirContext.forDstu2();
                IGenericClient client = context.newRestfulGenericClient(serverBaseURL);

                Patient patient = client.read()
                        .resource(Patient.class)
                        .withId("id")
                        .execute();
                //update patient data.
                client.update()
                        .resource(patient)
                        .execute();
                //Verify Result
        }
}
