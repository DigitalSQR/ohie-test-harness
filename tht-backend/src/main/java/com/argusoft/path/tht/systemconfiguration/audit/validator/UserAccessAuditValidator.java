package com.argusoft.path.tht.systemconfiguration.audit.validator;

import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditInfo;
import com.argusoft.path.tht.systemconfiguration.audit.service.UserAccessAuditService;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccessAuditValidator {

    @Autowired
    UserAccessAuditService userAccessAuditService;

    public UserAccessAuditInfo createUserAccessAudit(UserAccessAuditInfo userAccessAuditInfo,
                                                     ContextInfo contextInfo){

        return this.userAccessAuditService.createUserAccessAudit(userAccessAuditInfo, contextInfo);

    }

}
