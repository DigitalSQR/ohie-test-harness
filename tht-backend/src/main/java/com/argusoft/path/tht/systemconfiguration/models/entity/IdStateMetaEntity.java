package com.argusoft.path.tht.systemconfiguration.models.entity;

import org.hibernate.envers.Audited;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.UUID;

/**
 * This model used in model those contain UUID as id and state.
 *
 * @author Dhruv =
 */
@MappedSuperclass
@Audited
public class IdStateMetaEntity extends MetaEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "state")
    private String state;

    public IdStateMetaEntity() {

    }

    public IdStateMetaEntity(IdStateMetaEntity idStateMetaEntity) {
        super(idStateMetaEntity);
        this.setId(idStateMetaEntity.getId());
        this.setState(idStateMetaEntity.getState());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "IdStateMetaEntity{"
                + "id='" + id + '\''
                + ", state='" + state + '\''
                + '}';
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @PrePersist
    private void changesBeforeSave() {
        if (!StringUtils.hasLength(id)) {
            this.setId(UUID.randomUUID().toString());
        }
    }
}
