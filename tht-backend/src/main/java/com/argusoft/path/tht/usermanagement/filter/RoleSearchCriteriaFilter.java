package com.argusoft.path.tht.usermanagement.filter;

import com.argusoft.path.tht.systemconfiguration.examplefilter.AbstractCriteriaSearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import io.swagger.annotations.ApiParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class RoleSearchCriteriaFilter extends AbstractCriteriaSearchFilter<RoleEntity> {

    @ApiParam(
            value = "name of the role"
    )
    private String name;

    @Override
    public void validateSearchFilter() throws InvalidParameterException {

    }


    @Override
    protected List<Predicate> buildPredicates(Root<RoleEntity> root, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
