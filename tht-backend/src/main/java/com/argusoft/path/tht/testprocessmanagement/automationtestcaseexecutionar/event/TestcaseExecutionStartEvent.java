package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.event;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

/**
 * TestcaseExecutionerStartEvent
 *
 * @author Hardik
 */

public class TestcaseExecutionStartEvent extends ApplicationEvent {

    private final List<String> testcaseResultEntityIds;
    private final String refId;
    private final String refObjUri;
    private final Boolean isWorkflow;
    private final Boolean isFunctional;
    private final Boolean isRequired;
    private final Boolean isRecommended;
    private final ContextInfo contextInfo;

    public TestcaseExecutionStartEvent(Object testRequestId, List<String> testcaseResultEntityIds, String refId, String refObjUri, Boolean isWorkflow, Boolean isFunctional, Boolean isRequired, Boolean isRecommended, ContextInfo contextInfo) {
        super(testRequestId);
        this.testcaseResultEntityIds = testcaseResultEntityIds;
        this.refId = refId;
        this.refObjUri = refObjUri;
        this.isWorkflow = isWorkflow;
        this.isFunctional = isFunctional;
        this.isRequired = isRequired;
        this.isRecommended = isRecommended;
        this.contextInfo = contextInfo;
    }

    public List<String> getTestcaseResultEntityIds() {
        return testcaseResultEntityIds;
    }

    public String getRefId() {
        return refId;
    }

    public String getRefObjUri() {
        return refObjUri;
    }

    public Boolean getWorkflow() {
        return isWorkflow;
    }

    public Boolean getFunctional() {
        return isFunctional;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public Boolean getRecommended() {
        return isRecommended;
    }
    public ContextInfo getContextInfo() {
        return contextInfo;
    }
}
