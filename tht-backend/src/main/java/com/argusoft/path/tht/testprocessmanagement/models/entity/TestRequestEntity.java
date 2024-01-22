/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * This model is mapped to testRequest table in database.
 *
 * @author Dhruv
 */
@Entity
@Table(name = "testRequest")
public class TestRequestEntity extends IdStateNameMetaEntity {

    @Column(name = "evaluation_version_id")
    private String evaluationVersionId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "assessee_id")
    private UserEntity assessee;

    @Column(name = "product_name")
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "approver_id")
    private UserEntity approver;

    @Column(name = "fhir_version")
    private String fhirVersion;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Set<TestRequestUrlEntity> testRequestUrls;

    public String getEvaluationVersionId() {
        return evaluationVersionId;
    }

    public void setEvaluationVersionId(String evaluationVersionId) {
        this.evaluationVersionId = evaluationVersionId;
    }

    public UserEntity getAssessee() {
        return assessee;
    }

    public void setAssessee(UserEntity assessee) {
        this.assessee = assessee;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UserEntity getApprover() {
        return approver;
    }

    public void setApprover(UserEntity approver) {
        this.approver = approver;
    }

    public String getFhirVersion() {
        return fhirVersion;
    }

    public void setFhirVersion(String fhirVersion) {
        this.fhirVersion = fhirVersion;
    }

    public Set<TestRequestUrlEntity> getTestRequestUrls() {
        if (testRequestUrls == null) {
            testRequestUrls = new HashSet<>();
        }
        return testRequestUrls;
    }

    public void setTestRequestUrls(Set<TestRequestUrlEntity> testRequestUrls) {
        this.testRequestUrls = testRequestUrls;
    }

    @PrePersist
    private void changesBeforeSave() {
        if (StringUtils.isEmpty(this.getId())) {
            this.setId(UUID.randomUUID().toString());
            this.getTestRequestUrls().stream().forEach(testRequestUrlEntity -> testRequestUrlEntity.setTestRequestId(this.getId()));
        }
    }
}
