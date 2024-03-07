package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.sharedhealthrecord;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
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
public class SHRF5TestCase3 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(SHRF5TestCase3.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {
            String testCaseName = this.getClass().getSimpleName();
            LOGGER.info("Start testing " + testCaseName);

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_SHARED_HEALTH_RECORD_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            Patient patient = FHIRUtils.createPatient("Doe", "John", "male", "1990-01-01", "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "9414473", "555-555-5555", "john.doe@example.com", client);
            MethodOutcome patientOutcome = client.create().resource(patient).execute();
            if (!patientOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Patient");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Patient");
            }
            String patientReference = "Patient/" + patientOutcome.getId().getIdPart();

            Practitioner practitionerOne = FHIRUtils.createPractitioner("Walter", "male", "12-05-2001", "9414", "555-555-5555");
            MethodOutcome practitionerOneOutcome = client.create().resource(practitionerOne).execute();
            if (!practitionerOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Practitioner");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Practitioner");

            }
            String practitionerOneReference = "Practitioner/" + practitionerOneOutcome.getId().getIdPart();

            Practitioner practitionerTwo = FHIRUtils.createPractitioner("Mike", "male", "14-05-1999", "8114", "555-555-5555");
            MethodOutcome practitionerTwoOutcome = client.create().resource(practitionerTwo).execute();
            if (!practitionerTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating Practitioner");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Practitioner");

            }
            String practitionerTwoReference = "Practitioner/" + practitionerTwoOutcome.getId().getIdPart();

            Observation observationOne = FHIRUtils.createObservation(patientOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), "Code", 23.3);
            MethodOutcome observationOneOutcome = client.create().resource(observationOne).execute();

            if (!observationOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating ObservationOne");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Observation");

            }
            String observationOneReference = "Observation/" + observationOneOutcome.getId().getIdPart();

            Observation observationTwo = FHIRUtils.createObservation(patientOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), "Code", 24.4);
            MethodOutcome observationTwoOutcome = client.create().resource(observationTwo).execute();
            if (!observationTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed when creating ObservationTwo");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Observation");

            }
            String observationTwoReference = "Observation/" + observationTwoOutcome.getId().getIdPart();

            DiagnosticReport diagnosticReportOne = FHIRUtils.createDiagnosticReport(patientOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), observationOneOutcome.getId().getIdPart());
            MethodOutcome diagnosticReportOneOutcome = client.create().resource(diagnosticReportOne).execute();

            if (!diagnosticReportOneOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed while creating first Diagnostic Report");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Diagnostic Report");

            }
            DiagnosticReport diagnosticReportTwo = FHIRUtils.createDiagnosticReport(patientOutcome.getId().getIdPart(), practitionerTwoOutcome.getId().getIdPart(), observationTwoOutcome.getId().getIdPart());
            MethodOutcome diagnosticReportTwoOutcome = client.create().resource(diagnosticReportTwo).execute();

            if (!diagnosticReportTwoOutcome.getCreated()) {
                LOGGER.error(testCaseName + "Testcase Failed while creating second Diagnostic Report");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create Diagnostic Report");
            }


            Bundle bundle = (Bundle) client.search().forResource(DiagnosticReport.class)
                    .where(new ReferenceClientParam("patient").hasId(patientOutcome.getId().getIdPart()))
                    .prettyPrint()
                    .execute();
            int expectedTotal = 2;
            int actualTotal = bundle.getTotal();
            if (actualTotal != expectedTotal) {

                LOGGER.error(testCaseName + "Testcase Failed because expected " + expectedTotal + " entries, but found " + actualTotal);
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed because expected " + expectedTotal + " entries, but found " + actualTotal);
            }
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                Resource resource = entry.getResource();

                // Check if the resource is a DiagnosticReport
                if (resource instanceof DiagnosticReport diagnosticReport) {

                    // Validate subject reference
                    Reference subjectRef = diagnosticReport.getSubject();
                    if (!subjectRef.getReference().equals(patientReference)) {

                        LOGGER.error(testCaseName + "Testcase Failed because of invalid subject reference");
                        return new ValidationResultInfo(ErrorLevel.ERROR, "Failed because of invalid subject reference");

                    }

                    // Validate performer reference
                    boolean performerFound = false;
                    List<Reference> performers = diagnosticReport.getPerformer();
                    for (Reference performerRef : performers) {
                        if (performerRef.getReference().startsWith(practitionerOneReference) ||
                                performerRef.getReference().startsWith(practitionerTwoReference)) {
                            performerFound = true;
                            break;
                        }
                    }
                    if (!performerFound) {

                        LOGGER.error(testCaseName + "Testcase Failed because Performer reference not found");
                        return new ValidationResultInfo(ErrorLevel.ERROR, "Failed because Performer reference not found");

                    }

                    // Validate result reference
                    boolean resultFound = false;
                    List<Reference> results = diagnosticReport.getResult();
                    for (Reference resultRef : results) {
                        if (resultRef.getReference().startsWith(observationOneReference) ||
                                resultRef.getReference().startsWith(observationTwoReference)) {
                            resultFound = true;
                            break;
                        }
                    }
                    if (!resultFound) {

                        LOGGER.error(testCaseName + "Testcase Failed because Result reference not found");
                        return new ValidationResultInfo(ErrorLevel.ERROR, "Failed because Result reference not found");
                    }
                } else {

                    LOGGER.error(testCaseName + "Testcase Failed because Entry is not a DiagnosticReport resource");
                    return new ValidationResultInfo(ErrorLevel.ERROR, "Failed because Entry is not a DiagnosticReport resource");
                }
            }

            LOGGER.info(testCaseName + "Testcase successfully passed!");
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");
        }
        catch (Exception ex)
        {
            LOGGER.error(ValidateConstant.OPERATION_FAILED_EXCEPTION + SHRF5TestCase3.class.getSimpleName(), ex);
            throw new OperationFailedException(ex.getMessage(),ex);
        }
    }
}