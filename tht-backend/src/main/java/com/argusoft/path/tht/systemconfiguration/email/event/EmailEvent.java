package com.argusoft.path.tht.systemconfiguration.email.event;

import org.springframework.context.ApplicationEvent;

/**
 * Create Notification Event
 *
 * @author Ali
 */
public class EmailEvent extends ApplicationEvent {

    private final String subject;

    private final String htmlContent;

    public EmailEvent(Object receiver, String subject, String htmlContent) {
        super(receiver);
        this.subject = subject;
        this.htmlContent = htmlContent;
    }

    public String getSubject() { return subject; }

    public String getHtmlContent() { return htmlContent; }
}
