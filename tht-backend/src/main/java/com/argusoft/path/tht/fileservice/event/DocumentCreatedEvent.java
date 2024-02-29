package com.argusoft.path.tht.fileservice.event;

import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.springframework.context.ApplicationEvent;

public class DocumentCreatedEvent extends ApplicationEvent {
    private final ContextInfo contextInfo;
    public DocumentCreatedEvent(Object source, ContextInfo contextInfo) {
        super(source);
        this.contextInfo = contextInfo;
    }

    public ContextInfo getContextInfo() {
        return contextInfo;
    }
}
