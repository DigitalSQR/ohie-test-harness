package com.argusoft.path.tht.testprocessmanagement.models.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * This info is for TestRequestUrl DTO that contains all the TestRequestUrl model's data.
 *
 * @author Dhruv
 */
public class TestRequestUrlInfo {

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
    private String fhirApiBaseUrl;

    @ApiModelProperty(notes = "The website/UI url of the testRequestUrl",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String websiteUIBaseUrl;

    @ApiModelProperty(notes = "The fhirVersion of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String fhirVersion;

    public TestRequestUrlInfo() {
    }

    public TestRequestUrlInfo(String componentId, String fhirApiBaseUrl, String username, String password, String fhirVersion, String websiteUIBaseUrl) {
        this.componentId = componentId;
        this.username = username;
        this.password = password;
        this.fhirApiBaseUrl = fhirApiBaseUrl;
        this.fhirVersion = fhirVersion;
        this.websiteUIBaseUrl = websiteUIBaseUrl;
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

    public String getFhirApiBaseUrl() {
        return fhirApiBaseUrl;
    }

    public void setFhirApiBaseUrl(String fhirApiBaseUrl) {
        this.fhirApiBaseUrl = fhirApiBaseUrl;
    }

    public String getFhirVersion() {
        return fhirVersion;
    }

    public void setFhirVersion(String fhirVersion) {
        this.fhirVersion = fhirVersion;
    }

    public String getWebsiteUIBaseUrl() {
        return websiteUIBaseUrl;
    }

    public void setWebsiteUIBaseUrl(String websiteUIBaseUrl) {
        this.websiteUIBaseUrl = websiteUIBaseUrl;
    }
}
