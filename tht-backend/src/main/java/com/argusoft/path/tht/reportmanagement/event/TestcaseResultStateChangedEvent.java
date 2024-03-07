package com.argusoft.path.tht.reportmanagement.event;

import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.context.ApplicationEvent;

public class TestcaseResultStateChangedEvent extends ApplicationEvent {

    private final ContextInfo contextInfo;

    private final Boolean isManual;

    public TestcaseResultStateChangedEvent(Object testcaseResultId, Boolean isManual, ContextInfo contextInfo) {
        super(testcaseResultId);
        this.contextInfo = contextInfo;
        this.isManual = isManual;
    }


    public ContextInfo getContextInfo() {
        return contextInfo;
    }


    public Boolean getManual() {
        return isManual;
    }
}
