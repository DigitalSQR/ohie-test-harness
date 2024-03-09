package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * This model is mapped to testcase table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "testcase")
public class TestcaseEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "is_manual")
    private Boolean isManual;

    @Column(name = "bean_name")
    private String beanName;

    @Column(name = "failure_message", length = 1000)
    private String failureMessage;

    @Column(name = "question_type")
    private String questionType;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "specification_id")
    private SpecificationEntity specification;

    public TestcaseEntity() {
    }

    public TestcaseEntity(TestcaseEntity testcaseEntity) {
        super(testcaseEntity);
        this.setRank(testcaseEntity.getRank());
        this.setManual(testcaseEntity.getManual());
        this.setBeanName(testcaseEntity.getBeanName());
        if(testcaseEntity.getSpecification()!=null){
            this.setSpecification(new SpecificationEntity(testcaseEntity.getSpecification().getId()));
        }
    }

    public TestcaseEntity(String id) {
        this.setId(id);
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public SpecificationEntity getSpecification() {
        return specification;
    }

    public void setSpecification(SpecificationEntity specification) {
        this.specification = specification;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }
}
