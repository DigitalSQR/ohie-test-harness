package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.sharedhealthrecordrepository;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SHRF7TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(SHRF7TestCase1.class);

    @Override
    public ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) throws OperationFailedException {
        try {

            LOGGER.info("Start testing SHRF7TestCase1");
            LOGGER.info("Creating patient");

            // Create a new patient resource with all demographic information
            Patient patient = FHIRUtils.createPatient("Doe", "John", "male", "1990-01-01", "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "", "555-555-5555", "john.doe@example.com", client);

            MethodOutcome outcome = client.create()
                    .resource(patient)
                    .execute();


            // Check if the patient was created successfully
            if (!outcome.getCreated()) {
                LOGGER.error("Patient Creation Failed");
                return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Failed to create patient");
            }


            //Get patientId from patient creation outcome
            String patientId = outcome.getResource().getIdElement().getIdPart();
            String historyUrl = client.getServerBase() + "/Patient/" + patientId + "/_history";

            LOGGER.info("Fetching patient history using patientId");
            //Get patient history using patientId
            Bundle postCreationPatientHistoryBundle = client.search().byUrl(historyUrl).returnBundle(Bundle.class).execute();


            if (postCreationPatientHistoryBundle.getEntry() == null) {
                LOGGER.error("Post Creation Patient History not found");
                return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "No Patient History");
            }


            for (Bundle.BundleEntryComponent entry : postCreationPatientHistoryBundle.getEntry()) {
                if (!(entry.getResponse().getStatus().equals("201 Created"))) {
                    LOGGER.error("First entry of Post Creation Patient History is not of creation");
                    return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Patient Creation is not reflected in history");
                }
            }

            //Store the birthdate of current patient
            Date oldBirthDate = patient.getBirthDate();

            LOGGER.info("Updating Birthdate of patient");

            // Update the birthdate using a Date object
            Date birthDate = FHIRUtils.parseDate("1999-07-25");
            patient.setBirthDate(birthDate);

            //update patient data.
            outcome = client.update()
                    .resource(patient)
                    .withId(patientId)
                    .execute();


            if (!patientId.equals(outcome.getResource().getIdElement().getIdPart())) {
                LOGGER.error("patientId is not same after birthdate update");
                return new ValidationResultInfo("testCRF8", ErrorLevel.ERROR, "Failed because instead of update it has created patient");
            }


            // Fetch the history of changes for the Patient resource after updating
            LOGGER.info("Fetching Patient History after updating birthdate");
            Bundle postUpdatePatientHistoryBundle = client.search().byUrl(historyUrl).returnBundle(Bundle.class).execute();


            if (postUpdatePatientHistoryBundle.getTotal() < 2) {
                LOGGER.error("Post birthdate update Patient History has less than 2 entries");
//                return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "History does not have required amount of versions");
            }


            //Set iterations variable to zero as a reverse counter to which version of patient history is being read
            int iterations = 0;


            for (Bundle.BundleEntryComponent entry : postUpdatePatientHistoryBundle.getEntry()) {

                if (entry.getResource() instanceof Patient testPatient) {
                    //Check updated birthdate for first iteration
                    if (iterations == 0) {

                        //If last updated birthdate in not the new birthdate entered, return error
                        if (!(testPatient.getBirthDate().equals(birthDate))) {
                            LOGGER.error("Post Birthdate update Patient History does not have correct birth date in the first entry");
                            return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Last updated birthdate is not reflected");
                        }

                    }

                    //Check oldBirthDate for 2nd iteration
                    if (iterations == 1) {

                        //If birthdate in second last version is not what it was before update, return error
                        if (!(testPatient.getBirthDate().equals(oldBirthDate))) {
                            LOGGER.error("Post Birthdate update Patient History does not have correct birth date in the second entry");
                            return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Birthdate in previous version is incorrect");
                        }

                        //No more iterations required as update values are confirmed in the first two
                        break;
                    }
                }

                //Increase Iteration count
                iterations++;
            }


            LOGGER.info("Creating a diagnostic report");
            //Create a diagnostic Report
            DiagnosticReport diagnosticReport = new DiagnosticReport();
            Reference patientReference = new Reference("Patient/" + patientId);
            diagnosticReport.setSubject(patientReference);


            // Submit the diagnosticReport to the FHIR server
            MethodOutcome reportEntryOutcome = client.create()
                    .resource(diagnosticReport)
                    .execute();


            //Check if diagnostic report is created
            if (!reportEntryOutcome.getCreated()) {
                LOGGER.error("Diagnostic report not created");
                return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Diagnostic Report generation error");
            }

            String diagnosticReportId = reportEntryOutcome.getResource().getIdElement().getIdPart();


            // Read the existing DiagnosticReport
            LOGGER.info("Fetching diagnostic report");
            DiagnosticReport existingReport = client
                    .read()
                    .resource(DiagnosticReport.class)
                    .withId(diagnosticReportId)
                    .execute();

            //Changing status to "UNKNOWN"
            LOGGER.info("Updating diagnostic report's status to 'UNKNOWN'");
            DiagnosticReport.DiagnosticReportStatus status = existingReport.getStatus();

            // Modify the status value
            existingReport.setStatus(DiagnosticReport.DiagnosticReportStatus.UNKNOWN);

            // Update the DiagnosticReport with the modified status
            MethodOutcome updatedReport = client
                    .update()
                    .resource(existingReport)
                    .execute();


            String diagnosticHistoryUrl = client.getServerBase() + "/DiagnosticReport/" + diagnosticReportId + "/_history";


            //Get diagnostic report history
            LOGGER.info("Fetching diagnostic report history");
            Bundle diagnosticReportHistory = client.search().byUrl(diagnosticHistoryUrl).returnBundle(Bundle.class).execute();


            //Check if diagnostic report history is empty
            if (diagnosticReportHistory.getEntry() == null) {
                LOGGER.error("Post Update diagnostic report history has no entries");
                return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Diagnostic Report History is empty");
            }


            if (diagnosticReportHistory.getTotal() < 2) {
                LOGGER.error("Post Update Diagnostic report history has less than 2 entries");
//                return new ValidationResultInfo("testSHRF7TestCase1",ErrorLevel.ERROR,"Diagnostic Report History does not required number of versions");
            }


            //Set iterations to 0 to keep a count of how many entries have been checked
            iterations = 0;

            Date currentLastUpdatedDate = new Date();

            //Loop through the entries in report history
            //Checking for change in status and timestamp
            for (Bundle.BundleEntryComponent entry : diagnosticReportHistory.getEntry()) {
                if (entry.getResource() instanceof DiagnosticReport currentReport) {

                    //If first entry does not show current status value, then return error
                    if (iterations == 0) {
                        if (!(currentReport.getStatus().equals(DiagnosticReport.DiagnosticReportStatus.UNKNOWN))) {
                            LOGGER.error("Post update Diagnostic Report History does not have 'UNKNOWN' status in the last version");
                            return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Latest Version does not show change in status");
                        }
                        currentLastUpdatedDate = currentReport.getMeta().getLastUpdated();
                    }

                    //If second entry does not show value stored in status, return error
                    else if (iterations == 1) {
                        if (!(currentReport.getStatus() == status)) {
                            LOGGER.error("Post update Diagnostic Report History does not have correct status in the second last version");
                            return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Second last version shows changes");
                        }
                        if (!((currentLastUpdatedDate.compareTo(currentReport.getMeta().getLastUpdated())) > 0)) {
                            LOGGER.error("Post update Diagnostic Report History does not have it's last updated time after the update time of second last version");
                            return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Last Updated Time is not correct");
                        }
                        break;
                    }
                    iterations++;
                }
            }


            // Use the IGenericClient to delete the DiagnosticReport
            LOGGER.info("Deleting diagnostic report");
            MethodOutcome deleteDiagnosticReport = client.delete()
                    .resourceById("DiagnosticReport", diagnosticReportId)
                    .execute();


            //Get diagnostic report history
            LOGGER.info("Fetching diagnostic report history");
            Bundle postDeletionDiagnosticReportHistory = client.search().byUrl(diagnosticHistoryUrl).returnBundle(Bundle.class).execute();

            if (postDeletionDiagnosticReportHistory.getTotal() < 3) {
                LOGGER.error("Post deletion diagnostic history has less than 3 entries");
                return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Diagnostic Report History does not have required number of versions ====== 3");
            }


            iterations = 0;
            Date deletionTime = new Date();


            //Check if data is stored in pre-deletion versions
            for (Bundle.BundleEntryComponent entry : postDeletionDiagnosticReportHistory.getEntry()) {
                if (iterations == 0) {
                    deletionTime = postDeletionDiagnosticReportHistory.getMeta().getLastUpdated();

                    //Check if latest entry is for Deletion
                    if (!(entry.getRequest().getMethod() == Bundle.HTTPVerb.DELETE)) {
                        LOGGER.error("Post deletion diagnostic history does not use 'DELETE' method in last entry");
                        return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Delete method not used");
                    }
                }
                if (entry.getResource() instanceof DiagnosticReport postDeletionDiagnosticReport) {

                    if (iterations == 1) {

                        //Check if second last entry has id, status and last updated time is before deletion time
                        if (!(postDeletionDiagnosticReport.hasId()
                                && postDeletionDiagnosticReport.hasStatus()
                                && (postDeletionDiagnosticReport.getMeta().getLastUpdated().compareTo(deletionTime)) < 0
                        )) {
                            LOGGER.error("Post deletion diagnostic history does not have id or status or deletion time is not after second last entry time");
                            return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "Error in pre-deletion data storage or last updated time");
                        }
                        break;
                    }
                }
                iterations++;
            }


            //if all checks are avoided successfully, return test passed
            LOGGER.info("Test Case Passed");
            return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.OK, "Passed");


        } catch (Exception ex) {

            //Exception returns failed operation
            return new ValidationResultInfo("testSHRF7TestCase1", ErrorLevel.ERROR, "OPERATION FAILED");
        }
    }
}

