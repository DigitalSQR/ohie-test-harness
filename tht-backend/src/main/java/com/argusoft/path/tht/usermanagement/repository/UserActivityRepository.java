package com.argusoft.path.tht.usermanagement.repository;

import com.argusoft.path.tht.usermanagement.models.entity.UserActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserActivityRepository extends JpaRepository<UserActivityEntity, String>, JpaSpecificationExecutor<UserActivityEntity> {

}
