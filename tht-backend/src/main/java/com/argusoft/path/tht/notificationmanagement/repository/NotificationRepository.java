package com.argusoft.path.tht.notificationmanagement.repository;

import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the Notification model.
 *
 * @author Ali
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String>, JpaSpecificationExecutor<NotificationEntity> {

}
