package com.argusoft.path.tht.usermanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
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

public class UserSearchCriteriaFilter extends AbstractCriteriaSearchFilter<UserEntity> {

    private String id;

    @ApiParam(
            value = "name of the user"
    )
    private String name;

    @ApiParam(
            value = "name of the user"
    )
    private List<String> states;

    @ApiParam(
            value = "email of the user"
    )
    private String email;

    @ApiParam(
            value = "role of the user"
    )
    private String role;

    private Root<UserEntity> userEntityRoot;

    private Join<UserEntity, RoleEntity> userEntityRoleEntityJoin;

    public UserSearchCriteriaFilter(String id) {
        this.id = id;
    }

    public UserSearchCriteriaFilter() {
    }

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }

    @Override
    protected List<Predicate> buildPredicates(Root<UserEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
        setUserEntityRoot(root);
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getPrimaryId())) {
            predicates.add(criteriaBuilder.equal(getUserEntityRoot().get("id"), getPrimaryId()));
        }

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(getUserEntityRoot().get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (!CollectionUtils.isEmpty(getState())) {
            predicates.add(criteriaBuilder.in(getUserEntityRoot().get("state")).value(getState()));
        }

        if (StringUtils.hasLength(getEmail())) {
            predicates.add(criteriaBuilder.equal(getUserEntityRoot().get("email"), getEmail()));
        }

        if (StringUtils.hasLength(getRole())) {
            predicates.add(criteriaBuilder.equal(this.getUserEntityRoleEntityJoin().get("id"), getRole()));
        }

        return predicates;
    }

    @Override
    protected List<Predicate> buildAuthorizationPredicates(Root<UserEntity> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrimaryId() {
        return id;
    }

    public void setPrimaryId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private Root<UserEntity> getUserEntityRoot() {
        return userEntityRoot;
    }

    private void setUserEntityRoot(Root<UserEntity> userEntityRoot) {
        this.userEntityRoot = userEntityRoot;
        this.userEntityRoleEntityJoin = null;
    }

    private Join<UserEntity, RoleEntity> getUserEntityRoleEntityJoin() {
        if (userEntityRoleEntityJoin == null) {
            userEntityRoleEntityJoin = getUserEntityRoot().join("roles");
        }
        return userEntityRoleEntityJoin;
    }


}
