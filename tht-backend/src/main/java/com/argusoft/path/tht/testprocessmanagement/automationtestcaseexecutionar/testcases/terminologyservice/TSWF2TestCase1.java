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
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TSWF2TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(TSWF2TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
            ContextInfo contextInfo) throws OperationFailedException {
        try {
            LOGGER.info("Start testing TSWF1TestCase1");

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            LOGGER.info("Creating CodeSystem");

            String codeSystemUrl = "http://example.com/CodeSystem/mycodesystem";
            String codeToValidate = "12345";

            // checking if codeSystem exist or not
            Bundle codeSystemList = client.search()
                    .forResource(CodeSystem.class)
                    .where(CodeSystem.URL.matches().value(codeSystemUrl))
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

            // creating a codeSystem
            CodeSystem codeSystem = FHIRUtils.createCodeSystem(codeSystemUrl, "1.0.0", "MyCodeSystem", "Example Code System", "ACTIVE", "HL7 International / Terminology Infrastructure", "COMPLETE", "12345", "Blood Pressure", "A measurement of the force of blood against the walls of the arteries.");
            MethodOutcome outcome = client.create()
                    .resource(codeSystem)
                    .execute();

            // check if codeSystem got created
            if (!outcome.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            // search if valueSet already present
            String valueSetUrl = "http://example.com/ValueSet/example-valueset";
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
            ValueSet valueSet = FHIRUtils.createValueSet(valueSetUrl, "ExampleValueSet", "Example ValueSet Title", "ACTIVE", "HL7 International");

            //adding concept in valueSet
            FHIRUtils.addConceptValueSet(valueSet, codeSystemUrl, "12345", "Blood Pressure");

            MethodOutcome outcome1 = client.create()
                    .resource(valueSet)
                    .execute();

            if (!outcome1.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create valueSet");
            }

            // Validate against ValueSet
            Parameters validationParameters = new Parameters()
                    .addParameter("system", new UriType(codeSystemUrl))
                    .addParameter("code", new CodeType(codeToValidate))
                    .addParameter("url", new UriType(valueSetUrl));

            Parameters validationResults = client
                    .operation()
                    .onType(ValueSet.class)
                    .named("$validate-code")
                    .withParameters(validationParameters)
                    .execute();

            if (!validationResults.getParameterBool("result")) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to perform validate-code operation");
            }

            LOGGER.info("testTSWF1Case1 Testcase successfully passed!");
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");

        } catch (Exception ex) {
            LOGGER.error(ValidateConstant.EXCEPTION + TSWF1TestCase1.class.getSimpleName(), ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }

    }
}
