/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This model is mapped to user table in database.
 *
 * @author Dhruv
 */
@Entity
@Table(name = "component")
public class ComponentEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @OneToMany(mappedBy = "component", cascade = {}, fetch = FetchType.LAZY)
    private Set<SpecificationEntity> specifications;

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

    public void setSpecifications(Set<SpecificationEntity> specifications) {
        this.specifications = specifications;
    }
}
