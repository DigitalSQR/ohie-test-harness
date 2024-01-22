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
 * This model is mapped to specification table in database.
 *
 * @author Dhruv
 */
@Entity
@Table(name = "specification")
public class SpecificationEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "is_functional")
    private Boolean isFunctional;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "component_id")
    private ComponentEntity component;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "specification", cascade = {})
    private Set<TestcaseEntity> testcases;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Boolean getIsFunctional() {
        return isFunctional;
    }

    public void setIsFunctional(Boolean isFunctional) {
        this.isFunctional = isFunctional;
    }

    public ComponentEntity getComponent() {
        return component;
    }

    public void setComponent(ComponentEntity component) {
        this.component = component;
    }

    public Set<TestcaseEntity> getTestcases() {
        if (testcases == null) {
            testcases = new HashSet<>();
        }
        return testcases;
    }

    public void setTestcases(Set<TestcaseEntity> testcases) {
        this.testcases = testcases;
    }
}
