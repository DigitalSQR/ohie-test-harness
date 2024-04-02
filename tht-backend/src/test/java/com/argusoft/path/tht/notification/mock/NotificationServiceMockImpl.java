package com.argusoft.path.tht.notification.mock;

import com.argusoft.path.tht.notificationmanagement.models.dto.NotificationInfo;
import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.notificationmanagement.models.mapper.NotificationMapper;
import com.argusoft.path.tht.notificationmanagement.repository.NotificationRepository;
import com.argusoft.path.tht.usermanagement.mock.UserServiceMockImpl;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class NotificationServiceMockImpl {

    @Autowired
    NotificationMapper notificationMapper;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceMockImpl userServiceMock;


    public void init() {
        userServiceMock.init();
        createNotification("notification.02",  "notification.status.unread", "Notification message 2", "user.01");
        createNotification("notification.04", "notification.status.archived", "Notification message 3", "user.01");
        createNotification("notification.05", "notification.status.archived", "message 4", "user.01");
        createNotification("notification.06", "notification.status.unread", "message 5", "user.01");
    }

    public NotificationInfo createNotification(String id, String state, String message, String receiverId) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setId(id);
        notificationEntity.setState(state);
        notificationEntity.setCreatedBy("ivasiwala");
        notificationEntity.setUpdatedBy("ivasiwala");
        notificationEntity.setCreatedAt(new Date());
        notificationEntity.setUpdatedAt(new Date());
        notificationEntity.setMessage(message);
        notificationEntity.setReceiver(userRepository.findById(receiverId).get());
        notificationRepository.save(notificationEntity);
        return notificationMapper.modelToDto(notificationEntity);
    }

    public void clear() {
        notificationRepository.deleteAll();
        notificationRepository.flush();
        userServiceMock.clear();
    }

}