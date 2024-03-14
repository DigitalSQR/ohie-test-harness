package com.argusoft.path.tht.fileservice.event;

import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.context.ApplicationEvent;

/**
 * Document Created Event
 *
 * @author Hardik
 */

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
