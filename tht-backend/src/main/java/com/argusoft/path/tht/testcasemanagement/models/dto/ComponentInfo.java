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
 * This info is for Component DTO that contains all the Component model's data.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class ComponentInfo extends IdStateNameMetaInfo implements Serializable {

    @ApiModelProperty(notes = "The unique order of the component",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = true)
    private Integer order;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
