package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
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


    @ApiParam(
            value = "min rank of the test case"
    )
    private Integer minRank;

    @ApiParam(
            value = "max rank of the test case"
    )
    private Integer maxRank;

    @ApiParam(
            value = "test suite id from the zip file"
    )
    private String testSuiteId;

    @ApiParam(
            value = "rank of the test case"
    )
    private Integer rank;

    private Root<TestcaseEntity> testcaseEntityRoot;

    private Join<TestcaseEntity, SpecificationEntity> testcaseEntitySpecificationEntityJoin;

    public TestcaseCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestcaseCriteriaSearchFilter() {
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

        if (getMinRank() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(getTestcaseEntityRoot().get("rank"), getMinRank()));
        }

        if (getMaxRank() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(getTestcaseEntityRoot().get("rank"), getMaxRank()));
        }

        if(getTestSuiteId() != null){
            predicates.add(criteriaBuilder.equal(getTestcaseEntityRoot().get("testSuiteId"), getTestSuiteId()));
        }


        return predicates;
    }

    @Override
    protected List<Predicate> buildLikePredicates(Root<TestcaseEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setTestcaseEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestcaseEntityRoot().get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (getRank() != null) {
            predicates.add(criteriaBuilder.like(getTestcaseEntityRoot().get("rank").as(String.class), "%"+ rank + "%"));
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


    public Integer getMinRank() {
        return minRank;
    }

    public void setMinRank(Integer minRank) {
        this.minRank = minRank;
    }

    public Integer getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(Integer maxRank) {
        this.maxRank = maxRank;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
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
