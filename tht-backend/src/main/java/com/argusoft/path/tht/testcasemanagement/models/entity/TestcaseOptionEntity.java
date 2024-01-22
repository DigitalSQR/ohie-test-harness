/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;

import javax.persistence.*;

/**
 * This model is mapped to testcase_option table in database.
 *
 * @author Dhruv
 */
@Entity
@Table(name = "testcase_option")
public class TestcaseOptionEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "testcase_id")
    private TestcaseEntity testcase;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public TestcaseEntity getTestcase() {
        return testcase;
    }

    public void setTestcase(TestcaseEntity testcase) {
        this.testcase = testcase;
    }
}
