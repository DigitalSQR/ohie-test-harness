package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.workflow.cr;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.hl7.fhir.dstu3.model.Patient;

/**
 * Implemantation of the CRWF1 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow-1.
 *
 * @author dhruv
 * @since 2023-09-25
 */
public class CRWF1TestCases {

        public static void testCRWF1(String serverBaseURL, ContextInfo contextInfo) throws OperationFailedException {
            testCRWF1Case1(serverBaseURL, contextInfo);
        }

        private static void testCRWF1Case1(String serverBaseURL,ContextInfo contextInfo) throws OperationFailedException {
        FhirContext context = FhirContext.forDstu3();
        IGenericClient client = context.newRestfulGenericClient(serverBaseURL);


        //Standard 1: The Point-of-Service (PoS) system is a trusted application known by the HIE and it is registered with the interoperability layer to be able to send and receive data securely
        Patient patient = new Patient();
        //Create Mock data for the patient
        MethodOutcome outcome =  client.create()
                .resource(patient)
                .execute();

        //Verify Result
        //Standard 2: The workflow will result in a positive acknowledgement message when the patient is successfully created in the Client Registry or if the patient already exists in the Client Registry.
        if(Boolean.FALSE.equals(outcome.getCreated())) {
            //Add fail test case entry.
            return;
        }

        //Standard 1: The workflow will not register a duplicate patent if the patient already exists in the Client Registry.
        outcome =  client.create()
                .resource(patient)
                .execute();

        //Standard 2: The workflow will result in a positive acknowledgement message when the patient is successfully created in the Client Registry or if the patient already exists in the Client Registry.
        //Verify Result


        //delete data
        client.delete()
                .resourceById("Patient", "id")
                .execute();
    }
}
