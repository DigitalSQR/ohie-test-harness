package com.argusoft.path.tht.reportmanagement.filter;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Search filter for TestcaseResult
 *
 * @author Hardik
 */

public class TestcaseResultCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestcaseResultEntity> {

    private String id;

    @ApiParam(
            value = "name of the TestcaseResult"
    )
    private String name;

    @ApiParam(
            value = "state of the TestcaseResult"
    )
    private List<String> states;

    @ApiParam(
            value = "testerId of the TestcaseResult"
    )
    private String testerId;

    @ApiParam(
            value = "refObjUri of the TestcaseResult"
    )
    private String refObjUri;

    @ApiParam(
            value = "refId of the TestcaseResult"
    )
    private String refId;

    @ApiParam(
            value = "testRequestId of the TestcaseResult"
    )
    private String testRequestId;

    @ApiParam(
            value = "parentTestcaseResultId of the TestcaseResult"
    )
    private String parentTestcaseResultId;

    @ApiParam(
            value = "isManual of the TestcaseResult"
    )
    private Boolean isManual;

    @ApiParam(
            value = "isAutomated of the TestcaseResult"
    )
    private Boolean isAutomated;

    @ApiParam(
            value = "isFunctional of the TestcaseResult"
    )
    private Boolean isFunctional;

    @ApiParam(
            value = "isWorkflow of the TestcaseResult"
    )
    private Boolean isWorkflow;

    @ApiParam(
            value = "isRecommended of the TestcaseResult"
    )
    private Boolean isRecommended;

    @ApiParam(
            value = "isRequired of the TestcaseResult"
    )
    private Boolean isRequired;

    private Root<TestcaseResultEntity> testcaseResultEntityRoot;

    private Join<TestcaseResultEntity, UserEntity> testcaseResultEntityUserEntityJoin;

    private Join<TestcaseResultEntity, TestcaseResultEntity> testcaseResultEntityTestcaseResultEntityJoin;

    private Join<TestcaseResultEntity, TestRequestEntity> testcaseResultEntityTestRequestEntityJoin;

    private Join<TestRequestEntity, UserEntity> testRequestEntityUserEntityJoinWithTestcaseResultJoin;

