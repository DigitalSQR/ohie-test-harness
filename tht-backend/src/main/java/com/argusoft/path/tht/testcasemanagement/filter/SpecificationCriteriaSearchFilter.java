package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
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
public class SpecificationCriteriaSearchFilter extends AbstractCriteriaSearchFilter<SpecificationEntity> {

    private String id;

    @ApiParam(
            value = "name of the specification"
    )
    private String name;

    @ApiParam(
            value = "state of the specification"
    )
    private List<String> state;

    @ApiParam(
            value = "componentId of the specification"
    )
    private String componentId;

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    public SpecificationCriteriaSearchFilter() {
    }

    public SpecificationCriteriaSearchFilter(String id) {
        this.id = id;
    }

    @Override
    protected List<Predicate> buildPredicates(Root<SpecificationEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        List<Predicate> predicates = new ArrayList<>();

        if(StringUtils.hasLength(getPrimaryId())){
            predicates.add(criteriaBuilder.equal(root.get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(state));
        }

        if (getComponentId() != null) {
            Join<SpecificationEntity, ComponentEntity> componentJoin = root.join("component");
            predicates.add(criteriaBuilder.equal(componentJoin.get("id"), getComponentId()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<SpecificationEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getState() {
        return state;
    }

    public void setState(List<String> state) {
        this.state = state;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getPrimaryId() {
        return id;
    }

    public void setPrimaryId(String id) {
        this.id = id;
    }
}
