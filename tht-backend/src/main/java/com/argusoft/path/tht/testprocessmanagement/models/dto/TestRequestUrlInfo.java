/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.models.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This info is for TestRequestUrl DTO that contains all the TestRequestUrl model's data.
 *
 * @author Dhruv
 */
public class TestRequestUrlInfo implements Serializable {

    @ApiModelProperty(notes = "The componentId of the testRequestUrl",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String componentId;

    @ApiModelProperty(notes = "The username of the testRequestUrl",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String username;

    @ApiModelProperty(notes = "The password of the testRequestUrl",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String password;

    @ApiModelProperty(notes = "The url of the testRequestUrl",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String baseUrl;

    @ApiModelProperty(notes = "The fhirVersion of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String fhirVersion;

    public TestRequestUrlInfo() {
    }

    public TestRequestUrlInfo(String componentId, String baseUrl, String username, String password, String fhirVersion) {
        this.componentId = componentId;
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
        this.fhirVersion = fhirVersion;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getFhirVersion() {
        return fhirVersion;
    }

    public void setFhirVersion(String fhirVersion) {
        this.fhirVersion = fhirVersion;
    }
}
