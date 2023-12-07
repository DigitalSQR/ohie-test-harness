package com.argusoft.path.tht.systemconfiguration.models.dto;

import io.swagger.annotations.ApiModelProperty;

public class IdStateNameMetaInfo extends HasMetaInfo {

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

    @ApiModelProperty(notes = "This shows the name of the record",
            allowEmptyValue = true,
            example = "name",
            dataType = "String",
            required = false)
    private String name;

    @ApiModelProperty(notes = "This shows the description of the record",
            allowEmptyValue = true,
            example = "description of the record",
            dataType = "String",
            required = false)
    private String description;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
