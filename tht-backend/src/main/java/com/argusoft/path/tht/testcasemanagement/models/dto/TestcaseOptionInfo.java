/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This info is for Testcase DTO that contains all the Testcase model's data.
 *
 * @author Dhruv
 */
public class TestcaseOptionInfo extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The unique rank of the testcaseOption",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = true)
    private Integer rank;

    @ApiModelProperty(notes = "The isSuccess of the testcaseOption",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = false)
    private Boolean isSuccess;

    @ApiModelProperty(notes = "The testcaseId of the testcaseOption",
            allowEmptyValue = false,
            example = "testcaseId",
            dataType = "String",
            required = true)
    private String testcaseId;

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

    public String getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(String testcaseId) {
        this.testcaseId = testcaseId;
    }
}
