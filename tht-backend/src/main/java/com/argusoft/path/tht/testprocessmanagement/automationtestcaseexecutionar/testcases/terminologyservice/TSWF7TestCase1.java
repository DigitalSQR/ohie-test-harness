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

/**
 * Testcase For TSWF7TestCase1
 *
 * @author Rohit
 */

@Component
public class TSWF7TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(TSWF7TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap,
                                     ContextInfo contextInfo) throws OperationFailedException {
        try {

            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            String codeSystemVersion = "1.0.0";

            Bundle codeSystemList = client.search()
                    .forResource(CodeSystem.class)
                    .where(CodeSystem.URL.matches().value("http://example.com/example/mycodesystem"))
                    .and(CodeSystem.VERSION.exactly().code(codeSystemVersion))
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

            CodeSystem codeSystem = FHIRUtils.createCodeSystem("http://example.com/example/mycodesystem", "1.0.0", "MyCodeSystem", "Example Code System", "ACTIVE", "HL7 International / Terminology Infrastructure", "COMPLETE", "12345", "Blood Pressure", "A measurement of the force of blood against the walls of the arteries.");
            MethodOutcome outcome = client.create()
                    .resource(codeSystem)
                    .execute();

            // check if codeSystem got created
            if (Boolean.FALSE.equals(outcome.getCreated())) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            // create parameter for $lookup operation
            Parameters parameters = new Parameters();
            parameters.addParameter().setName("system").setValue(new UriType("http://example.com/example/mycodesystem"));
            parameters.addParameter().setName("code").setValue(new StringType("12345"));

            Parameters result = client
                    .operation()
                    .onType(CodeSystem.class)
                    .named("$lookup")
                    .withParameters(parameters)
                    .execute();

            if (result.hasParameter() && result.getParameter("display").getValue().toString().equals("Blood Pressure")) {
                return new ValidationResultInfo(ErrorLevel.OK, "Passed");
            }

            return new ValidationResultInfo(ErrorLevel.ERROR, "cannot perform $lookup");

        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + TSWF7TestCase1.class.getSimpleName(), e);
            throw new OperationFailedException(e.getMessage(), e);
        }
    }
}
