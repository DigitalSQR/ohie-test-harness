package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.springframework.stereotype.Component;

@Component
public class CRF7TestCase1 implements TestCase {
    @Override
    public ValidationResultInfo test(IGenericClient client, ContextInfo contextInfo) throws OperationFailedException {

        try {
            OperationOutcome operationOutcome = new OperationOutcome();
            operationOutcome.addIssue().setSeverity(OperationOutcome.IssueSeverity.ERROR).setDiagnostics("An error occur").setCode(OperationOutcome.IssueType.PROCESSING);
            MethodOutcome outcome = client.create().resource(operationOutcome).execute();
            String operationOutcomeId = outcome.getId().getIdPart();

            if (outcome.getCreated()) {
                // Retrieving the created OperationOutcome
                OperationOutcome resultOperationOutcome = client.read().resource(OperationOutcome.class).withId(operationOutcomeId).execute();

                // Checking issues for severity, diagnostics, and code
                for (OperationOutcome.OperationOutcomeIssueComponent issue : resultOperationOutcome.getIssue()) {
                    if (issue.hasSeverity() && issue.getSeverity() == OperationOutcome.IssueSeverity.ERROR &&
                            issue.hasDiagnostics() && issue.getDiagnostics().equals("An error occur") && issue.hasCode() &&
                            issue.getCode() == OperationOutcome.IssueType.PROCESSING) {
                        return new ValidationResultInfo("testCRF7case1", ErrorLevel.OK, "Passed");
                    } else {
                        return new ValidationResultInfo("testCRF7case1", ErrorLevel.ERROR, "Failed because operation outcome data is not matched");
                    }
                }
                // Additional return statement to handle the case where the loop does not execute
                return new ValidationResultInfo("testCRF7case1", ErrorLevel.ERROR, "Failed because operationOutcome is not found");
            } else {
                return new ValidationResultInfo("testCRF7case1", ErrorLevel.ERROR, "failed because OperationOutcome was not created");
            }

        } catch (Exception ex) {
            throw new OperationFailedException(ex.getMessage(), ex);
        }
    }
}
