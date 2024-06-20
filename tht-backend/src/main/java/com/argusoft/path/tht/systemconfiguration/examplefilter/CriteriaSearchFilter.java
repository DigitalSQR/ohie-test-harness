package com.argusoft.path.tht.systemconfiguration.examplefilter;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Criteria Search Filter
 *
 * @author Hardik
 */

public interface CriteriaSearchFilter<T> {

    Specification<T> buildSpecification(ContextInfo contextInfo) throws InvalidParameterException;

    Specification<T> buildSpecification(Pageable pageable, ContextInfo contextInfo) throws InvalidParameterException;

    Specification<T> buildLikeSpecification(ContextInfo contextInfo) throws InvalidParameterException;

    Specification<T> buildLikeSpecification(Pageable pageable, ContextInfo contextInfo) throws InvalidParameterException;

}
