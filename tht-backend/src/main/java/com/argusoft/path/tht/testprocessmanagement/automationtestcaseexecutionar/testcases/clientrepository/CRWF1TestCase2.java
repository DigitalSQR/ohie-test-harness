package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.stereotype.Component;

/**
 * Implemantation of the CRWF1 Testcase2 Testing.
 * Reference https://guides.ohie.org/arch-spec/introduction/patient-identity-management-workflows/create-patient-demographic-record-workflow-1.
 *
 * @author dhruv
 * @since 2023-09-25
 */
@Component
public class CRWF1TestCase2 implements TestCase {

    @Override
    public ValidationResultInfo test(IGenericClient client,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            System.out.println("Started testCRWF1Case2");
            Patient patient = new Patient();
            //Create Mock data for the patient
            MethodOutcome outcome = client.create()
                    .resource(patient)
                    .execute();
            RelatedPerson relatedPerson = new RelatedPerson();
//        relatedPerson.setPatient()
            Communication communication = new Communication();
            System.out.println("=>" + outcome.getId());
            if (Boolean.FALSE.equals(outcome.getCreated())) {
                //add entry for failure testcase.
                return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.ERROR, "Not able to create patient");
            }
            //Standard: The workflow will not register a duplicate patent if the patient already exists in the Client Registry.
            outcome = client.create()
                    .resource(patient)
                    .execute();
            System.out.println("Finished testCRWF1Case2");
            //add entry for separate testcase.
            return new ValidationResultInfo("testCRWF1Case2", ErrorLevel.OK, "Passed");
        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
