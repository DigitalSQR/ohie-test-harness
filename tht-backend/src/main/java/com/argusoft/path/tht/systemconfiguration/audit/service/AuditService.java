package com.argusoft.path.tht.systemconfiguration.audit.service;

import com.argusoft.path.tht.systemconfiguration.audit.filter.SearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;

import java.util.List;

/**
 * @author Bhavi This interface provides contract for Audit API.
 */
public interface AuditService {

    public List<Object> searchAudit(SearchFilter searchFilter, ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException;
}
