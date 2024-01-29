package com.argusoft.path.tht.systemconfiguration.examplefilter;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractCriteriaSearchFilter<T> implements CriteriaSearchFilter<T> {
    @Override
    public final Specification<T> buildSpecification(ContextInfo contextInfo) throws InvalidParameterException {
        validateSearchFilter();
        return (root, query, criteriaBuilder) -> {
            BiFunction<Root<T>, CriteriaBuilder, Predicate> predicateFunction = preparePredicate(contextInfo);
            modifyCriteriaQuery(root, query);
            return predicateFunction.apply(root, criteriaBuilder);
        };
    }

    protected void modifyCriteriaQuery(Root<T> root, CriteriaQuery<?> query) {
    }


    protected final BiFunction<Root<T>, CriteriaBuilder, Predicate> preparePredicate(ContextInfo contextInfo) {
        return (root, criteriaBuilder) -> {
            List<Predicate> predicates = buildPredicates(root, criteriaBuilder, contextInfo);
            List<Predicate> authorizationPredicates = buildAuthorizationPredicates(root, criteriaBuilder, contextInfo);

            if (!CollectionUtils.isEmpty(authorizationPredicates)) {
                predicates.addAll(authorizationPredicates);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected abstract List<Predicate> buildPredicates(Root<T> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo);

    protected abstract List<Predicate> buildAuthorizationPredicates(Root<T> root, CriteriaBuilder criteriaBuilder, ContextInfo contextInfo);

    protected abstract void validateSearchFilter() throws InvalidParameterException;


}
