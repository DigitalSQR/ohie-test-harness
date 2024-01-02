package com.argusoft.path.tht.systemconfiguration.models.entity;

import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.UUID;

/**
 * This model used in model those contain UUID as id, state, name and description.
 *
 * @author Dhruv
 * =
 */
@MappedSuperclass
public class IdStateNameMetaEntity extends MetaEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", length = 1000)
    private String name;

    @Column(name = "state")
    private String state;

    @Column(name = "description", length = 1000)
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @PrePersist
    private void changesBeforeSave() {
        if (StringUtils.isEmpty(id)) {
            this.setId(UUID.randomUUID().toString());
        }
    }
}
