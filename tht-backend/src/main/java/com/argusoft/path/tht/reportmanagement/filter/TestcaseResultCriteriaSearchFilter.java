package com.argusoft.path.tht.reportmanagement.filter;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
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

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    public TestcaseResultCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestcaseResultCriteriaSearchFilter() {
    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestcaseResultEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        List<Predicate> predicates = new ArrayList<>();

        if(StringUtils.hasLength(getPrimaryId())){
            predicates.add(criteriaBuilder.equal(root.get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (!CollectionUtils.isEmpty(getStates())) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(getStates()));
        }

        if (getTesterId() != null) {
            Join<TestcaseResultEntity, UserEntity> ownerJoin = root.join("tester");
            predicates.add(criteriaBuilder.equal(ownerJoin.get("id"), getTesterId()));
        }

        if (StringUtils.hasLength(getRefObjUri())) {
            predicates.add(criteriaBuilder.equal(root.get("refObjUri"), getRefObjUri()));
        }

        if (StringUtils.hasLength(getRefId())) {
            predicates.add(criteriaBuilder.equal(root.get("refId"), getRefId()));
        }

        if (StringUtils.hasLength(getParentTestcaseResultId())) {
            Join<TestcaseResultEntity, TestcaseResultEntity> joinTable = root.join("parentTestcaseResult");
            predicates.add(criteriaBuilder.equal(joinTable.get("id"), getParentTestcaseResultId()));
        }

        if (getManual() != null) {
            predicates.add(criteriaBuilder.equal(root.get("isManual"), getManual()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<TestcaseResultEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        List<Predicate> predicates = new ArrayList<>();

        Join<TestcaseResultEntity, TestRequestEntity> joinTableWithTestRequest = root.join("testRequest");

        if (contextInfo.isAssessee()) {
            Join<TestRequestEntity, UserEntity> joinTableWithUserEntity = joinTableWithTestRequest.join("assessee");
            predicates.add(criteriaBuilder.equal(joinTableWithUserEntity.get("id"), contextInfo.getUsername()));
        }

        if (getTestRequestId() != null) {
            predicates.add(criteriaBuilder.equal(joinTableWithTestRequest.get("id"), getTestRequestId()));
        }


        return predicates;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
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

    public void setPrimaryId(String id) {
        this.id = id;
    }
}
