package com.argusoft.path.tht.usermanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdEntity;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class RoleEntity extends IdEntity {

    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "RoleEntity{"
                + "id=" + this.getId()
                + ", name=" + this.getName()
                + '}';
    }

}