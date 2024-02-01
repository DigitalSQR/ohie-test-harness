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
 * @author Aastha
 */
public class TestResultOptionInfo extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The testcaseResultId of testResultOption",
            allowEmptyValue = false,
            example = "testcaseResultId",
            dataType = "String",
            required = true)
    private String testcaseResultId;

    @ApiModelProperty(notes = "The testcaseOptionId of testResultOption",
            allowEmptyValue = false,
            example = "testcaseOptionId",
            dataType = "String",
            required = true)
    private String testcaseOptionId;

    @ApiModelProperty(notes = "The version of testResultOption",
            allowEmptyValue = false,
            example = "1",
            dataType = "Long",
            required = true)
    private Long version;

    @ApiModelProperty(notes = "isSelected of testResultOption",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = true)
    private Boolean isSelected;

    public TestResultOptionInfo() {
    }

    public TestResultOptionInfo(String testcaseOptionId, String testcaseResultId, Long version, Boolean isSelected) {
        this.testcaseOptionId = testcaseOptionId;
        this.testcaseResultId = testcaseResultId;
        this.version = version;
        this.isSelected = isSelected;
    }

    public String getTestcaseResultId() {
        return testcaseResultId;
    }

    public void setTestcaseResultId(String testcaseResultId) {
        this.testcaseResultId = testcaseResultId;
    }

    public String getTestcaseOptionId() {
        return testcaseOptionId;
    }

    public void setTestcaseOptionId(String testcaseOptionId) {
        this.testcaseOptionId = testcaseOptionId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
