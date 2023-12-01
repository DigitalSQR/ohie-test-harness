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
 * This info is for Specification DTO that contains all the Specification model's data.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class SpecificationInfo extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The unique order of the specification",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = true)
    private Integer order;

    @ApiModelProperty(notes = "The isFunctional of the testcase",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = false)
    private Boolean isFunctional;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getFunctional() {
        return isFunctional;
    }

    public void setFunctional(Boolean functional) {
        isFunctional = functional;
    }
}
