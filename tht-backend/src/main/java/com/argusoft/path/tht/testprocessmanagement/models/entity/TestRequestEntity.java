package com.argusoft.path.tht.testprocessmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.hibernate.envers.Audited;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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

    @Column(name = "product_name")
    private String productName;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private UserEntity approver;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<TestRequestUrlEntity> testRequestUrls;

    public TestRequestEntity() {
    }

    public TestRequestEntity(TestRequestEntity testRequestEntity) {
        super(testRequestEntity);
        if(testRequestEntity.getAssessee()!=null){
            this.setAssessee(new UserEntity(testRequestEntity.getAssessee()));
        }
        this.setProductName(testRequestEntity.getProductName());
        if(testRequestEntity.getApprover()!=null){
            this.setApprover(new UserEntity(testRequestEntity.getApprover()));
        }
        if(testRequestEntity.getTestRequestUrls()!=null){
            this.setTestRequestUrls(testRequestEntity.getTestRequestUrls().stream().map(TestRequestUrlEntity::new).collect(Collectors.toSet()));
        }
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
        if (!StringUtils.hasLength(this.getId())) {
            this.setId(UUID.randomUUID().toString());
            this.getTestRequestUrls().stream().forEach(testRequestUrlEntity -> testRequestUrlEntity.setTestRequestId(this.getId()));
        }
    }
}
