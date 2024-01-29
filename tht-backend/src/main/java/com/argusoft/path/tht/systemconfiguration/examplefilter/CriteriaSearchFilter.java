package com.argusoft.path.tht.systemconfiguration.examplefilter;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.springframework.data.jpa.domain.Specification;

public interface CriteriaSearchFilter<T> {

    Specification<T> buildSpecification(ContextInfo contextInfo) throws InvalidParameterException;


}
