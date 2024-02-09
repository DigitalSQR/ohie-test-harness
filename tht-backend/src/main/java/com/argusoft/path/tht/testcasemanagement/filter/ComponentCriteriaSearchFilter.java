package com.argusoft.path.tht.testcasemanagement.filter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Component
public class ComponentCriteriaSearchFilter extends AbstractCriteriaSearchFilter<ComponentEntity> {

    private String id;

    @ApiParam(
            value = "name of the component"
    )
    private String name;

    @ApiParam(
            value = "state of the component"
    )
    private List<String> state;

    private Root<ComponentEntity> componentEntityRoot;

    public ComponentCriteriaSearchFilter(String id) {
        this.id = id;
    }

    public ComponentCriteriaSearchFilter() {
    }

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected List<Predicate> buildPredicates(Root<ComponentEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setComponentEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getComponentEntityRoot().get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getComponentEntityRoot().get("name")),getNameBasedOnSearchType(getName()) ));
        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(getComponentEntityRoot().get("state")).value(getState()));
        }


        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<ComponentEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
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


    public String getPrimaryId() {
        return id;
    }

    public void setPrimaryId(String id) {
        this.id = id;
    }

    private Root<ComponentEntity> getComponentEntityRoot() {
        return componentEntityRoot;
    }

    private void setComponentEntityRoot(Root<ComponentEntity> componentEntityRoot) {
        this.componentEntityRoot = componentEntityRoot;
    }
}
