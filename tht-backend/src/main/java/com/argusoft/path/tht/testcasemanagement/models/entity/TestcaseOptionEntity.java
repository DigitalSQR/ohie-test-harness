package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * This model is mapped to testcase_option table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "testcase_option")
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TestcaseOptionEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "testcase_id")
//    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private TestcaseEntity testcase;

    public TestcaseOptionEntity(TestcaseOptionEntity testcaseOptionEntity) {
        super(testcaseOptionEntity);
        this.setRank(testcaseOptionEntity.getRank());
        this.setSuccess(testcaseOptionEntity.getSuccess());
        if (testcaseOptionEntity.getTestcase() != null) {
            this.setTestcase(new TestcaseEntity(testcaseOptionEntity.getTestcase().getId()));
        }

    }

    public TestcaseOptionEntity() {
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public TestcaseEntity getTestcase() {
        return testcase;
    }

    public void setTestcase(TestcaseEntity testcase) {
        this.testcase = testcase;
    }
}
