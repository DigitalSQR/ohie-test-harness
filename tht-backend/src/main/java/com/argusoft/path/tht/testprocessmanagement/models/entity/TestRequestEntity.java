package com.argusoft.path.tht.testprocessmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This model is mapped to testRequest table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "testRequest")
public class TestRequestEntity extends IdStateNameMetaEntity {

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "assessee_id")
    private UserEntity assessee;


    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private UserEntity approver;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<TestRequestUrlEntity> testRequestUrls;

    @Column(name = "message")
    private String message;

    @OneToMany(mappedBy = "testRequest", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<TestRequestValueEntity> testRequestValues;

    public TestRequestEntity() {
    }

    public TestRequestEntity(TestRequestEntity testRequestEntity) {
        super(testRequestEntity);

        testRequestEntity.setMessage(testRequestEntity.getMessage());

        if (testRequestEntity.getAssessee() != null) {
            this.setAssessee(new UserEntity(testRequestEntity.getAssessee().getId()));
        }
        if (testRequestEntity.getApprover() != null) {
            this.setApprover(new UserEntity(testRequestEntity.getApprover().getId()));
        }
        if (testRequestEntity.getTestRequestUrls() != null) {
            this.setTestRequestUrls(testRequestEntity.getTestRequestUrls().stream().map(TestRequestUrlEntity::new).collect(Collectors.toSet()));
        }
        if (testRequestEntity.getTestRequestValues() != null) {
            this.setTestRequestValues(testRequestEntity.getTestRequestValues().stream().map(TestRequestValueEntity::new).collect(Collectors.toSet()));
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TestRequestEntity(String id) {
        this.setId(id);
    }

    public UserEntity getAssessee() {
        return assessee;
    }

    public void setAssessee(UserEntity assessee) {
        this.assessee = assessee;
    }

    public UserEntity getApprover() {
        return approver;
    }

    public void setApprover(UserEntity approver) {
        this.approver = approver;
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

    public Set<TestRequestValueEntity> getTestRequestValues() {
        if(testRequestValues == null){
            testRequestValues = new HashSet<>();
        }
        return testRequestValues;
    }

    public void setTestRequestValues(Set<TestRequestValueEntity> testRequestValues) {
        this.testRequestValues = testRequestValues;
    }

    @PrePersist
    private void changesBeforeSaveTestRequest() {
        this.getTestRequestUrls().stream().forEach(testRequestUrlEntity -> testRequestUrlEntity.setTestRequestId(this.getId()));
        this.getTestRequestValues().stream().forEach(testRequestValueEntity -> testRequestValueEntity.setTestRequest(this));
    }



    @Override
    public String toString() {
        return "TestRequestEntity{" +
                "id=" + getId() +
                '}';
    }
}
