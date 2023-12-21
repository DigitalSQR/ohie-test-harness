/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This info is for Component DTO that contains all the Component model's data.
 *
 * @author Dhruv
 */
public class TestRequestInfo extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The evaluationVersionId of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String evaluationVersionId;

    @ApiModelProperty(notes = "The assesseeId of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String assesseeId;

    @ApiModelProperty(notes = "The productName of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String productName;

    @ApiModelProperty(notes = "The approverId of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String approverId;

    @ApiModelProperty(notes = "The fhirVersion of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String fhirVersion;

    public String getEvaluationVersionId() {
        return evaluationVersionId;
    }

    public void setEvaluationVersionId(String evaluationVersionId) {
        this.evaluationVersionId = evaluationVersionId;
    }

    public String getAssesseeId() {
        return assesseeId;
    }

    public void setAssesseeId(String assesseeId) {
        this.assesseeId = assesseeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getFhirVersion() {
        return fhirVersion;
    }

    public void setFhirVersion(String fhirVersion) {
        this.fhirVersion = fhirVersion;
    }
}
