package com.argusoft.path.tht.testprocessmanagement.models.entity;

import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This model is mapped to testRequestUrl table in database.
 *
 * @author Dhruv
 */
@Entity
@Table(name = "test_request_url")
@IdClass(TestRequestUrlEntityId.class)
public class TestRequestUrlEntity implements Serializable {

    @Id
    @Column(name = "test_request_id")
    private String testRequestId;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "component_id")
    private ComponentEntity component;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "fhir_version")
    private String fhirVersion;

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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
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

}