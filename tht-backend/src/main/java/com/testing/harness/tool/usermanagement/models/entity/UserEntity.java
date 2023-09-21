/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testing.harness.tool.usermanagement.models.entity;

import com.testing.harness.tool.systemconfiguration.models.entity.IdMetaEntity;

import javax.persistence.*;

/**
 * This model is mapped to user table in database.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Entity
@Table(name = "tht_user")
public class UserEntity extends IdMetaEntity {

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;
    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
