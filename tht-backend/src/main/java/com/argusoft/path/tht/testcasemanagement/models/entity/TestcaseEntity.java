/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;

import javax.persistence.*;

/**
 * This model is mapped to testcase table in database.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Entity
@Table(name = "testcase")
public class TestcaseEntity extends IdStateNameMetaEntity {

    @Column(name = "order")
    private Integer order;

    @Column(name = "is_manual")
    private Boolean isManual;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "bean_name")
    private String beanName;

    @ManyToOne
    @JoinColumn(name = "specification_id")
    private SpecificationEntity specification;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public SpecificationEntity getSpecification() {
        return specification;
    }

    public void setSpecification(SpecificationEntity specification) {
        this.specification = specification;
    }
}
