package com.argusoft.path.tht.systemconfiguration.models.entity;

import jakarta.persistence.*;

@MappedSuperclass
public class IdStateMetaEntity extends MetaEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "state")
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
