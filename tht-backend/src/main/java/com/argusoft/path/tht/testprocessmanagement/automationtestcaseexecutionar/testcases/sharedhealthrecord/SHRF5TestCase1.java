package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.sharedhealthrecord;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SHRF5TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(SHRF5TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
            ContextInfo contextInfo) throws OperationFailedException {
        try {
            String testCaseName = this.getClass().getSimpleName();
            LOGGER.info("Start testing " + testCaseName);

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_SHARED_HEALTH_RECORD_REGISTRY_ID);

            if (client == null) {
                LOGGER.error(testCaseName + "Failed to get IGenericClient");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            Patient patient = FHIRUtils.createPatient("Doe", "John", "male", "1990-01-01", "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "9414473", "555-555-5555", "john.doe@example.com", client);
            MethodOutcome patientOutcome = client.create().resource(patient).execute();

            if (!patientOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Patient");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Patient");
            }
            Practitioner practitionerOne = FHIRUtils.createPractitioner("Walter", "male", "12-05-2001", "9414", "555-555-5555");
            MethodOutcome practitionerOneOutcome = client.create().resource(practitionerOne).execute();
            String practitionerReference = "Practitioner/" + practitionerOneOutcome.getId().getIdPart();
            if (!practitionerOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Practitioner");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Practitioner");
            }
            Organization organizationOne = FHIRUtils.createOrganization("Good Health Clinic", "India", "Gandhinagar", "111-111-111");
            MethodOutcome organizationOneOutcome = client.create().resource(organizationOne).execute();
            if (!organizationOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating organization");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create organization");
            }

            String patientId = patientOutcome.getId().getIdPart();

            Composition admissionNote = FHIRUtils.createAdmissionNote(patientId, organizationOneOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), "Good Health Clinic");
            MethodOutcome admissionNoteOutcome = client.create().resource(admissionNote).execute();

            if (!admissionNoteOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Composition");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Admission Composition");
            }
            String originalCompositionId = admissionNoteOutcome.getResource().getIdElement().getIdPart();

            Bundle compositionResults = client.search().forResource(Composition.class).
                    where(Composition.PATIENT.hasId(patientId)).and(Composition.RES_ID.exactly().
                    code(admissionNoteOutcome.getId().getIdPart())).returnBundle(Bundle.class).
                    execute();

            if (compositionResults.hasEntry() && compositionResults.getEntry().size() == 1) {
                for (Bundle.BundleEntryComponent entry : compositionResults.getEntry()) {
                    if (entry.getResource() instanceof Composition composition) {

                        String compositionId = composition.getIdElement().getIdPart();

                        if (!(compositionId.equals(originalCompositionId))) {
                            LOGGER.error(testCaseName + "Test case failed because of different composition Id in Bundle");
                            return new ValidationResultInfo(ErrorLevel.ERROR, "Different Composition Id");
                        }

                        String title = composition.getTitle();

                        String authorReference = null;
                        List<Reference> authors = composition.getAuthor();
                        if (authors == null || authors.isEmpty()) {
                            LOGGER.error(testCaseName + "Test case failed because no Author found in Bundle");
                            return new ValidationResultInfo(ErrorLevel.ERROR, "No Author in Composition Bundle");
                        }
                        authorReference = authors.get(0).getReference();
                        if (!(authorReference.equals(practitionerReference))) {
                            LOGGER.error(testCaseName + "Test case failed because of different Practitioner reference in Bundle");
                            return new ValidationResultInfo(ErrorLevel.ERROR, "Different Practitioner reference");
                        }
                        if (!(title.equals(admissionNote.getTitle()))) {
                            LOGGER.error(testCaseName + "Test case failed because of different title in Admission Note in Bundle");
                            return new ValidationResultInfo(ErrorLevel.ERROR, "Different title in Admission Note");
                        }
                        LOGGER.info(testCaseName + "Testcase successfully passed!");
                        return new ValidationResultInfo(ErrorLevel.OK, "Passed");
                    }
                }
            }
            LOGGER.error(testCaseName + "Test case failed because Composition has no Entry");
            return new ValidationResultInfo(ErrorLevel.ERROR, "Composition has no entry");
        } catch (Exception ex) {
            LOGGER.error(ValidateConstant.EXCEPTION + SHRF5TestCase1.class.getSimpleName(), ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }

}
