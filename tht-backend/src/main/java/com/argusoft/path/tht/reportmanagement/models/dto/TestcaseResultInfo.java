/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This info is for TestcaseResult DTO that contains all the TestcaseResult model's data.
 *
 * @author Dhruv
 */
public class TestcaseResultInfo extends IdStateNameMetaInfo implements Serializable {

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
    
    @ApiModelProperty(notes = "The isManual of the TestcaseResult",
            allowEmptyValue = false,
            example = "false",
            dataType = "Boolean",
            required = true)
    private Boolean isManual;

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
}
