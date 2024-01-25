package com.argusoft.path.tht.systemconfiguration.examplefilter;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.BiFunction;

public interface CriteriaSearchFilter<T> {

    Specification<T> buildSpecification(ContextInfo contextInfo) throws InvalidParameterException;


}
