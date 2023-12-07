package com.argusoft.path.tht.usermanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateMetaEntity;

import javax.persistence.*;

@Entity
@Table(name = "token_verification")
public class TokenVerificationEntity extends IdStateMetaEntity {

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
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
