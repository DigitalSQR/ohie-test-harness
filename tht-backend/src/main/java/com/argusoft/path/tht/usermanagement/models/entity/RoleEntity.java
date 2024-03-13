package com.argusoft.path.tht.usermanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdEntity;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This model is mapped to role table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "role")
public class RoleEntity extends IdEntity {

    @Column(name = "name")
    private String name;

    public RoleEntity() {
    }

    public RoleEntity(RoleEntity roleEntity) {
        super(roleEntity);
        this.setName(roleEntity.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RoleEntity{"
                + "id=" + this.getId()
                + ", name=" + this.getName()
                + '}';
    }

}
