/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdInfo;

import java.io.Serializable;

/**
 * This info is for User DTO that contains all the Role model's data.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class RoleInfo extends IdInfo implements Serializable {

    //    @ApiModelProperty(notes = "The name for User model",
//            allowEmptyValue = false,
//            example = "quick user",
//            dataType = "String",
//            required = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
