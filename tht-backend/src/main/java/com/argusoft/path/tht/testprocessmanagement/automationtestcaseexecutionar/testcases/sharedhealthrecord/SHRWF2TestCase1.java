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
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SHRWF2TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(SHRWF2TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> isGenericClientMap, ContextInfo contextInfo) throws OperationFailedException {
        try {
            IGenericClient client = isGenericClientMap.get(ComponentServiceConstants.COMPONENT_SHARED_HEALTH_RECORD_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            Patient patient = FHIRUtils.createPatient("Doe", "John", "male", "1990-01-01", "urn:oid:1.3.6.1.4.1.21367.13.20.1000", "IHERED-994", true, "9414473", "555-555-5555", "john.doe@example.com", client);
            MethodOutcome patientOutcome = client.create().resource(patient).execute();
            if (!patientOutcome.getCreated()) {

                LOGGER.error("Failed to create a patient");
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create a patient");
            }

            LOGGER.info("Patient is created");

            Practitioner practitionerOne = FHIRUtils.createPractitioner("Walter", "male", "12-05-2001", "9414", "555-555-5555");
            MethodOutcome practitionerOneOutcome = client.create().resource(practitionerOne).execute();

            if (!practitionerOneOutcome.getCreated()) {

                LOGGER.error("Failed to create first practitioner");

                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create first practitioner");
            }

            LOGGER.info("First Practitioner is created");

            Practitioner practitionerTwo = FHIRUtils.createPractitioner("Mike", "male", "14-05-1999", "8114", "555-555-5555");
            MethodOutcome practitionerTwoOutcome = client.create().resource(practitionerTwo).execute();
            if (!practitionerTwoOutcome.getCreated()) {

                LOGGER.error("Failed to create second practitioner");

                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create second practitioner");

            }

            LOGGER.info("Created second Practitioner");

            DocumentReference documentReferenceOne = FHIRUtils.createDocumentReference("DocumentReference123", patientOutcome.getId().getIdPart(), practitionerOneOutcome.getId().getIdPart(), "\"https://example.com/documents/cda123", "Patient CDA Document First Hospital");
            MethodOutcome documentReferenceOneOutcome = client.create().resource(documentReferenceOne).execute();
            if (!documentReferenceOneOutcome.getCreated()) {

                LOGGER.error("Failed to create first document reference");

                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create first document reference");

            }

            LOGGER.info("First document reference created");

            DocumentReference documentReferenceTwo = FHIRUtils.createDocumentReference("DocumentReference124", patientOutcome.getId().getIdPart(), practitionerTwoOutcome.getId().getIdPart(), "\"https://example.com/documents/cda124", "Patient CDA Document Second Hospital");
            MethodOutcome documentReferenceTwoOutcome = client.create().resource(documentReferenceTwo).execute();
            if (!documentReferenceTwoOutcome.getCreated()) {

                LOGGER.error("Failed to create second document reference");

                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create second document reference");

            }

            LOGGER.info("Second document reference created");

            Bundle bundle = (Bundle) client.search().forResource(DocumentReference.class)
                    .where(new ReferenceClientParam("patient").hasId(patientOutcome.getId().getIdPart()))
                    .prettyPrint()
                    .execute();

            List<String> documentReferenceIds = new ArrayList<>();
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                if (entry.getResource() instanceof DocumentReference documentReference) {
                    String documentReferenceId = documentReference.getIdElement().getIdPart();
                    documentReferenceIds.add(documentReferenceId);
                }
            }
            for (String documentReferenceId : documentReferenceIds) {
                DocumentReference documentReference = client
                        .read()
                        .resource(DocumentReference.class)
                        .withId(documentReferenceId)
                        .execute();

                LOGGER.info("Reading document reference using document reference id");

                if (documentReference != null) {

                    boolean isCdaCompliant = isCdaCompliant(documentReference);

                    if (!isCdaCompliant) {

                        LOGGER.error("Not CDA Compliant");

                        return new ValidationResultInfo(ErrorLevel.ERROR, "Not CDA Compliant");
                    }
                } else {

                    LOGGER.error("Document reference not found");

                    return new ValidationResultInfo(ErrorLevel.ERROR, "Document reference not found");
                }
            }
            LOGGER.info("Test case SHRWF2 Passed");
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");
        } catch (Exception ex) {
            LOGGER.error(ValidateConstant.OPERATION_FAILED_EXCEPTION + SHRWF2TestCase1.class.getSimpleName(), ex);

            throw new OperationFailedException(ex.getMessage(), ex);
        }

    }

    // Method to check if the DocumentReference content is CDA compliant
    private static boolean isCdaCompliant(DocumentReference documentReference) {
        //CDA content type is "application/xml"
        return documentReference.getContent()
                .stream()
                .anyMatch(content -> "application/xml".equals(content.getAttachment().getContentType()));
    }
}
