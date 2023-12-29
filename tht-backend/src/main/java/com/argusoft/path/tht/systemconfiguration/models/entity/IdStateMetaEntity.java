package com.argusoft.path.tht.systemconfiguration.models.entity;

import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.UUID;

/**
 * This model used in model those contain UUID as id and state.
 *
 * @author Dhruv
 * =
 */
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

    @PrePersist
    private void changesBeforeSave() {
        if (!StringUtils.isEmpty(id)) {
            this.setId(UUID.randomUUID().toString());
        }
    }
}
