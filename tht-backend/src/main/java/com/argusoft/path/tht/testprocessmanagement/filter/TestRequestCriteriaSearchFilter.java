package com.argusoft.path.tht.testprocessmanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
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

public class TestRequestCriteriaSearchFilter extends AbstractCriteriaSearchFilter<TestRequestEntity> {

    @ApiParam(
            value = "name of the testRequest"
    )
    private String name;

    @ApiParam(
            value = "state of the testRequest"
    )
    private List<String> states;

    @ApiParam(
            value = "assesseeId of the testRequest"
    )
    private String assesseeId;


    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected List<Predicate> buildPredicates(Root<TestRequestEntity> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (!CollectionUtils.isEmpty(getStates())) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(getStates()));
        }

        if (getAssesseeId() != null) {
            Join<TestcaseEntity, UserEntity> joinTable = root.join("assessee");
            predicates.add(criteriaBuilder.equal(joinTable.get("id"), getAssesseeId()));
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

    public String getAssesseeId() {
        return assesseeId;
    }

    public void setAssesseeId(String assesseeId) {
        this.assesseeId = assesseeId;
    }
}
