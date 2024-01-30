package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import io.swagger.annotations.ApiParam;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TestcaseCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestcaseEntity> {


    private String id;

    @ApiParam(
            value = "name of the testcase"
    )
    private String name;

    @ApiParam(
            value = "state of the testcase"
    )
    private List<String> states;

    @ApiParam(
            value = "specificationId of the testcase"
    )
    private String specificationId;

    @ApiParam(
            value = "isManual of the testcase"
    )
    private Boolean isManual;

    private Root<TestcaseEntity> testcaseEntityRoot;

    private Join<TestcaseEntity, SpecificationEntity> testcaseEntitySpecificationEntityJoin;

    public TestcaseCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestcaseCriteriaSearchFilter() {
    }

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestcaseEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setTestcaseEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getTestcaseEntityRoot().get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestcaseEntityRoot().get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(getTestcaseEntityRoot().get("state")).value(getState()));
        }

        if (getManual() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseEntityRoot().get("isManual"), getManual()));
        }

        if (getSpecificationId() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseEntitySpecificationEntityJoin().get("id"), getSpecificationId()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<TestcaseEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        return null;
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

    public String getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(String specificationId) {
        this.specificationId = specificationId;
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

    private Root<TestcaseEntity> getTestcaseEntityRoot() {
        return testcaseEntityRoot;
    }

    private void setTestcaseEntityRoot(Root<TestcaseEntity> testcaseEntityRoot) {
        this.testcaseEntityRoot = testcaseEntityRoot;
    }

    public Join<TestcaseEntity, SpecificationEntity> getTestcaseEntitySpecificationEntityJoin() {
        if(testcaseEntitySpecificationEntityJoin == null){
            testcaseEntitySpecificationEntityJoin = getTestcaseEntityRoot().join("specification");
        }
        return testcaseEntitySpecificationEntityJoin;
    }
}
