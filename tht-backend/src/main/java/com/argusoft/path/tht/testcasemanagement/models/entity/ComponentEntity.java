package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This model is mapped to user table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "component")
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ComponentEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @JsonIgnore
    @OneToMany(mappedBy = "component", cascade = {})
//    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<SpecificationEntity> specifications;

    public ComponentEntity() {
    }

    public ComponentEntity(ComponentEntity componentEntity) {
        super(componentEntity);
        this.setRank(componentEntity.getRank());
        this.setSpecifications(componentEntity.getSpecifications().stream()
                .map(specification -> new SpecificationEntity(specification.getId())).collect(Collectors.toSet()));

    }

    public ComponentEntity(String id) {
        this.setId(id);
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Set<SpecificationEntity> getSpecifications() {
        if (specifications == null) {
            specifications = new HashSet<>();
        }
        return specifications;
    }

    @Override
    public String toString() {
        return "ComponentEntity{" +
                "id=" + this.getId() +
                ", name=" + this.getName() +
                ", rank=" + rank +
                ", specifications=" + specifications +
                '}';
    }

    public void setSpecifications(Set<SpecificationEntity> specifications) {
        this.specifications = specifications;
    }
}
