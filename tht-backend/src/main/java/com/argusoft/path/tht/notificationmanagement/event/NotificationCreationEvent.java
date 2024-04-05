package com.argusoft.path.tht.notificationmanagement.event;

import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.context.ApplicationEvent;

/**
 * Create Notification Event
 *
 * @author Ali
 */
public class NotificationCreationEvent extends ApplicationEvent {
    private final ContextInfo contextInfo;

    public NotificationCreationEvent(Object notificationEntity, ContextInfo contextInfo) {
        super(notificationEntity);
        this.contextInfo = contextInfo;
    }

    public ContextInfo getContextInfo() {
        return contextInfo;
    }

}
