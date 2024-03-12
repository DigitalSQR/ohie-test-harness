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

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "fhir_version")
    private String fhirVersion;

    @Column(name = "website_ui_base_url")
    private String websiteUIBaseUrl;

    public TestRequestUrlEntity() {

    }

    public TestRequestUrlEntity(TestRequestUrlEntity testRequestUrlEntity) {
        this.setTestRequestId(testRequestUrlEntity.getTestRequestId());
        if(testRequestUrlEntity.getComponent()!=null){
            this.setComponent(new ComponentEntity(testRequestUrlEntity.getComponent().getId()));
        }
        this.setFhirApiBaseUrl(testRequestUrlEntity.getFhirApiBaseUrl());
        this.setUsername(testRequestUrlEntity.getUsername());
        this.setPassword(testRequestUrlEntity.getPassword());
        this.setFhirVersion(testRequestUrlEntity.getFhirVersion());
        this.setWebsiteUIBaseUrl(testRequestUrlEntity.getWebsiteUIBaseUrl());
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
}