    public TestcaseResultCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestcaseResultCriteriaSearchFilter() {
    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestcaseResultEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        this.setTestcaseResultEntityRoot(root);

        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(this.getTestcaseResultEntityRoot().get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(this.getTestcaseResultEntityRoot().get("name")), getNameBasedOnSearchType(getName())));

        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(this.getTestcaseResultEntityRoot().get("state")).value(getState()));
        }

        if (getTesterId() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseResultEntityUserEntityJoinForTester().get("id"), getTesterId()));
        }

        if (StringUtils.hasLength(getRefObjUri())) {
            predicates.add(criteriaBuilder.equal(this.getTestcaseResultEntityRoot().get("refObjUri"), getRefObjUri()));
        }

        if (StringUtils.hasLength(getRefId())) {
            predicates.add(criteriaBuilder.equal(this.getTestcaseResultEntityRoot().get("refId"), getRefId()));
        }

        if (StringUtils.hasLength(getParentTestcaseResultId())) {
            predicates.add(criteriaBuilder.equal(this.getTestcaseResultEntityTestcaseResultEntityJoin().get("id"), getParentTestcaseResultId()));
        }

        if (getManual() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseResultEntityRoot().get("isManual"), getManual()));
        }

        if (getAutomated() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseResultEntityRoot().get("isAutomated"), getAutomated()));
        }

        if (getFunctional() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseResultEntityRoot().get("isFunctional"), getFunctional()));
        }

        if (getWorkflow() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseResultEntityRoot().get("isWorkflow"), getWorkflow()));
        }

        if (getRecommended() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseResultEntityRoot().get("isRecommended"), getRecommended()));
        }

        if (getRequired() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseResultEntityRoot().get("isRequired"), getRequired()));
        }

        if (getTestRequestId() != null) {
            Join<TestcaseResultEntity, TestRequestEntity> joinTableWithTestRequest = this.getTestcaseResultEntityTestRequestEntityJoin();
            predicates.add(criteriaBuilder.equal(joinTableWithTestRequest.get("id"), getTestRequestId()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<TestcaseResultEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {

        List<Predicate> predicates = new ArrayList<>();

        if (contextInfo.isAssessee()) {
            Join<TestRequestEntity, UserEntity> joinTableWithUserEntity = this.getTestRequestEntityUserEntityJoinWithTestcaseResultJoin();
            predicates.add(criteriaBuilder.equal(joinTableWithUserEntity.get("id"), contextInfo.getUsername()));
        }

        return predicates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getState() {
        return states;
    }

    public void setState(List<String> states) {
        this.states = states;
    }

    public String getTesterId() {
        return testerId;
    }

    public void setTesterId(String testerId) {
        this.testerId = testerId;
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

    public String getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(String testRequestId) {
        this.testRequestId = testRequestId;
    }

    public String getParentTestcaseResultId() {
        return parentTestcaseResultId;
    }

    public void setParentTestcaseResultId(String parentTestcaseResultId) {
        this.parentTestcaseResultId = parentTestcaseResultId;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public String getPrimaryId() {
        return id;
    }

    public Boolean getAutomated() {
        return isAutomated;
    }

    public void setAutomated(Boolean automated) {
        isAutomated = automated;
    }

    public Boolean getFunctional() {
        return isFunctional;
    }

    public void setFunctional(Boolean functional) {
        isFunctional = functional;
    }

    public Boolean getWorkflow() {
        return isWorkflow;
    }

    public void setWorkflow(Boolean workflow) {
        isWorkflow = workflow;
    }

    public Boolean getRecommended() {
        return isRecommended;
    }

    public void setRecommended(Boolean recommended) {
        isRecommended = recommended;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    private Root<TestcaseResultEntity> getTestcaseResultEntityRoot() {
        return testcaseResultEntityRoot;
    }

    private void setTestcaseResultEntityRoot(Root<TestcaseResultEntity> testcaseResultEntityRoot) {
        this.testcaseResultEntityRoot = testcaseResultEntityRoot;
        testcaseResultEntityUserEntityJoin = null;
        testcaseResultEntityTestcaseResultEntityJoin = null;
        testcaseResultEntityTestRequestEntityJoin = null;
        testRequestEntityUserEntityJoinWithTestcaseResultJoin = null;

    }

    public Join<TestcaseResultEntity, UserEntity> getTestcaseResultEntityUserEntityJoinForTester() {
        if (this.testcaseResultEntityUserEntityJoin == null) {
            this.testcaseResultEntityUserEntityJoin = getTestcaseResultEntityRoot().join("tester");
        }
        return testcaseResultEntityUserEntityJoin;
    }

    public Join<TestcaseResultEntity, TestcaseResultEntity> getTestcaseResultEntityTestcaseResultEntityJoin() {
        if (testcaseResultEntityTestcaseResultEntityJoin == null) {
            testcaseResultEntityTestcaseResultEntityJoin = getTestcaseResultEntityRoot().join("parentTestcaseResult");
        }
        return testcaseResultEntityTestcaseResultEntityJoin;
    }

    public Join<TestcaseResultEntity, TestRequestEntity> getTestcaseResultEntityTestRequestEntityJoin() {
        if (testcaseResultEntityTestRequestEntityJoin == null) {
            testcaseResultEntityTestRequestEntityJoin = getTestcaseResultEntityRoot().join("testRequest");
        }
        return testcaseResultEntityTestRequestEntityJoin;
    }

    public Join<TestRequestEntity, UserEntity> getTestRequestEntityUserEntityJoinWithTestcaseResultJoin() {
        if (testRequestEntityUserEntityJoinWithTestcaseResultJoin == null) {
            testRequestEntityUserEntityJoinWithTestcaseResultJoin = getTestcaseResultEntityTestRequestEntityJoin().join("assessee");
        }
        return testRequestEntityUserEntityJoinWithTestcaseResultJoin;
    }
}
