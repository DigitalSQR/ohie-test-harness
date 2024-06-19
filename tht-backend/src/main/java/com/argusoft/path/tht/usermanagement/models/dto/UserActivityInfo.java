package com.argusoft.path.tht.usermanagement.models.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

public class UserActivityInfo {

    @ApiModelProperty(notes = "The unique id of the model",
            allowEmptyValue = false,
            example = "userId",
            dataType = "String",
            required = false)
    private String id;


    @ApiModelProperty(
            notes = "The access time",
            allowEmptyValue = false,
            example = "2024-06-04 10:15:30",
            dataType = "Date",
            required = true
    )
    private Date accessTime;

    @ApiModelProperty(
            notes = "The user ID",
            allowEmptyValue = false,
            example = "12345",
            dataType = "String",
            required = true
    )
    private String userId;

    @ApiModelProperty(
            notes = "The API URL",
            allowEmptyValue = false,
            example = "/api/v1/resource",
            dataType = "String",
            required = true
    )
    private String apiUrl;

    @ApiModelProperty(
            notes = "The scope of the request (e.g., POST, PUT)",
            allowEmptyValue = false,
            example = "POST",
            dataType = "String",
            required = true
    )
    private String scope;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
