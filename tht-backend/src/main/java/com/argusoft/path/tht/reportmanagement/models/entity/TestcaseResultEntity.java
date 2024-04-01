package com.argusoft.path.tht.reportmanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Set;

/**
 * This model is mapped to user table in database.
 *
 * @author Dhruv
 */
@Entity
@Audited
@Table(name = "testcase_result")
public class TestcaseResultEntity extends IdStateNameMetaEntity {

    @Column(name = "rank")
    private Integer rank;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "tester_id")
    private UserEntity tester;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_test_case_result_id", updatable = false)
    private TestcaseResultEntity parentTestcaseResult;

    @OneToMany(mappedBy = "testcaseResultEntity", fetch = FetchType.EAGER)
    private Set<TestcaseResultAttributesEntity> testcaseResultAttributesEntities;

    @Column(name = "ref_obj_uri", updatable = false)
    private String refObjUri;

    @Column(name = "ref_id", updatable = false)
    private String refId;

    @Column(name = "message", length = 2000)
    private String message;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "test_request_id", updatable = false)
    private TestRequestEntity testRequest;

    @Column(name = "has_system_error")
    private Boolean hasSystemError;

    @Column(name = "is_manual", updatable = false)
    private Boolean isManual;

    @Column(name = "is_required", updatable = false)
    private Boolean isRequired;

    @Column(name = "is_automated", updatable = false)
    private Boolean isAutomated;

    @Column(name = "is_recommended", updatable = false)
    private Boolean isRecommended;

    @Column(name = "is_workflow", updatable = false)
    private Boolean isWorkflow;

    @Column(name = "is_functional", updatable = false)
    private Boolean isFunctional;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "grade")
    private String grade;

    @Column(name = "compliant")
    private int compliant;
    @Column(name = "non_compliant")
    private int nonCompliant;

    public TestcaseResultEntity() {
    }

    public TestcaseResultEntity(TestcaseResultEntity testcaseResultEntity) {
        super(testcaseResultEntity);
        this.setRank(testcaseResultEntity.getRank());
        this.setRefObjUri(testcaseResultEntity.getRefObjUri());
        this.setRefId(testcaseResultEntity.getRefId());
        this.setMessage(testcaseResultEntity.getMessage());
        this.setHasSystemError(testcaseResultEntity.getHasSystemError());
        this.setDuration(testcaseResultEntity.getDuration());
        this.setGrade(testcaseResultEntity.getGrade());
        this.setSuccess(testcaseResultEntity.getSuccess());
        this.setManual(testcaseResultEntity.getManual());
        this.setRequired(testcaseResultEntity.getRequired());
        this.setAutomated(testcaseResultEntity.getAutomated());
        this.setRecommended(testcaseResultEntity.getRecommended());
        this.setWorkflow(testcaseResultEntity.getWorkflow());
        this.setFunctional(testcaseResultEntity.getFunctional());
        this.setGrade(testcaseResultEntity.getGrade());
        this.setCompliant(testcaseResultEntity.getCompliant());
        this.setNonCompliant(testcaseResultEntity.getNonCompliant());

        if (testcaseResultEntity.getTester() != null) {
            this.setTester(new UserEntity(testcaseResultEntity.getTester().getId()));
        }
        if (testcaseResultEntity.getParentTestcaseResult() != null) {
            this.setParentTestcaseResult(new TestcaseResultEntity(testcaseResultEntity.getParentTestcaseResult().getId()));
        }
        if (testcaseResultEntity.getTestRequest() != null) {
            this.setTestRequest(new TestRequestEntity(testcaseResultEntity.getTestRequest().getId()));
        }
    }

    public TestcaseResultEntity(String id) {
        this.setId(id);
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public UserEntity getTester() {
        return tester;
    }

    public void setTester(UserEntity tester) {
        this.tester = tester;
    }

    public String getRefObjUri() {
        return refObjUri;
    }

    public void setRefObjUri(String refObjUri) {
        this.refObjUri = refObjUri;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getHasSystemError() {
        return hasSystemError;
    }

    public void setHasSystemError(Boolean hasSystemError) {
        this.hasSystemError = hasSystemError;
    }

    public TestRequestEntity getTestRequest() {
        return testRequest;
    }

    public void setTestRequest(TestRequestEntity testRequest) {
        this.testRequest = testRequest;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public TestcaseResultEntity getParentTestcaseResult() {
        return parentTestcaseResult;
    }

    public void setParentTestcaseResult(TestcaseResultEntity parentTestcaseResult) {
        this.parentTestcaseResult = parentTestcaseResult;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getAutomated() {
        return isAutomated;
    }

    public void setAutomated(Boolean automated) {
        isAutomated = automated;
    }

    public Boolean getRecommended() {
        return isRecommended;
    }

    public void setRecommended(Boolean recommended) {
        isRecommended = recommended;
    }

    public Boolean getWorkflow() {
        return isWorkflow;
    }

    public void setWorkflow(Boolean workflow) {
        isWorkflow = workflow;
    }

    public Boolean getFunctional() {
        return isFunctional;
    }

    public void setFunctional(Boolean functional) {
        isFunctional = functional;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Set<TestcaseResultAttributesEntity> getTestcaseResultAttributesEntities() {
        return testcaseResultAttributesEntities;
    }

    public void setTestcaseResultAttributesEntities(Set<TestcaseResultAttributesEntity> testcaseResultAttributesEntities) {
        this.testcaseResultAttributesEntities = testcaseResultAttributesEntities;
    }

    public int getCompliant() {
        return compliant;
    }

    public void setCompliant(int compliant) {
        this.compliant = compliant;
    }

    public int getNonCompliant() {
        return nonCompliant;
    }

    public void setNonCompliant(int nonCompliant) {
        this.nonCompliant = nonCompliant;
    }

    @Override
    public String toString() {
        return "TestcaseResultEntity{"
                + "rank=" + rank
                + ", testcaseResultAttributesEntities=" + testcaseResultAttributesEntities
                + ", refObjUri='" + refObjUri + '\''
                + ", refId='" + refId + '\''
                + ", message='" + message + '\''
                + ", hasSystemError=" + hasSystemError
                + ", isManual=" + isManual
                + ", isRequired=" + isRequired
                + ", isAutomated=" + isAutomated
                + ", isRecommended=" + isRecommended
                + ", isWorkflow=" + isWorkflow
                + ", isFunctional=" + isFunctional
                + ", isSuccess=" + isSuccess
                + ", duration=" + duration
                + ", grade='" + grade
                + ", compliant='" + compliant
                + ", nonCompliant='" + nonCompliant + '\''
                + '}';
    }

}
