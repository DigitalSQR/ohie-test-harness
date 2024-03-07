package com.argusoft.path.tht.systemconfiguration.examplefilter;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.data.jpa.domain.Specification;

public interface CriteriaSearchFilter<T> {

    Specification<T> buildSpecification(ContextInfo contextInfo) throws InvalidParameterException;


}
