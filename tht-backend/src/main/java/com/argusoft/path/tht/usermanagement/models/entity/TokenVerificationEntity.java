package com.argusoft.path.tht.usermanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdMetaEntity;
import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateMetaEntity;
import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "token_verification")
public class TokenVerificationEntity extends IdStateMetaEntity {

    @OneToOne
    @Column(name = "user_id")
    private UserEntity userEntity;


    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
