package com.argusoft.path.tht.usermanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RoleSearchCriteriaFilter extends AbstractCriteriaSearchFilter<RoleEntity> {

    private String id;

    @ApiParam(
            value = "name of the role"
    )
    private String name;

    private Root<RoleEntity> roleEntityRoot;

    public RoleSearchCriteriaFilter(String id) {
        this.id = id;
    }

    public RoleSearchCriteriaFilter() {
    }

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected List<Predicate> buildPredicates(Root<RoleEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setRoleEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getRoleEntityRoot().get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getRoleEntityRoot().get("name")), getNameBasedOnSearchType(getName())));

        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<RoleEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryId() {
        return id;
    }

    private Root<RoleEntity> getRoleEntityRoot() {
        return roleEntityRoot;
    }

    private void setRoleEntityRoot(Root<RoleEntity> roleEntityRoot) {
        this.roleEntityRoot = roleEntityRoot;
    }
}
