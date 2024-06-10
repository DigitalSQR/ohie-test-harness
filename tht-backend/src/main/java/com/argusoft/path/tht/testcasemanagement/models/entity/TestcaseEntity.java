package com.argusoft.path.tht.testcasemanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This model is mapped to testcase table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "testcase")
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TestcaseEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "is_manual")
    private Boolean isManual;

    @Column(name = "bean_name")
    private String beanName;

    @Column(name = "failure_message", length = 2000)
    private String failureMessage;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "test_case_run_environment")
    private String testcaseRunEnvironment;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "specification_id")
//    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private SpecificationEntity specification;

    @Column(name = "test_suite_id", length = 1300)
    private String testSuiteId;

    @Column(name = "sut_actor_api_key")
    private String sutActorApiKey;


    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<TestcaseVariableEntity> testcaseVariables;


    public TestcaseEntity() {
    }

    public TestcaseEntity(TestcaseEntity testcaseEntity) {
        super(testcaseEntity);
        this.setRank(testcaseEntity.getRank());
        this.setManual(testcaseEntity.getManual());
        this.setBeanName(testcaseEntity.getBeanName());
        this.setTestcaseRunEnvironment(testcaseEntity.getTestcaseRunEnvironment());
        this.setFailureMessage(testcaseEntity.getFailureMessage());
        this.setTestSuiteId(testcaseEntity.getTestSuiteId());
        this.setSutActorApiKey(testcaseEntity.getSutActorApiKey());
        this.setQuestionType(testcaseEntity.getQuestionType());
        this.setTestcaseVariables(testcaseEntity.getTestcaseVariables());
        if (testcaseEntity.getSpecification() != null) {
            this.setSpecification(new SpecificationEntity(testcaseEntity.getSpecification().getId()));
        }
        if (testcaseEntity.getTestcaseVariables() != null) {
            this.setTestcaseVariables(testcaseEntity.getTestcaseVariables().stream().map(TestcaseVariableEntity::new).collect(Collectors.toList()));
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

    public String getTestcaseRunEnvironment() {
        return testcaseRunEnvironment;
    }

    public void setTestcaseRunEnvironment(String testcaseRunEnvironment) {
        this.testcaseRunEnvironment = testcaseRunEnvironment;
    }

    public String getSutActorApiKey() {
        return sutActorApiKey;
    }

    public void setSutActorApiKey(String sutActorApiKey) {
        this.sutActorApiKey = sutActorApiKey;
    }

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    public List<TestcaseVariableEntity> getTestcaseVariables() {
        return testcaseVariables;
    }

    public void setTestcaseVariables(List<TestcaseVariableEntity> testcaseVariables) {
        this.testcaseVariables = testcaseVariables;
    }

    @PrePersist
    private void changesBeforeSaveTestCase() {
        this.getTestcaseVariables().stream().forEach(testcaseVariableEntity -> testcaseVariableEntity.setTestcaseId(this.getId()));
    }
}
