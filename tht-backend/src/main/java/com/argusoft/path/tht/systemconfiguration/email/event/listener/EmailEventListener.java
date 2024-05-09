package com.argusoft.path.tht.systemconfiguration.email.event.listener;

import com.argusoft.path.tht.systemconfiguration.email.event.EmailEvent;
import com.argusoft.path.tht.systemconfiguration.email.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.mail.MessagingException;

/**
 * Email Event Listener
 *
 * @author Ali
 */
@Component
public class EmailEventListener {

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Async
    @TransactionalEventListener
    public void mailUser(EmailEvent emailEvent) throws MessagingException {
        String receiver = (String) emailEvent.getSource();
        messageService.sendMessage(receiver, emailEvent.getSubject(), emailEvent.getHtmlContent());
    }
}
