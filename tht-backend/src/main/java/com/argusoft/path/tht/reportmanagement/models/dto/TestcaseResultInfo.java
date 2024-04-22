package com.argusoft.path.tht.reportmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * This info is for TestcaseResult DTO that contains all the TestcaseResult
 * model's data.
 *
 * @author Dhruv
 */
public class TestcaseResultInfo extends IdStateNameMetaInfo {

    @ApiModelProperty(notes = "The unique rank of the TestcaseResult",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = true)
    private Integer rank;

    @ApiModelProperty(notes = "The testerId of the TestcaseResult",
            allowEmptyValue = false,
            example = "testerId",
            dataType = "String",
            required = false)
    private String testerId;

    @ApiModelProperty(notes = "The refObjUri of the TestcaseResult",
            allowEmptyValue = false,
            example = "refObjUri",
            dataType = "String",
            required = true)
    private String refObjUri;

    @ApiModelProperty(notes = "The refId of the TestcaseResult",
            allowEmptyValue = false,
            example = "refId",
            dataType = "String",
            required = true)
    private String refId;

    @ApiModelProperty(notes = "The message of the TestcaseResult",
            allowEmptyValue = false,
            example = "message",
            dataType = "String",
            required = false)
    private String message;

    @ApiModelProperty(notes = "The failureMessage of the TestcaseResult",
            allowEmptyValue = false,
            example = "failureMessage",
            dataType = "String",
            required = false)
    private String failureMessage;

    @ApiModelProperty(notes = "The testRequestId of the TestcaseResult",
            allowEmptyValue = false,
            example = "testRequestId",
            dataType = "String",
            required = true)
    private String testRequestId;

    @ApiModelProperty(notes = "The hasSystemError of the TestcaseResult",
            allowEmptyValue = false,
            example = "false",
            dataType = "Boolean",
            required = true)
    private Boolean hasSystemError;

    @ApiModelProperty(notes = "The isRequired of the TestcaseResult",
            allowEmptyValue = false,
            example = "false",
            dataType = "Boolean",
            required = true)
    private Boolean isRequired;

    @ApiModelProperty(notes = "The isRecommended of the TestcaseResult",
            allowEmptyValue = false,
            example = "false",
            dataType = "Boolean",
            required = true)
    private Boolean isRecommended;

    @ApiModelProperty(notes = "The isManual of the TestcaseResult",
            allowEmptyValue = false,
            example = "false",
            dataType = "Boolean",
            required = true)
    private Boolean isManual;

    @ApiModelProperty(notes = "The isAutomated of the TestcaseResult",
            allowEmptyValue = false,
            example = "false",
            dataType = "Boolean",
            required = true)
    private Boolean isAutomated;

    @ApiModelProperty(notes = "The isSuccess of the testcaseOption",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = false)
    private Boolean isSuccess;

    @ApiModelProperty(notes = "The testcaseOptionId of the TestcaseResult",
            allowEmptyValue = false,
            example = "testcaseOptionId1",
            dataType = "String",
            required = true)
    private String testcaseOptionId;

    @ApiModelProperty(notes = "The parentTestcaseResultId of the TestcaseResult",
            allowEmptyValue = false,
            example = "parentTestcaseResultId1",
            dataType = "String",
            required = true)
    private String parentTestcaseResultId;

    @ApiModelProperty(notes = "The isFunctional of the TestcaseResult",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = true)
    private Boolean isFunctional;

    @ApiModelProperty(notes = "The isWorkflow of the TestcaseResult",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = true)
    private Boolean isWorkflow;

    @ApiModelProperty(notes = "The duration of the TestcaseResult in milliseconds",
            allowEmptyValue = true,
            example = "duration in milliseconds",
            dataType = "Long",
            required = false)
    private Long duration;

    @ApiModelProperty(notes = "The grade of the TestcaseResult",
            allowEmptyValue = true,
            example = "A",
            dataType = "String",
            required = false)
    private String grade;

    @ApiModelProperty(notes = "The testSessionId from testbed",
            allowEmptyValue = true,
            dataType = "String")
    private String testSessionId;

    @ApiModelProperty(notes = "The testcaseId of the testcaseResult",
            allowEmptyValue = true,
            dataType = "String")
    private String testcaseId;
    private Set<TestcaseResultAttributesInfo> testcaseResultAttributesEntities;

    public Set<TestcaseResultAttributesInfo> getTestcaseResultAttributesEntities() {
        return testcaseResultAttributesEntities;
    }

    public void setTestcaseResultAttributesEntities(Set<TestcaseResultAttributesInfo> testcaseResultAttributesEntities) {
        this.testcaseResultAttributesEntities = testcaseResultAttributesEntities;
    }

    public String getParentTestcaseResultId() {
        return parentTestcaseResultId;
    }

    public void setParentTestcaseResultId(String parentTestcaseResultId) {
        this.parentTestcaseResultId = parentTestcaseResultId;
    }

    public String getTestcaseOptionId() {
        return testcaseOptionId;
    }

    public void setTestcaseOptionId(String testcaseOptionId) {
        this.testcaseOptionId = testcaseOptionId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getTesterId() {
        return testerId;
    }

    public void setTesterId(String testerId) {
        this.testerId = testerId;
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

    public String getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(String testRequestId) {
        this.testRequestId = testRequestId;
    }

    public Boolean getHasSystemError() {
        return hasSystemError;
    }

    public void setHasSystemError(Boolean hasSystemError) {
        this.hasSystemError = hasSystemError;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
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

    public Boolean getRecommended() {
        return isRecommended;
    }

    public void setRecommended(Boolean recommended) {
        isRecommended = recommended;
    }

    public Boolean getAutomated() {
        return isAutomated;
    }

    public void setAutomated(Boolean automated) {
        isAutomated = automated;
    }

    public Boolean getFunctional() {
        return isFunctional;
    }

    public void setFunctional(Boolean functional) {
        isFunctional = functional;
    }

    public Boolean getWorkflow() {
        return isWorkflow;
    }

    public void setWorkflow(Boolean workflow) {
        isWorkflow = workflow;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTestSessionId() {
        return testSessionId;
    }

    public void setTestSessionId(String testSessionId) {
        this.testSessionId = testSessionId;
    }

    public String getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(String testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    @Override
    public String toString() {
        return "TestcaseResultInfo{"
                + "rank=" + rank
                + ", testerId='" + testerId + '\''
                + ", refObjUri='" + refObjUri + '\''
                + ", refId='" + refId + '\''
                + ", message='" + message + '\''
                + ", failureMessage='" + failureMessage + '\''
                + ", testRequestId='" + testRequestId + '\''
                + ", hasSystemError=" + hasSystemError
                + ", isRequired=" + isRequired
                + ", isRecommended=" + isRecommended
                + ", isManual=" + isManual
                + ", isAutomated=" + isAutomated
                + ", isSuccess=" + isSuccess
                + ", testcaseOptionId='" + testcaseOptionId + '\''
                + ", parentTestcaseResultId='" + parentTestcaseResultId + '\''
                + ", isFunctional=" + isFunctional
                + ", isWorkflow=" + isWorkflow
                + ", duration=" + duration
                + ", grade='" + grade + '\''
                + ", testSessionId='" + testSessionId + '\''
                + ", testcaseId='" + testcaseId + '\''
                + ", testcaseResultAttributesEntities=" + testcaseResultAttributesEntities
                + '}';
    }
}
