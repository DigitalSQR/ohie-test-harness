package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
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

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }


    @Override
    protected List<Predicate> buildPredicates(Root<TestcaseOptionEntity> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if(StringUtils.hasLength(getName())){
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if(!CollectionUtils.isEmpty(getStates())){
            predicates.add(criteriaBuilder.in(root.get("state")).value(getStates()));
        }

        if (getTestcaseId() != null) {
            Join<TestcaseOptionEntity, TestcaseEntity> componentJoin = root.join("testcase");
            predicates.add(criteriaBuilder.equal(componentJoin.get("id"), getTestcaseId()));
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

    public String getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(String testcaseId) {
        this.testcaseId = testcaseId;
    }
}
