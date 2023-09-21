/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.models.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * This class is provides implementation for IdMetaInfo.
 *
 * @author dhruv
 */
public class IdMetaInfo extends HasMetaInfo {

    @ApiModelProperty(notes = "The unique id of the model",
            allowEmptyValue = false,
            example = "tht.state.viapointinfo.active",
            dataType = "String",
            required = false)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
