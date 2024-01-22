/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;

import javax.persistence.*;

/**
 * This model is mapped to user table in database.
 *
 * @author Dhruv
 */
@Entity
@Table(name = "testcase_result")
public class TestcaseResultEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "tester_id")
    private UserEntity tester;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "parent_test_case_result_id")
    private TestcaseResultEntity parentTestcaseResult;

    @Column(name = "ref_obj_uri")
    private String refObjUri;

    @Column(name = "ref_id")
    private String refId;

    @Column(name = "message")
    private String message;

    @Column(name = "test_request_id")
    private String testRequestId;

    @Column(name = "has_system_error")
    private Boolean hasSystemError;

    @Column(name = "is_manual")
    private Boolean isManual;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "testcase_option_id")
    private TestcaseOptionEntity testcaseOption;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public UserEntity getTester() {
        return tester;
    }

    public void setTester(UserEntity tester) {
        this.tester = tester;
    }

    public String getRefObjUri() {
        return refObjUri;
    }

    public void setRefObjUri(String refObjUri) {
        this.refObjUri = refObjUri;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getHasSystemError() {
        return hasSystemError;
    }

    public void setHasSystemError(Boolean hasSystemError) {
        this.hasSystemError = hasSystemError;
    }

    public String getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(String testRequestId) {
        this.testRequestId = testRequestId;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public TestcaseOptionEntity getTestcaseOption() {
        return testcaseOption;
    }

    public void setTestcaseOption(TestcaseOptionEntity testcaseOption) {
        this.testcaseOption = testcaseOption;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public TestcaseResultEntity getParentTestcaseResult() {
        return parentTestcaseResult;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public void setParentTestcaseResult(TestcaseResultEntity parentTestcaseResult) {
        this.parentTestcaseResult = parentTestcaseResult;
    }
}
