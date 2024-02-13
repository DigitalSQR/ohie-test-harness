package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.sharedhealthrecord;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class SHRF5TestCase4 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(SHRF5TestCase4.class);
@Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException
    {
        try {
            String testCaseName = this.getClass().getSimpleName();
            LOGGER.info("Start testing " + testCaseName);

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_SHARED_HEALTH_RECORD_REGISTRY_ID);

            if (client == null) {
                LOGGER.error(testCaseName + "Failed to get IGenericClient");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            String identifierSystem = "urn:oid:1.3.6.1.4.1.21367.13.20.1000";
            String identifierValue = "IHERED-994";
            Patient patient = FHIRUtils.createPatient("Doe", "John", "male", "1990-01-01", identifierSystem, identifierValue, true, "9414473", "555-555-5555", "john.doe@example.com", client);
            MethodOutcome patientOutcome = client.create().resource(patient).execute();
            System.out.println("Patient reference id: " + patientOutcome.getId().getIdPart());
            if (!patientOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Patient");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Patient");
            }
            Practitioner practitionerOne = FHIRUtils.createPractitioner("Walter", "male", "12-05-2001", "9414", "555-555-5555");
            MethodOutcome practitionerOneOutcome = client.create().resource(practitionerOne).execute();
            if (!practitionerOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Practitioner");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Practitioner");
            }
            Practitioner practitionerTwo = FHIRUtils.createPractitioner("Mike", "male", "14-05-1999", "8114", "555-555-5555");
            MethodOutcome practitionerTwoOutcome = client.create().resource(practitionerTwo).execute();
            if (!practitionerTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Practitioner");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Practitioner");
            }
            Organization organizationOne = FHIRUtils.createOrganization("Good Health Clinic", "India", "Gandhinagar", "111-111-111");
            MethodOutcome organizationOneOutcome = client.create().resource(organizationOne).execute();
            if (!organizationOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating organization");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create organization");
            }

            Organization organizationTwo = FHIRUtils.createOrganization("Better Health Clinic", "India", "Ahembdabad", "111-222-111");
            MethodOutcome organizationTwoOutcome = client.create().resource(organizationTwo).execute();
            if (!organizationTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating organization");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create organization");
            }

            String patientId = patientOutcome.getId().getIdPart();

            Composition admissionNoteOne = FHIRUtils.createAdmissionNote(patientId, organizationOneOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), "Good Health Clinic");
            MethodOutcome admissionOneNoteOutcome = client.create().resource(admissionNoteOne).execute();
            if (!admissionOneNoteOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Composition");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Admission Composition");
            }
            DocumentReference documentReferenceAdmissionNoteOne = FHIRUtils.createDocumentReference(patientOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), client.getServerBase() + "/Composition/" + admissionOneNoteOutcome.getId().getIdPart(), "Patient Admission Note");
            MethodOutcome documentReferenceAdmissionNoteOneOutcome = client.create().resource(documentReferenceAdmissionNoteOne).execute();

            if (!documentReferenceAdmissionNoteOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Document Reference of Admission");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Document Reference of Admission");
            }
            Composition operativeNoteOne = FHIRUtils.createOperativeNote(patientId, organizationOneOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), "Good Health Clinic");
            MethodOutcome operativeNoteOneOutcome = client.create().resource(operativeNoteOne).execute();
            if (!operativeNoteOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Composition");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create operative Composition");
            }
            System.out.println("operativeNoteOutcome id:" + operativeNoteOneOutcome.getId().getIdPart());
            DocumentReference documentReferenceOperativeNoteOne = FHIRUtils.createDocumentReference(patientOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), client.getServerBase() + "/Composition/" + operativeNoteOneOutcome.getId().getIdPart(), "Patient OperativeNote");
            MethodOutcome documentReferenceOperativeNoteOneOutcome = client.create().resource(documentReferenceOperativeNoteOne).execute();

            if (!documentReferenceOperativeNoteOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Document Reference of Operative Note");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Document Reference of Operative Note");
            }

            Composition dischargeSummaryOne = FHIRUtils.createDischargeSummary(patientId, organizationOneOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), "Good Health Clinic");
            MethodOutcome dischargeSummaryOneOutcome = client.create().resource(dischargeSummaryOne).execute();
            if (!dischargeSummaryOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Composition");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Discharge Composition");
            }
            System.out.println("dischargeSummaryOutcome id: " + dischargeSummaryOneOutcome.getId().getIdPart());

            DocumentReference documentReferenceDischargeNoteOne = FHIRUtils.createDocumentReference(patientOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), client.getServerBase() + "/Composition/" + dischargeSummaryOneOutcome.getId().getIdPart(), "Patient Discharge Note");
            MethodOutcome documentReferenceDischargeNoteOneOutcome = client.create().resource(documentReferenceDischargeNoteOne).execute();

            if (!documentReferenceDischargeNoteOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Document Reference of Discharge Note");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Document Reference of Discharge Note");
            }


            Composition admissionNoteTwo = FHIRUtils.createAdmissionNote(patientId, organizationTwoOutcome.getId().getIdPart(), practitionerTwoOutcome.getId().getIdPart(), "Better Health Clinic");
            MethodOutcome admissionTwoNoteOutcome = client.create().resource(admissionNoteTwo).execute();
            if (!admissionTwoNoteOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Composition");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Admission Composition");
            }

            DocumentReference documentReferenceAdmissionNoteTwo = FHIRUtils.createDocumentReference(patientOutcome.getId().getIdPart(), practitionerTwoOutcome.getId().getIdPart(), client.getServerBase() + "/Composition/" + admissionTwoNoteOutcome.getId().getIdPart(), "Patient Admission Note");
            MethodOutcome documentReferenceAdmissionNoteTwoOutcome = client.create().resource(documentReferenceAdmissionNoteTwo).execute();

            if (!documentReferenceAdmissionNoteTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Document Reference of Admission");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Document Reference of Admission");
            }
            Composition operativeNoteTwo = FHIRUtils.createOperativeNote(patientId, organizationTwoOutcome.getId().getIdPart(), practitionerTwoOutcome.getId().getIdPart(), "Better Health Clinic");
            MethodOutcome operativeNoteTwoOutcome = client.create().resource(operativeNoteTwo).execute();
            if (!operativeNoteOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Composition");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Operative Note Composition");
            }

            DocumentReference documentReferenceOperativeNoteTwo = FHIRUtils.createDocumentReference(patientOutcome.getId().getIdPart(), practitionerTwoOutcome.getId().getIdPart(), client.getServerBase() + "/Composition/" + operativeNoteTwoOutcome.getId().getIdPart(), "Patient OperativeNote");
            MethodOutcome documentReferenceOperativeNoteTwoOutcome = client.create().resource(documentReferenceOperativeNoteTwo).execute();

            if (!documentReferenceOperativeNoteTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Document Reference of Operative Note");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Document Reference of Operative Note");
            }

            Composition dischargeSummaryTwo = FHIRUtils.createDischargeSummary(patientId, organizationTwoOutcome.getId().getIdPart(), practitionerTwoOutcome.getId().getIdPart(), "Better Health Clinic");
            MethodOutcome dischargeSummaryTwoOutcome = client.create().resource(dischargeSummaryTwo).execute();
            if (!dischargeSummaryTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Composition");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Discharge Composition");
            }

            DocumentReference documentReferenceDischargeNoteTwo = FHIRUtils.createDocumentReference(patientOutcome.getId().getIdPart(), practitionerTwoOutcome.getId().getIdPart(), client.getServerBase() + "/Composition/" + dischargeSummaryTwoOutcome.getId().getIdPart(), "Patient Discharge Note");
            MethodOutcome documentReferenceDischargeNoteTwoOutcome = client.create().resource(documentReferenceDischargeNoteTwo).execute();

            if (!documentReferenceDischargeNoteTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Document Reference of Discharge Note");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Document Reference of Discharge Note");
            }


            Bundle patientSummaryBundle = client
                    .search()
                    .byUrl(client.getServerBase() + "/Patient/" + patientOutcome.getId().getIdPart() + "/$summary").returnBundle(Bundle.class)
                    .execute();
            if (!patientSummaryBundle.hasEntry() || !patientSummaryBundle.getType().toCode().equals("document")) {
                LOGGER.error(testCaseName + "Testcase Failed because patient summary bundle has no entry or Bundle type is not of document");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Bundle has no entry or not as expected entry");
            }
            for (Bundle.BundleEntryComponent entry : patientSummaryBundle.getEntry()) {
                // Extract the resource object from the entry
                if (entry.getResource() instanceof Patient) {

                    // Check if the patient identifier matches the one you created
                    boolean identifiersMatch = false;
                    for (Identifier identifier : patient.getIdentifier()) {
                        if (identifier.getSystem().equals(identifierSystem) && identifier.getValue().equals(identifierValue)) {
                            identifiersMatch = true;
                            break;
                        }
                    }
                    boolean nameMatches = false;
                    for (HumanName name : patient.getName()) {
                        String firstName = name.getGivenAsSingleString();
                        String lastName = name.getFamily();
                        // Check if the name matches your criteria
                        if ("John".equals(firstName) && "Doe".equals(lastName)) {
                            nameMatches = true;
                            break;
                        }
                    }
                    if (!identifiersMatch || !nameMatches) {

                        LOGGER.error(testCaseName + "Testcase Failed because of of wrong identifier or wrong patient name");
                        return new ValidationResultInfo(ErrorLevel.ERROR, "Wrong identifier or patient name");
                    }

                }
            }

            LOGGER.info(testCaseName + "Testcase successfully passed!");
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");

        }
        catch (Exception ex)
        {
            LOGGER.error("caught OperationFailedException in SHRF5TestCase4 ", ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
