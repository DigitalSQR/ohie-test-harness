package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestcaseOptionCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestcaseOptionEntity> {

    private String id;

    @ApiParam(
            value = "name of the testcaseOption"
    )
    private String name;
    @ApiParam(
            value = "state of the testcaseOption"
    )
    private List<String> states;

    @ApiParam(
            value = "testcaseId of the testcaseOption"
    )
    private String testcaseId;

    private Root<TestcaseOptionEntity> testcaseOptionEntityRoot;

    private Join<TestcaseOptionEntity, TestcaseEntity> testcaseOptionEntityTestcaseEntityJoin;

    public TestcaseOptionCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public TestcaseOptionCriteriaSearchFilter() {
    }

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestcaseOptionEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setTestcaseOptionEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getTestcaseOptionEntityRoot().get("name")),getNameBasedOnSearchType(getName()) ));

        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(getTestcaseOptionEntityRoot().get("state")).value(getState()));
        }

        if (getTestcaseId() != null) {
            predicates.add(criteriaBuilder.equal(getTestcaseOptionEntityTestcaseEntityJoin().get("id"), getTestcaseId()));
        }

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getTestcaseOptionEntityRoot().get("id"), getPrimaryId()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<TestcaseOptionEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
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

    public String getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(String testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getPrimaryId() {
        return id;
    }

    private Root<TestcaseOptionEntity> getTestcaseOptionEntityRoot() {
        return testcaseOptionEntityRoot;
    }

    private void setTestcaseOptionEntityRoot(Root<TestcaseOptionEntity> testcaseOptionEntityRoot) {
        this.testcaseOptionEntityRoot = testcaseOptionEntityRoot;
        testcaseOptionEntityTestcaseEntityJoin = null;
    }

    public Join<TestcaseOptionEntity, TestcaseEntity> getTestcaseOptionEntityTestcaseEntityJoin() {
        if (testcaseOptionEntityTestcaseEntityJoin == null) {
            testcaseOptionEntityTestcaseEntityJoin = getTestcaseOptionEntityRoot().join("testcase");
        }
        return testcaseOptionEntityTestcaseEntityJoin;
    }
}
