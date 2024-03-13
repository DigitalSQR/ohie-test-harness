package com.argusoft.path.tht.systemconfiguration.examplefilter;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractCriteriaSearchFilter<T> implements CriteriaSearchFilter<T> {

    private SearchType nameSearchType = SearchType.CONTAINING;

    @Override
    public final Specification<T> buildSpecification(ContextInfo contextInfo) throws InvalidParameterException {
        validateSearchFilter();
        return (root, query, criteriaBuilder) -> {
            BiFunction<Root<T>, CriteriaBuilder, Predicate> predicateFunction = preparePredicate(contextInfo);
            modifyCriteriaQuery(criteriaBuilder, root, query);
            return predicateFunction.apply(root, criteriaBuilder);
        };
    }

    @Override
    public final Specification<T> buildSpecification(Pageable pageable, ContextInfo contextInfo) throws InvalidParameterException {
        validateSearchFilter();
        return (root, query, criteriaBuilder) -> {
            BiFunction<Root<T>, CriteriaBuilder, Predicate> predicateFunction = preparePredicate(contextInfo);
            modifyCriteriaQuery(criteriaBuilder, root, query, pageable);
            return predicateFunction.apply(root, criteriaBuilder);
        };
    }

    protected void modifyCriteriaQuery(CriteriaBuilder criteriaBuilder, Root<T> root, CriteriaQuery<?> query) {
    }

    protected void modifyCriteriaQuery(CriteriaBuilder criteriaBuilder, Root<T> root, CriteriaQuery<?> query, Pageable pageable) {
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

    public SearchType getNameSearchType() {
        return nameSearchType;
    }

    public void setNameSearchType(SearchType nameSearchType) {
        this.nameSearchType = nameSearchType;
    }

    public String getNameBasedOnSearchType(String name) {
        switch (nameSearchType) {
            case EXACTLY:
                return name.toLowerCase();
            case STARTING:
                return name.toLowerCase() + "%";
            case ENDING:
                return "%" + name.toLowerCase();
            default:
                //CONTAINING
                return "%" + name.toLowerCase() + "%";
        }
    }
}
