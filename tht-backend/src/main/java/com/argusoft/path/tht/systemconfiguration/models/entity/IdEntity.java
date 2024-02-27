package com.argusoft.path.tht.systemconfiguration.models.entity;

import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.UUID;

/**
 * This model used in model those contain UUID as id.
 *
 * @author Dhruv
 */
@MappedSuperclass
public class IdEntity {

    @Id
    @Column(name = "id")
    private String id;

    public IdEntity() {
    }

    public IdEntity(IdEntity idEntity) {
        this.setId(idEntity.getId());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PrePersist
    private void changesBeforeSave() {
        if (!StringUtils.hasLength(id)) {
            this.setId(UUID.randomUUID().toString());
        }
    }

}
