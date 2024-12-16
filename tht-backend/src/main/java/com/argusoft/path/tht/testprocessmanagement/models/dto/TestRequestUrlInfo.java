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
    @ApiModelProperty(notes = "The loginType of the testRequestUrl",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String loginType;

    @ApiModelProperty(notes = "The username of the testRequestUrl",
            allowEmptyValue = true,
            example = "1",
            dataType = "String",
            required = true)
    private String username;

    @ApiModelProperty(notes = "The password of the testRequestUrl",
            allowEmptyValue = true,
            example = "1",
            dataType = "String",
            required = true)
    private String password;

    @ApiModelProperty(notes = "The clientId of the testRequestUrl",
            allowEmptyValue = true,
            example = "1",
            dataType = "String",
            required = true)
    private String clientId;
    @ApiModelProperty(notes = "The clientSecret of the testRequestUrl",
            allowEmptyValue = true,
            example = "1",
            dataType = "String",
            required = true)
    private String clientSecret;
    @ApiModelProperty(notes = "The loginUrl of the testRequestUrl",
            allowEmptyValue = true,
            example = "1",
            dataType = "String",
            required = true)
    private String loginUrl;
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

    public TestRequestUrlInfo(String componentId, String loginType, String username, String password, String clientId, String clientSecret, String loginUrl, String fhirApiBaseUrl, String websiteUIBaseUrl, String fhirVersion) {
        this.componentId = componentId;
        this.loginType = loginType;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.loginUrl = loginUrl;
        this.fhirApiBaseUrl = fhirApiBaseUrl;
        this.websiteUIBaseUrl = websiteUIBaseUrl;
        this.fhirVersion = fhirVersion;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getFhirApiBaseUrl() {
        return fhirApiBaseUrl;
    }

    public void setFhirApiBaseUrl(String fhirApiBaseUrl) {
        this.fhirApiBaseUrl = fhirApiBaseUrl;
    }

    public String getWebsiteUIBaseUrl() {
        return websiteUIBaseUrl;
    }

    public void setWebsiteUIBaseUrl(String websiteUIBaseUrl) {
        this.websiteUIBaseUrl = websiteUIBaseUrl;
    }

    public String getFhirVersion() {
        return fhirVersion;
    }

    public void setFhirVersion(String fhirVersion) {
        this.fhirVersion = fhirVersion;
    }
}
