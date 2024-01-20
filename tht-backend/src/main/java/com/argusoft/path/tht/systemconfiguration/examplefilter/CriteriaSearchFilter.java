package com.argusoft.path.tht.systemconfiguration.examplefilter;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.BiFunction;

public interface CriteriaSearchFilter<T> {

    Specification<T> buildSpecification() throws InvalidParameterException;

    BiFunction<Root<T>, CriteriaBuilder, Predicate> buildPredicate() throws InvalidParameterException;

}
