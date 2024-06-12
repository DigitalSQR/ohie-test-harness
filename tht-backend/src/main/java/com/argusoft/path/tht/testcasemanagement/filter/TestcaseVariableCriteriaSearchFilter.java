package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Criteria Search filter for testcaseVariables
 *
 * @author Aastha
 */

public class TestcaseVariableCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestcaseVariableEntity> {

    private String id;

    @ApiParam(
            value = "state of the testcase variable"
    )
    private String state;

    @ApiParam(
            value = "key of the testcase variable"
    )
    private String testcaseVariableKey;


    private Root<TestcaseVariableEntity> testcaseVariablesEntityRoot;

    public TestcaseVariableCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestcaseVariableCriteriaSearchFilter() {
    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestcaseVariableEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setTestcaseVariablesEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getTestcaseVariablesEntityRoot().get("id"), getPrimaryId()));
        }

        if (getState() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseVariablesEntityRoot().get("state"), getState()));
        }

        if (getTestcaseVariableKey() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseVariablesEntityRoot().get("testcaseVariableKey"), getTestcaseVariableKey()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildLikePredicates(Root<TestcaseVariableEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) throws ParseException {
        return null;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTestcaseVariableKey() {
        return testcaseVariableKey;
    }

    public void setTestcaseVariableKey(String testcaseVariableKey) {
        this.testcaseVariableKey = testcaseVariableKey;
    }

    private Root<TestcaseVariableEntity> getTestcaseVariablesEntityRoot() {
        return testcaseVariablesEntityRoot;
    }

    private void setTestcaseVariablesEntityRoot(Root<TestcaseVariableEntity> testcaseVariablesEntityRoot) {
        this.testcaseVariablesEntityRoot = testcaseVariablesEntityRoot;
    }

    public String getPrimaryId() {
        return id;
    }

}
