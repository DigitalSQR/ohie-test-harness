package com.argusoft.path.tht.systemconfiguration.examplefilter;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractCriteriaSearchFilter<T> implements CriteriaSearchFilter<T> {
    @Override
    public Specification<T> buildSpecification() throws InvalidParameterException {
        validateSearchFilter();
        return (root, query, criteriaBuilder) -> {
            BiFunction<Root<T>, CriteriaBuilder, Predicate> predicateFunction = preparePredicate();
            return predicateFunction.apply(root, criteriaBuilder);
        };
    }

    @Override
    public BiFunction<Root<T>, CriteriaBuilder, Predicate> buildPredicate() throws InvalidParameterException {
        validateSearchFilter();
        return this.preparePredicate();
    }


    protected BiFunction<Root<T>, CriteriaBuilder, Predicate> preparePredicate() {
        return (root, criteriaBuilder) -> {
            List<Predicate> predicates = buildPredicates(root, criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected abstract List<Predicate> buildPredicates(Root<T> root, CriteriaBuilder criteriaBuilder);

    protected abstract void validateSearchFilter() throws InvalidParameterException;


}
