package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.sharedhealthrecord;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Testcase For SHRWF1TestCase1
 *
 * @author Nirabhra
 */

@Component
public class SHRWF1TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(SHRWF1TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) {

        String testCaseName = this.getClass().getSimpleName();
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);

        IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_SHARED_HEALTH_RECORD_REGISTRY_ID);
        if (client == null) {
            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
        }

        //Creating Patient
        Patient patient = FHIRUtils.createPatient("Doe", "John", "male", "1990-01-01", "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "john.doe@example.com", client);

        LOGGER.info("Creating Patient");
        MethodOutcome outcome = client.create().resource(patient).execute();

        //Checking if patient is created
        if (Boolean.FALSE.equals(outcome.getCreated())) {
            LOGGER.error("Patient Creation Failed");
            return new ValidationResultInfo(ErrorLevel.ERROR, "Patient creation failed");
        }

        String patientID = outcome.getResource().getIdElement().getIdPart();

        //Creating Practitioners
        Practitioner practitioner1 = FHIRUtils.createPractitioner("Jane Doe", "female", "1970-02-02", "MIHERD-645", "666-666-6666");

        Practitioner practitioner2 = FHIRUtils.createPractitioner("Mary Doe", "female", "1980-03-03", "MIHERD-646", "777-777-7777");

        LOGGER.info("Creating Practitioner 1");
        outcome = client.create().resource(practitioner1).execute();

        //Checking if practitioner is created
        if (Boolean.FALSE.equals(outcome.getCreated())) {
            LOGGER.error("Practitioner 1 Creation Failed");
            return new ValidationResultInfo(ErrorLevel.ERROR, "Practitioner 1 creation failed");
        }

        String practitionerID = outcome.getResource().getIdElement().getIdPart();

        List<String> participantIDs = new ArrayList<>();
        participantIDs.add(practitionerID);

        LOGGER.info("Creating Practitioner 2");
        outcome = client.create().resource(practitioner2).execute();

        //Checking if practitioner is created
        if (Boolean.FALSE.equals(outcome.getCreated())) {
            LOGGER.error("Practitioner 2 Creation Failed");
            return new ValidationResultInfo(ErrorLevel.ERROR, "Practitioner 2 creation failed");
        }

        practitionerID = outcome.getResource().getIdElement().getIdPart();

        participantIDs.add(practitionerID);

        //Create encounter
        LOGGER.info("Creating Encounter");
        Encounter encounter = FHIRUtils.createEncounter(patientID, participantIDs, "inpatient", "2019-04-04");

        outcome = client.create().resource(encounter).execute();

        //Check if encounter is created
        if (Boolean.FALSE.equals(outcome.getCreated())) {
            return new ValidationResultInfo(ErrorLevel.ERROR, "Encounter creation failed");
        }

        String encounterID = outcome.getResource().getIdElement().getIdPart();

        //Fetching encounter
        LOGGER.info("Fetching Encounter");
        Encounter fetchEncounter = client.read().resource(Encounter.class).withId(encounterID).execute();

        String fetchedPatientID = "";
        String fetchedPractitionerID = "";

        //Make a pattern for extracting the patient ID from the references
        LOGGER.info("Extracting patient and practitioner(s) IDs from encounter references");
        String input = fetchEncounter.getSubject().getReference();
        ;

        Pattern pattern = Pattern.compile("\\d+");

        Matcher matcher = pattern.matcher(input);

        // Check if a match is found
        if (matcher.find()) {
            //Store the patient ID
            fetchedPatientID = matcher.group();

        } else {
            LOGGER.error("Patient ID not found");
            return new ValidationResultInfo(ErrorLevel.ERROR, "Could not find patientID");
        }

        //Check if Patient ID matches
        if (!(fetchedPatientID.equals(patientID))) {
            LOGGER.error("Patient ID does not match");
            return new ValidationResultInfo(ErrorLevel.ERROR, "Patient ID does not match");
        }

        //Loop through the participants of the encounter
        LOGGER.info("Looping through the participants");
        for (Encounter.EncounterParticipantComponent participant : fetchEncounter.getParticipant()) {

            //Make a pattern for extracting the practitioner ID from the references
            input = participant.getIndividual().getReference();

            matcher = pattern.matcher(input);

            // Check if a match is found
            if (matcher.find()) {
                //Store the practitionerID
                fetchedPractitionerID = matcher.group();

            } else {
                LOGGER.error("Practitioner ID not found");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Could not find practitionerID");
            }
            boolean participantIDsMatch = false;

            for (String participantID : participantIDs) {
                if (fetchedPractitionerID.equals(participantID)) {
                    participantIDsMatch = true;
                    break;
                }
            }

            //Check if Practitioner ID matches
            if (!participantIDsMatch) {
                LOGGER.error("Practitioner ID does not match");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Patient ID does not match");
            }
        }

        return new ValidationResultInfo(ErrorLevel.OK, "Passed");

    }
}
