package com.argusoft.path.tht.systemconfiguration.audit.service;

import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditInfo;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;

public interface UserAccessAuditService {

    public UserAccessAuditInfo createUserAccessAudit(UserAccessAuditInfo userAccessAuditInfo,
                                                     ContextInfo contextInfo);

    public UserAccessAuditInfo getUserAccessAuditById(String userAccessAuditId, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException;

}
