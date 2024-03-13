package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CRF7TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(CRF7TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) throws OperationFailedException {

        try {
            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_CLIENT_REGISTRY_ID);
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            OperationOutcome operationOutcome = new OperationOutcome();
            operationOutcome.addIssue().setSeverity(OperationOutcome.IssueSeverity.ERROR).setDiagnostics("An error occur").setCode(OperationOutcome.IssueType.PROCESSING);
            MethodOutcome outcome = client.create().resource(operationOutcome).execute();
            String operationOutcomeId = outcome.getId().getIdPart();

            if (outcome.getCreated()) {
                // Retrieving the created OperationOutcome
                OperationOutcome resultOperationOutcome = client.read().resource(OperationOutcome.class).withId(operationOutcomeId).execute();

                // Checking issues for severity, diagnostics, and code
                for (OperationOutcome.OperationOutcomeIssueComponent issue : resultOperationOutcome.getIssue()) {
                    if (issue.hasSeverity() && issue.getSeverity() == OperationOutcome.IssueSeverity.ERROR
                            && issue.hasDiagnostics() && issue.getDiagnostics().equals("An error occur") && issue.hasCode()
                            && issue.getCode() == OperationOutcome.IssueType.PROCESSING) {
                        return new ValidationResultInfo(ErrorLevel.OK, "Passed");
                    } else {
                        return new ValidationResultInfo(ErrorLevel.ERROR, "Failed because operation outcome data is not matched");
                    }
                }
                // Additional return statement to handle the case where the loop does not execute
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed because operationOutcome is not found");
            } else {
                return new ValidationResultInfo(ErrorLevel.ERROR, "failed because OperationOutcome was not created");
            }

        } catch (Exception ex) {
            LOGGER.error(ValidateConstant.OPERATION_FAILED_EXCEPTION + CRF7TestCase1.class.getSimpleName(), ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
