package com.argusoft.path.tht.notificationmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateMetaEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;

import javax.persistence.*;

/**
 * This model is mapped to notification table in database.
 *
 * @author Ali
 */
@Entity
@Table(name = "notification")
public class NotificationEntity extends IdStateMetaEntity {

    @Column(name = "message")
    private String message;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private UserEntity receiver;

    public NotificationEntity() {}

    public NotificationEntity(String message, UserEntity receiver) {
        this.message = message;
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "message='" + message + '\'' +
                ", receiver=" + receiver +
                '}';
    }
}
