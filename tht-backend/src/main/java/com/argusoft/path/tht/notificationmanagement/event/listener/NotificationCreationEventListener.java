package com.argusoft.path.tht.notificationmanagement.event.listener;

import com.argusoft.path.tht.notificationmanagement.event.NotificationCreationEvent;
import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.notificationmanagement.models.mapper.NotificationMapper;
import com.argusoft.path.tht.notificationmanagement.service.NotificationService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Notification Creation Event Listener
 *
 * @author Ali
 */
@Component
public class NotificationCreationEventListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationCreationEventListener.class);

    private NotificationService notificationService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private NotificationMapper notificationMapper;

    @Autowired
    public void setNotificationMapper(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Autowired
    public void setSimpMessagingTemplate(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Async
    @TransactionalEventListener
    public void notifyNotificationCreationWebSocket(NotificationCreationEvent notificationCreationEvent) {
        try {
            if (notificationCreationEvent.getSource() instanceof NotificationEntity notificationEntity) {
                notificationEntity = notificationService.createNotification(notificationEntity, notificationCreationEvent.getContextInfo());
                // WebSocket send notification message
                String destination = "/notification/" + notificationEntity.getReceiver().getId();
                simpMessagingTemplate.convertAndSend(destination, notificationMapper.modelToDto(notificationEntity));
            } else {
                LOGGER.error("Error getting source of Notification Entity in NotificationCreationListener");
            }
        } catch (InvalidParameterException | OperationFailedException | DoesNotExistException |
                 DataValidationErrorException e) {
            LOGGER.error("Caught Exception while creating and sending notification to web socket ", e);
        }
    }
}
