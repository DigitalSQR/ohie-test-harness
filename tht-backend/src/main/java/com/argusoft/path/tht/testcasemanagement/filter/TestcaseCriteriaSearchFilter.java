package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
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

public class TestcaseCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestcaseEntity> {


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

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestcaseEntity> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (!CollectionUtils.isEmpty(getStates())) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(getStates()));
        }

        if (getManual() != null) {
            predicates.add(criteriaBuilder.equal(root.get("isManual"), getManual()));
        }

        if (getSpecificationId() != null) {
            Join<TestcaseEntity, SpecificationEntity> joinTable = root.join("specification");
            predicates.add(criteriaBuilder.equal(joinTable.get("id"), getSpecificationId()));
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
}
