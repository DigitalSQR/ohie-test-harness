package com.argusoft.path.tht.usermanagement.repository;

import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * This repository is for making queries on the TokenVerification model.
 *
 * @author Hardik
 */
public interface TokenVerificationRepository extends JpaRepository<TokenVerificationEntity, String> {

    @Query("SELECT e FROM TokenVerificationEntity e where e.id = (:id) AND e.userEntity.id = (:userId) AND e.state = (:state) AND e.type = (:type)")
    Optional<TokenVerificationEntity> findActiveTokenByIdAndUserAndType(@Param("state") String state, @Param("id") String id, @Param("userId") String userId, @Param("type") String type);

    @Query("SELECT e FROM TokenVerificationEntity e where e.userEntity.id = (:userId) and e.type = (:type)")
    List<TokenVerificationEntity> findAllTokenVerificationsByTypeAndUser(@Param("type") String type, @Param("userId") String userId);

}
