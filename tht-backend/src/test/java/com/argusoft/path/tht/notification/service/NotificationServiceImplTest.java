package com.argusoft.path.tht.notification.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.notification.mock.NotificationServiceMockImpl;
import com.argusoft.path.tht.notificationmanagement.constant.NotificationServiceConstants;
import com.argusoft.path.tht.notificationmanagement.filter.NotificationCriteriaSearchFilter;
import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.notificationmanagement.service.NotificationService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


class NotificationServiceImplTest extends TestingHarnessToolTestConfiguration {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationServiceMockImpl notificationServiceMockImpl;

    @Autowired
    private UserService userService;

    private ContextInfo contextInfo;

    @BeforeEach
    @Override
    public void init() {
        super.init();
        notificationServiceMockImpl.init();

        // Set context info
        contextInfo = new ContextInfo("dummyuser1@testmail.com", "user.01", "password", true, true, true, true, new ArrayList<>());
        contextInfo.setCurrentDate(new Date());


    }

    @AfterEach
    void after() {
        notificationServiceMockImpl.clear();
    }

    @Test
    void testCreateNotification() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException {

        // Test 1
        NotificationEntity notification1 = new NotificationEntity();
        notification1.setId("notification.01");
        notification1.setMessage("create notification message");

        UserEntity user = userService.getUserById("user.01", contextInfo);
        notification1.setReceiver(user);

        NotificationEntity resultNotification1 = notificationService.createNotification(notification1, contextInfo);
        assertEquals("create notification message", resultNotification1.getMessage());

    }


    @Test
    @Transactional
    public void testUpdateNotification() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        //  Test case 1 : Update the notification data

        NotificationEntity notification2 = notificationService.getNotificationById("notification.02", contextInfo);
//          Before Update
        assertEquals("Notification message 2", notification2.getMessage());

        notification2.setMessage("Updated notification message 2");

        NotificationEntity updatedNotification = notificationService.updateNotification(notification2, contextInfo);

        // After update
        assertEquals("Updated notification message 2", updatedNotification.getMessage());


        // Test case 2 : Given notification id does not exist
        NotificationEntity notification3 = new NotificationEntity();
        notification3.setId("notification.03");
        assertThrows(DataValidationErrorException.class, () -> {
            notificationService.updateNotification(notification3, contextInfo);
        });

    }


    @Test
    @Transactional
    public void testUpdateNotificationState() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        // Test case 1 : Update the notification state

        NotificationEntity notification2 = notificationService.getNotificationById("notification.02", contextInfo);

        // Before Update
        assertEquals(NotificationServiceConstants.NOTIFICATION_STATUS_UNREAD, notification2.getState());

        NotificationEntity updatedNotification = notificationService.changeState(notification2.getId(), NotificationServiceConstants.NOTIFICATION_STATUS_ARCHIVED, contextInfo);

        // After update
        assertEquals(NotificationServiceConstants.NOTIFICATION_STATUS_ARCHIVED, updatedNotification.getState());


        // Test case 2 : State is invalid

        NotificationEntity notification3 = notificationService.getNotificationById("notification.04", contextInfo);

        // Before Update
        assertEquals(NotificationServiceConstants.NOTIFICATION_STATUS_ARCHIVED, notification3.getState());

        assertThrows(DataValidationErrorException.class, () -> {
            notificationService.changeState(notification3.getId(), "notification.status.test", contextInfo);
        });

    }


    @Test
    void testGetNotificationById() throws InvalidParameterException, DoesNotExistException {

        // Test case 1: Passing notification id as null
        assertThrows(InvalidParameterException.class, () -> {
            notificationService.getNotificationById(null, contextInfo);
        });

        // Test case 2: Notification data does not exist with given id
        assertThrows(DoesNotExistException.class, () -> {
            notificationService.getNotificationById("notification.15", contextInfo);
        });

        // Test case 3: Notification data exist with given id
        NotificationEntity finalNotification = notificationService.getNotificationById("notification.04", contextInfo);
        assertEquals("notification.04", finalNotification.getId());

    }

    @Test
    void testSearchNotification() throws InvalidParameterException, OperationFailedException {

        // Test case 1: Search notification by state.

        NotificationCriteriaSearchFilter notificationSearchFilter3 = new NotificationCriteriaSearchFilter();

        List<String> notificationStates3 = new ArrayList<>();
        notificationStates3.add(NotificationServiceConstants.NOTIFICATION_STATUS_ARCHIVED);

        notificationSearchFilter3.setState(notificationStates3);

        List<NotificationEntity> notificationEntities3 = notificationService.searchNotifications(notificationSearchFilter3, contextInfo);
        assertEquals(2, notificationEntities3.size());

    }

}