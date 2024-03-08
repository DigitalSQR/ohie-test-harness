package com.argusoft.path.tht.systemconfiguration.audit.repository;

import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccessAuditRepository extends JpaRepository<UserAccessAuditEntity,Long> {

}
