package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
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
 * Criteria Search filter for testcase
 *
 * @author Hardik
 */

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

    @ApiParam(
            value = "questionType of the testcase",
            example = "SINGLE_SELECT, MULTI_SELECT"
    )
    private String questionType;

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
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestcaseEntityRoot().get("name")), getNameBasedOnSearchType(getName())));
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

        if (StringUtils.hasLength(getQuestionType())) {
            predicates.add(criteriaBuilder.equal(getTestcaseEntityRoot().get("questionType"), getQuestionType()));
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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    private Root<TestcaseEntity> getTestcaseEntityRoot() {
        return testcaseEntityRoot;
    }

    private void setTestcaseEntityRoot(Root<TestcaseEntity> testcaseEntityRoot) {
        this.testcaseEntityRoot = testcaseEntityRoot;
        this.testcaseEntitySpecificationEntityJoin = null;
    }

    public Join<TestcaseEntity, SpecificationEntity> getTestcaseEntitySpecificationEntityJoin() {
        if (testcaseEntitySpecificationEntityJoin == null) {
            testcaseEntitySpecificationEntityJoin = getTestcaseEntityRoot().join("specification");
        }
        return testcaseEntitySpecificationEntityJoin;
    }
}
