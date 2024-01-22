package com.argusoft.path.tht.usermanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateMetaEntity;

import javax.persistence.*;

/**
 * This model is mapped to token verification table in database.
 *
 * @author Hardik
 */
@Entity
@Table(name = "token_verification")
public class TokenVerificationEntity extends IdStateMetaEntity {

    @OneToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;

    @Column(name = "type", nullable = false)
    private String type;


    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
