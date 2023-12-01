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
 * @author dhruv
 * @since 2023-09-13
 */
public class TestcaseInfo extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The unique order of the testcase",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = true)
    private Integer order;

    @ApiModelProperty(notes = "The className of the testcase",
            allowEmptyValue = false,
            example = "className",
            dataType = "String",
            required = false)
    private String className;

    @ApiModelProperty(notes = "The isManual of the testcase",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = false)
    private Boolean isManual;

    @ApiModelProperty(notes = "The isRequired of the testcase",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = false)
    private Boolean isRequired;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
}
