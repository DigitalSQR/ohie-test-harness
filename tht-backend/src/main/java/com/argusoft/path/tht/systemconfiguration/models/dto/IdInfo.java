package com.argusoft.path.tht.systemconfiguration.models.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * This class is provides implementation for IdInfo.
 *
 * @author Dhruv
 */
public class IdInfo {

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

    public IdInfo(String id) {
        this.id = id;
    }
    public IdInfo(){

    }


}
