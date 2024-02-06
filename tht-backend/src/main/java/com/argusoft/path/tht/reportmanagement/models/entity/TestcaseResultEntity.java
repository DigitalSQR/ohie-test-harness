/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
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

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "tester_id")
    private UserEntity tester;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_test_case_result_id")
    private TestcaseResultEntity parentTestcaseResult;

    @Column(name = "ref_obj_uri")
    private String refObjUri;

    @Column(name = "ref_id")
    private String refId;

    @Column(name = "message")
    private String message;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "test_request_id")
    private TestRequestEntity testRequest;

    @Column(name = "has_system_error")
    private Boolean hasSystemError;

    @Column(name = "is_manual")
    private Boolean isManual;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "is_automated")
    private Boolean isAutomated;

    @Column(name = "is_recommended")
    private Boolean isRecommended;

    @Column(name = "is_workflow")
    private Boolean isWorkflow;

    @Column(name = "is_functional")
    private Boolean isFunctional;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "testcase_option_id")
    private TestcaseOptionEntity testcaseOption;

    @Column(name = "duration")
    private Long duration;

    @Transient
    private String grade;

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

    public TestRequestEntity getTestRequest() {
        return testRequest;
    }

    public void setTestRequest(TestRequestEntity testRequest) {
        this.testRequest = testRequest;
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

    public void setParentTestcaseResult(TestcaseResultEntity parentTestcaseResult) {
        this.parentTestcaseResult = parentTestcaseResult;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getAutomated() {
        return isAutomated;
    }

    public void setAutomated(Boolean automated) {
        isAutomated = automated;
    }

    public Boolean getRecommended() {
        return isRecommended;
    }

    public void setRecommended(Boolean recommended) {
        isRecommended = recommended;
    }

    public Boolean getWorkflow() {
        return isWorkflow;
    }

    public void setWorkflow(Boolean workflow) {
        isWorkflow = workflow;
    }

    public Boolean getFunctional() {
        return isFunctional;
    }

    public void setFunctional(Boolean functional) {
        isFunctional = functional;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
