package com.argusoft.path.tht.reportmanagement.event;

import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.springframework.context.ApplicationEvent;

public class TestcaseResultAttributeEvent extends ApplicationEvent {

    private final ContextInfo contextInfo;

    public TestcaseResultAttributeEvent(Object testcaseResultId, ContextInfo contextInfo) {
        super(testcaseResultId);
        this.contextInfo = contextInfo;
    }

    public ContextInfo getContextInfo() {
        return contextInfo;
    }
}
