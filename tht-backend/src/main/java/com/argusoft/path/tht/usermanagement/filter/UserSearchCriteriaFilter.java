package com.argusoft.path.tht.usermanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import io.swagger.annotations.ApiParam;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class UserSearchCriteriaFilter extends AbstractCriteriaSearchFilter<UserEntity> {


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


    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }


    @Override
    protected List<Predicate> buildPredicates(Root<UserEntity> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasLength(getName())) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (!CollectionUtils.isEmpty(getStates())) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(getStates()));
        }

        if (StringUtils.hasLength(getEmail())) {
            predicates.add(criteriaBuilder.equal(root.get("email"), getEmail()));
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
