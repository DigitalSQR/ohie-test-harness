package com.argusoft.path.tht.systemconfiguration.audit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomApplicationEventPublisher {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    private static final String REMOTE_ADDRESS  = "remoteAddress";

    public void publishLogonEvent(String principal, String remoteAddress, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put(REMOTE_ADDRESS, remoteAddress);
        data.put("message", message);
        this.applicationEventPublisher.publishEvent(
                new AuditApplicationEvent(
                        principal,
                        CustomApplicationEventListener.LOGON_SUCCESS_EVENT,
                        data
                ));
    }

    public void publishLogoffEvent(String principal, String remoteAddress, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put(REMOTE_ADDRESS, remoteAddress);
        data.put("message", message);
        this.applicationEventPublisher.publishEvent(
                new AuditApplicationEvent(
                        principal,
                        CustomApplicationEventListener.LOGOFF_SUCCESS_EVENT,
                        data
                ));
    }

    public void publishPermissionDeniedEvent(String message, String principal, String remoteAddress) {
        Map<String, Object> data = new HashMap<>();
        data.put(REMOTE_ADDRESS, remoteAddress);
        data.put("message", message);
        this.applicationEventPublisher.publishEvent(
                new AuditApplicationEvent(
                        principal,
                        CustomApplicationEventListener.PERMISSION_DENIED_EVENT,
                        data
                ));
    }

    public void publishOperationFailedEvent(String message, String principal, String remoteAddress) {
        Map<String, Object> data = new HashMap<>();
        data.put(REMOTE_ADDRESS, remoteAddress);
        data.put("message", message);
        this.applicationEventPublisher.publishEvent(
                new AuditApplicationEvent(
                        principal,
                        CustomApplicationEventListener.OPERATION_FAILED_EVENT,
                        data
                ));
    }
}
