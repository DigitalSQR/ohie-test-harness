package com.argusoft.path.tht.systemconfiguration.models.dto;

import io.swagger.annotations.ApiModelProperty;

public class IdStateMetaInfo extends HasMetaInfo {

    @ApiModelProperty(notes = "The unique id of the model",
            allowEmptyValue = false,
            example = "tht.state.viapointinfo.active",
            dataType = "String",
            required = false)
    private String id;

    @ApiModelProperty(notes = "This shows the state of the record",
            allowEmptyValue = false,
            example = "tht.state.viapointinfo.active",
            dataType = "String",
            required = false)
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
