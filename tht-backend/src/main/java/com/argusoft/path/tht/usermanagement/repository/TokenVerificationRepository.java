package com.argusoft.path.tht.usermanagement.repository;

import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenVerificationRepository extends JpaRepository<TokenVerificationEntity,String> {

    @Query("SELECT e FROM TokenVerificationEntity e where e.id = (:id) AND e.userInfo.id = (:userId) AND e.state = (:state)")
    Optional<TokenVerificationEntity> findActiveTokenByIdAndUserInfo(@Param("state") String state , @Param("id") String id, @Param("userId") String userId);

}
