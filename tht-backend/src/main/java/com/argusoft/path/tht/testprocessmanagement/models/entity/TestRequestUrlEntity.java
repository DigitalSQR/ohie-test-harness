package com.argusoft.path.tht.testprocessmanagement.models.entity;

import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * This model is mapped to testRequestUrl table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "test_request_url")
@IdClass(TestRequestUrlEntityId.class)
public class TestRequestUrlEntity {

    @Id
    @Column(name = "test_request_id")
    private String testRequestId;

    @Id
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id")
    private ComponentEntity component;

    @Column(name = "fhir_api_base_url")
    private String fhirApiBaseUrl;

    //TODO:Add LoginType String column (Basic username passowrd, OAuth2)
    //username password -> username, password
    //OAuth2 -> username, password, clientSecret, clientId, Login URL

    @Column(name = "loginType")
    private String loginType;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

    @Column(name = "clientId")
    private String clientId;
    @Column(name = "clientSecret")
    private String clientSecret;

    @Column(name = "loginUrl")
    private String loginUrl;
    @Column(name = "fhir_version")
    private String fhirVersion;

    @Column(name = "website_ui_base_url")
    private String websiteUIBaseUrl;

    @Column(name = "request_param_name")
    private String headerParamName;

    @Column(name = "request_param_value")
    private String headerParamValue;

    public TestRequestUrlEntity() {

    }

    @Override
    public String toString() {
        return "TestRequestUrlEntity{" +
                "testRequestId='" + testRequestId + '\'' +
                ", component=" + component +
                ", fhirApiBaseUrl='" + fhirApiBaseUrl + '\'' +
                ", loginType='" + loginType + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", loginUrl='" + loginUrl + '\'' +
                ", fhirVersion='" + fhirVersion + '\'' +
                ", websiteUIBaseUrl='" + websiteUIBaseUrl + '\'' +
                ", headerParamName='" + headerParamName + '\'' +
                ", headerParamValue='" + headerParamValue + '\'' +
                '}';
    }

    public TestRequestUrlEntity(TestRequestUrlEntity testRequestUrlEntity) {
        this.setTestRequestId(testRequestUrlEntity.getTestRequestId());
        if (testRequestUrlEntity.getComponent() != null) {
            this.setComponent(new ComponentEntity(testRequestUrlEntity.getComponent().getId()));
        }
        this.setFhirApiBaseUrl(testRequestUrlEntity.getFhirApiBaseUrl());
        this.setUsername(testRequestUrlEntity.getUsername());
        this.setPassword(testRequestUrlEntity.getPassword());
        this.setFhirVersion(testRequestUrlEntity.getFhirVersion());
        this.setWebsiteUIBaseUrl(testRequestUrlEntity.getWebsiteUIBaseUrl());
        this.setClientId(testRequestUrlEntity.getClientId());
        this.setClientSecret(testRequestUrlEntity.getClientSecret());
        this.setLoginUrl(testRequestUrlEntity.getLoginUrl());
        this.setLoginType(testRequestUrlEntity.getLoginType());

        if(testRequestUrlEntity.getHeaderParamName() != null){
            this.setHeaderParamName(testRequestUrlEntity.getHeaderParamName());
        }

        if(testRequestUrlEntity.getHeaderParamValue() != null){
            this.setHeaderParamValue(testRequestUrlEntity.getHeaderParamValue());
        }
    }

    public String getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(String testRequestId) {
        this.testRequestId = testRequestId;
    }

    public ComponentEntity getComponent() {
        return component;
    }

    public void setComponent(ComponentEntity component) {
        this.component = component;
    }

    public String getFhirApiBaseUrl() {
        return fhirApiBaseUrl;
    }

    public void setFhirApiBaseUrl(String fhirApiBaseUrl) {
        this.fhirApiBaseUrl = fhirApiBaseUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
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

    public String getHeaderParamName() {
        return headerParamName;
    }

    public void setHeaderParamName(String headerParamName) {
        this.headerParamName = headerParamName;
    }

    public String getHeaderParamValue() {
        return headerParamValue;
    }

    public void setHeaderParamValue(String headerParamValue) {
        this.headerParamValue = headerParamValue;
    }
}