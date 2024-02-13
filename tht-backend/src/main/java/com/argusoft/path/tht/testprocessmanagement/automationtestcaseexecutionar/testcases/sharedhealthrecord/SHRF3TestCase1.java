package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.sharedhealthrecord;

import ca.uhn.fhir.parser.IParser;
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
public class SHRF3TestCase1 implements TestCase {
    public static final Logger logger = LoggerFactory.getLogger(SHRF3TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) throws Exception {
        try {

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_SHARED_HEALTH_RECORD_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo("testSHRF3TestCase1", ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            logger.info("Start testing SHRF3TestCase1");


            // Create a new patient resource with all demographic information by passing XML data and get patient in XML format.
            Patient patient = FHIRUtils.createPatient("Doe", "John", "MALE", "1990-01-01", "00001", "555-555-5555", true, "", "21468992", "xyz@gmail.com", client);

            IParser jsonParser = client.getFhirContext().newJsonParser().setPrettyPrint(true);
            IParser xmlParser = client.getFhirContext().newXmlParser().setPrettyPrint(true);

            String xmlDataForPatient = xmlParser.encodeResourceToString(patient);

            MethodOutcome outcomeForPatient = client.create().
                    resource(xmlDataForPatient).execute();

            // Check if the patient was created successfully
            if (!outcomeForPatient.getCreated()) {
                logger.error("Failed to create patient by passing XML data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create patient by passing XML data");
            }
            Patient resultPatient = client.read()
                    .resource(Patient.class)
                    .withId(outcomeForPatient.getResource().getIdElement().getIdPart())
                    .encodedXml()
                    .execute();

            if (resultPatient == null) {
                logger.error("Patient created successfully by XML data but failed to get patient by Id in XML format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Patient created successfully by XML data but failed to get patient by Id in XML format");
            }


            //Create Practitioner by passing XML data and get practitioner in JSON format.

            Practitioner practitioner = FHIRUtils.createPractitioner("Dr. John Doe", "male", "1980-01-01", "MD", "Doctor of Medicine", "123-456-7890", "john.doe@example.com", "456 Main St", "Cityville", "Stateville", "12345");

            String xmlDataForPractitioner = xmlParser.encodeResourceToString(practitioner);

            MethodOutcome outcomeForPractitioner =
                    client.create().resource(xmlDataForPractitioner).execute();


            // Check if the practitioner was created successfully
            if (!outcomeForPractitioner.getCreated()) {
                logger.error("Failed to create practitioner by passing XML data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create practitioner by passing XML data");
            }

            Practitioner resultPractitioner = client.read()
                    .resource(Practitioner.class)
                    .withId(outcomeForPractitioner.getResource().getIdElement().getIdPart())
                    .encodedJson()
                    .execute();

            if (resultPractitioner == null) {
                logger.error("Practitioner created successfully by passing XML data but failed to get practitioner by Id in JSON Format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Practitioner created successfully by passing XML data but failed to get practitioner by Id in JSON Format");
            }

            // Create Organization by passing JSON data and get organization in XML format.
            Organization organization = FHIRUtils.createOrganization("Health System Inc.", "Hospital", "123 Main St", "Cityville",
                    "Stateville", "Countryland", "12345");

            String jsonDataForOrganization = jsonParser.encodeResourceToString(organization);

            MethodOutcome outcomeForOrganization =
                    client.create().resource(jsonDataForOrganization).execute();


            // Check if the organization was created successfully
            if (!outcomeForOrganization.getCreated()) {
                logger.error("Failed to create organization by passing JSON data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create organization by passing JSON data");
            }

            Organization resultOrganization = client.read()
                    .resource(Organization.class)
                    .withId(outcomeForOrganization.getResource().getIdElement().getIdPart())
                    .encodedXml()
                    .execute();

            if (resultOrganization == null) {
                logger.error("Organization created successfully by passing JSON data but failed to get organization by Id in XML format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Organization created successfully by passing JSON data but failed to get organization by Id in XML format");
            }


            // Create Practitioner Role by passing JSON data and get practitioner in JSON format.
            PractitionerRole practitionerRole = FHIRUtils.createPractitionerRole("General Practitioner", "GP", resultPractitioner.getId(), resultOrganization.getId());

            String jsonDataForPractitionerRole = jsonParser.encodeResourceToString(practitionerRole);

            MethodOutcome outcomeForPractitionerRole =
                    client.create().resource(jsonDataForPractitionerRole).execute();


            // Check if the practitioner role was created successfully
            if (!outcomeForPractitionerRole.getCreated()) {
                logger.error("Failed to create practitioner role by passing JSON data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create practitioner role by passing JSON data");
            }

            PractitionerRole resultPractitionerRole = client.read()
                    .resource(PractitionerRole.class)
                    .withId(outcomeForPractitionerRole.getResource().getIdElement().getIdPart())
                    .encodedJson()
                    .execute();

            if (resultPractitionerRole == null) {
                logger.error("Practitioner role created successfully by passing JSON data but failed to get practitioner role by Id by in JSON format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Practitioner role created successfully by passing JSON data but failed to get practitioner role by Id by in JSON format");
            }


            // Create Encounter and getting encounter data in JSON format.
            Encounter encounter = FHIRUtils.createEncounter(resultPatient.getId(), resultPractitioner.getId(), "101");

            MethodOutcome outcomeForEncounter =
                    client.create().resource(encounter).execute();


            // Check if the encounter was created successfully
            if (!outcomeForEncounter.getCreated()) {
                logger.error("Failed to create encounter");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create encounter");
            }

            Encounter resultEncounter = client.read()
                    .resource(Encounter.class)
                    .withId(outcomeForEncounter.getResource().getIdElement().getIdPart())
                    .encodedJson()
                    .execute();

            if (resultEncounter == null) {
                logger.error("Encounter created successfully but failed to get encounter by Id in JSON format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Encounter created successfully but failed to get encounter by Id in JSON format");
            }

            // Create Condition by get condition data in XML format.
            Condition condition = FHIRUtils.createCondition(resultPatient.getId(), resultEncounter.getId(), "Hypertension", "12345");

            MethodOutcome outcomeForCondition =
                    client.create().resource(condition).execute();


            // Check if the condition was created successfully
            if (!outcomeForCondition.getCreated()) {
                logger.error("Failed to create condition");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create condition");
            }

            Condition resultCondition = client.read()
                    .resource(Condition.class)
                    .withId(outcomeForCondition.getResource().getIdElement().getIdPart())
                    .encodedXml()
                    .execute();

            if (resultCondition == null) {
                logger.error("Condition created successfully but failed to get condition by Id in XML format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Condition created successfully but failed to get condition by Id in XML format");
            }

            // Create Medication by passing JSON data and get medication data.
            Medication medication = FHIRUtils.createMedication("12345", "Aspirin", "Tablet");

            String jsonDataForMedication = jsonParser.encodeResourceToString(medication);

            MethodOutcome outcomeForMedication =
                    client.create().resource(jsonDataForMedication).execute();

            // Check if the medication was created successfully
            if (!outcomeForMedication.getCreated()) {
                logger.error("Failed to create Medication by passing JSON data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Medication by passing JSON data");
            }

            Medication resultMedication = client.read()
                    .resource(Medication.class)
                    .withId(outcomeForMedication.getResource().getIdElement().getIdPart())
                    .execute();

            if (resultMedication == null) {
                logger.error("Medication created successfully by passing JSON data but failed to get medication by Id");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Medication created successfully by passing JSON data but failed to get medication by Id");
            }

            // Create Medication request by passing XML data and get medication request data.
            MedicationRequest medicationRequest = FHIRUtils.createMedicationRequest(resultMedication.getId(), resultPatient.getId());
            String xmlDataForMedicationRequest = xmlParser.encodeResourceToString(medicationRequest);

            MethodOutcome outcomeForMedicationRequest =
                    client.create().resource(xmlDataForMedicationRequest).execute();

            // Check if the medication request was created successfully
            if (!outcomeForMedicationRequest.getCreated()) {
                logger.error("Failed to create Medication request by passing XML data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Medication request by passing XML data");
            }

            MedicationRequest resultMedicationRequest = client.read()
                    .resource(MedicationRequest.class)
                    .withId(outcomeForMedicationRequest.getResource().getIdElement().getIdPart())
                    .execute();

            if (resultMedicationRequest == null) {
                logger.error("Medication request created successfully by passing XML data but failed to get medication request by Id");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Medication request created successfully by passing XML data but failed to get medication request by Id");
            }

            // Create Procedure by passing JSON data and get procedure in JSON format.
            Procedure procedure = FHIRUtils.createProcedure(resultPatient.getId(), "12345", "Appendectomy");

            String jsonDataForProcedure = jsonParser.encodeResourceToString(procedure);

            MethodOutcome outcomeForProcedure =
                    client.create().resource(jsonDataForProcedure).execute();

            // Check if the procedure was created successfully
            if (!outcomeForProcedure.getCreated()) {
                logger.error("Failed to create Procedure by passing JSON data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Procedure by passing JSON data");
            }

            Procedure resultProcedure = client.read()
                    .resource(Procedure.class)
                    .withId(outcomeForProcedure.getResource().getIdElement().getIdPart())
                    .encodedJson()
                    .execute();

            if (resultProcedure == null) {
                logger.error("Procedure created successfully by passing JSON data but failed to get procedure by Id in JSON format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Procedure created successfully by passing JSON data but failed to get procedure by Id in JSON format");
            }

            // Create Observation by passing XML data and get observation in XML format.
            Observation observation = FHIRUtils.createObservation(resultPatient.getId(), "78901", "Blood Pressure", 120.0, "mmHg");

            String xmlDataForObservation = xmlParser.encodeResourceToString(observation);

            MethodOutcome outcomeForObservation =
                    client.create().resource(xmlDataForObservation).execute();

            // Check if the observation was created successfully
            if (!outcomeForObservation.getCreated()) {
                logger.error("Failed to create Observation by passing XML data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Observation by passing XML data");
            }

            Observation resultObservation = client.read()
                    .resource(Observation.class)
                    .withId(outcomeForObservation.getResource().getIdElement().getIdPart())
                    .encodedXml()
                    .execute();

            if (resultObservation == null) {
                logger.error("Observation created successfully by passing XML data but failed to get Observation by Id in XML format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Observation created successfully by passing XML data but failed to get Observation by Id in XML format");
            }

            // Create Allergy Intolerance by passing JSON data and get allergy intolerance in JSON format.
            AllergyIntolerance allergyIntolerance = FHIRUtils.createAllergyIntolerance(resultPatient.getId(), "98765", "Peanut");

            String jsonDataForAllergyIntolerance = jsonParser.encodeResourceToString(allergyIntolerance);

            MethodOutcome outcomeForAllergyIntolerance =
                    client.create().resource(jsonDataForAllergyIntolerance).execute();

            // Check if the Allergy Intolerance was created successfully
            if (!outcomeForAllergyIntolerance.getCreated()) {
                logger.error("Failed to create Allergy Intolerance by passing JSON data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Allergy Intolerance by passing JSON data");
            }

            AllergyIntolerance resultAllergyIntolerance = client.read()
                    .resource(AllergyIntolerance.class)
                    .withId(outcomeForAllergyIntolerance.getResource().getIdElement().getIdPart())
                    .encodedJson()
                    .execute();

            if (resultAllergyIntolerance == null) {
                logger.error("Allergy Intolerance created successfully by passing JSON data but failed to get Observation by Id in JSON format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Allergy Intolerance created successfully by passing JSON data but failed to get Observation by Id in JSON format");
            }

            // Create Immunization by passing XML data and get immunization in XML format.
            Immunization immunization = FHIRUtils.createImmunization(resultPatient.getId(), "78901", "COVID-19 Vaccine");

            String xmlDataForImmunization = xmlParser.encodeResourceToString(immunization);

            MethodOutcome outcomeForImmunization =
                    client.create().resource(xmlDataForImmunization).execute();

            // Check if the immunization was created successfully
            if (!outcomeForImmunization.getCreated()) {
                logger.error("Failed to create Immunization by passing XML data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Immunization by passing XML data");
            }

            Immunization resultImmunization = client.read()
                    .resource(Immunization.class)
                    .withId(outcomeForImmunization.getResource().getIdElement().getIdPart())
                    .encodedXml()
                    .execute();

            if (resultImmunization == null) {
                logger.error("Immunization created successfully by passing XML data but failed to get immunization by Id in XML format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Immunization created successfully by passing XML data but failed to get immunization by Id in XML format");
            }

            // Create Diagnosis Report by passing JSON data and get diagnosis report in JSON format.
            DiagnosticReport diagnosticReport = FHIRUtils.createDiagnosticReportWithCode(resultPatient.getId(), "56789", "X-ray Report");

            String jsonDataForDiagnosticReport = jsonParser.encodeResourceToString(diagnosticReport);

            MethodOutcome outcomeForDiagnosticReport =
                    client.create().resource(jsonDataForDiagnosticReport).execute();

            // Check if the Diagnostic Report was created successfully
            if (!outcomeForDiagnosticReport.getCreated()) {
                logger.error("Failed to create Diagnostic Report by passing JSON data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Diagnostic Report by passing JSON data");
            }

            DiagnosticReport resultDiagnosticReport = client.read()
                    .resource(DiagnosticReport.class)
                    .withId(outcomeForDiagnosticReport.getResource().getIdElement().getIdPart())
                    .encodedJson()
                    .execute();

            if (resultDiagnosticReport == null) {
                logger.error("Diagnostic Report created successfully by passing JSON data but failed to get diagnostic report by Id in JSON format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Diagnostic Report created successfully by passing JSON data but failed to get diagnostic report by Id in JSON format");
            }

            // Create Medication administration by passing JSON data and get medical administration in JSON format.
            MedicationAdministration medicationAdministration = FHIRUtils.createMedicationAdministration(resultPatient.getId(), resultPractitioner.getId(), resultMedication.getId(), resultEncounter.getId());

            String jsonDataForMedicationAdministration = jsonParser.encodeResourceToString(medicationAdministration);

            MethodOutcome outcomeForMedicationAdministration =
                    client.create().resource(jsonDataForMedicationAdministration).execute();

            // Check if the Diagnostic Report was created successfully
            if (!outcomeForDiagnosticReport.getCreated()) {
                logger.error("Failed to create Diagnostic Report by passing JSON data");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Diagnostic Report by passing JSON data");
            }

            MedicationAdministration resultMedicationAdministration = client.read()
                    .resource(MedicationAdministration.class)
                    .withId(outcomeForMedicationAdministration.getResource().getIdElement().getIdPart())
                    .encodedJson()
                    .execute();

            if (resultMedicationAdministration == null) {
                logger.error("Medication administration created successfully by passing JSON data but failed to get medication administration report by Id in JSON format");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Medication administration created successfully by passing JSON data but failed to get medication administration report by Id in JSON format");
            }

            logger.info("SHRF3 test case passed");
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");

        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
