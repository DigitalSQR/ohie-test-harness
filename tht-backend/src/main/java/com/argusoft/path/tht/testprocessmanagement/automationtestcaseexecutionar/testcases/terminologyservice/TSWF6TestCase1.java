package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.terminologyservice;

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
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ValueSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class TSWF6TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(TSWF6TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
            ContextInfo contextInfo) throws OperationFailedException {
        try {
            LOGGER.info("Start testing TSWF6TestCase1");

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            LOGGER.info("Creating CodeSystem");
            // Creating codeSystem
            if (Boolean.FALSE.equals(Objects.requireNonNull(addCodeSystem(client, "http://example.com/gender", "1.0.0", "Gender", "Codes for gender", "ACTIVE", "HL7 International / Terminology Infrastructure", "COMPLETE", "male", "Male", "HL7-defined gender codes")).getCreated())) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            LOGGER.info("Creating ValueSet");
            // Creating valueSet
            if (Boolean.FALSE.equals(Objects.requireNonNull(addValueSet(client, "http://example.com/ValueSet/example-valueset", "ExampleValueSet", "Example ValueSet Title", "ACTIVE", "HL7 International / Terminology Infrastructure", "http://example.com/gender", "12345", "Blood Pressure")).getCreated())) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create valueSet");
            }

            // Creating valueSet
            if (Boolean.FALSE.equals(Objects.requireNonNull(addValueSet(client, "http://example.com/ValueSet/example-valueset2", "ExampleValueSet2", "Example ValueSet Title2", "ACTIVE", "HTHE International", "http://example.com/gender", "12345", "Blood Pressure")).getCreated())) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create valueSet");
            }

            // Publisher name to search for
            String publisherName = "HL7 International / Terminology Infrastructure";

            // Use $search to search for CodeSystem by publisher
            Bundle searchResults = client
                    .search()
                    .forResource(ValueSet.class)
                    .where(ValueSet.PUBLISHER.matches().value(publisherName))
                    .returnBundle(Bundle.class)
                    .execute();

            // checking if it contains codeSystem with particular publisherName
            if (isValueSetPresent(searchResults, "http://example.com/ValueSet/example-valueset") && !isValueSetPresent(searchResults, "http://example.com/ValueSet/example-valueset2")) {
                LOGGER.info("testTSWF6Case1 Testcase successfully passed!");
                return new ValidationResultInfo(ErrorLevel.OK, "Passed");
            }

            return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to perform query code in valueSet");

        } catch (Exception ex) {
            LOGGER.error(ValidateConstant.EXCEPTION + TSWF6TestCase1.class.getSimpleName(), ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }

    }

    public static MethodOutcome addCodeSystem(IGenericClient client, String url, String version, String name, String title, String status, String publisher, String content, String code, String display, String definition) {

        // checking if codeSystem exist or not
        Bundle codeSystemList = client.search()
                .forResource(CodeSystem.class)
                .where(CodeSystem.URL.matches().value(url))
                .and(CodeSystem.VERSION.exactly().code(version))
                .returnBundle(Bundle.class)
                .execute();

        // checking if any element present in codeSystemList and deleting it
        if (codeSystemList.hasEntry()) {
            for (Bundle.BundleEntryComponent entry : codeSystemList.getEntry()) {
                CodeSystem codeSystem = (CodeSystem) entry.getResource();
                String id = codeSystem.getIdElement().getIdPart();
                client.delete().resourceById("CodeSystem", id).execute();
            }
        }

        CodeSystem codeSystem = FHIRUtils.createCodeSystem(url, version, name, title, status, publisher, content, code, display, definition);
        MethodOutcome outcome = client.create()
                .resource(codeSystem)
                .execute();

        return outcome;
    }

    public static MethodOutcome addValueSet(IGenericClient client, String valueSetUrl, String name, String title, String status, String publisherName, String codeSystemUrl, String code, String display) {
        // search if valueSet already present
        Bundle valueSetList = client.search()
                .forResource(ValueSet.class)
                .where(ValueSet.URL.matches().value(valueSetUrl))
                .returnBundle(Bundle.class)
                .execute();

        // checking if any element present in codeSystemList and deleting it
        if (valueSetList.hasEntry()) {
            for (Bundle.BundleEntryComponent entry : valueSetList.getEntry()) {
                ValueSet valueSet = (ValueSet) entry.getResource();
                String id = valueSet.getIdElement().getIdPart();
                client.delete().resourceById("ValueSet", id).execute();
            }
        }

        //creating new valueSet
        ValueSet valueSet = FHIRUtils.createValueSet(valueSetUrl, name, title, status, publisherName);

        //adding concept in valueSet
        FHIRUtils.addConceptValueSet(valueSet, codeSystemUrl, code, display);

        MethodOutcome outcome = client.create()
                .resource(valueSet)
                .execute();

        return outcome;
    }

    public static boolean isValueSetPresent(Bundle searchResults, String url) {
        if (searchResults.hasEntry()) {
            for (Bundle.BundleEntryComponent entry : searchResults.getEntry()) {
                ValueSet valueSet = (ValueSet) entry.getResource();
                if (valueSet.getUrl().equalsIgnoreCase(url)) {
                    return true;
                }
            }
        }

        return false;
    }
}
