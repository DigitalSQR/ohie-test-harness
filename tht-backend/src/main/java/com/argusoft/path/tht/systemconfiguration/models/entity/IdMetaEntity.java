/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.models.entity;

import jakarta.persistence.*;

/**
 * This model used in model those contains UUID as id.
 *
 * @author dhruv
 * @since 2021-2-25
 */
@MappedSuperclass
public class IdMetaEntity extends MetaEntity {

    @Id
    @Column(name = "id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
