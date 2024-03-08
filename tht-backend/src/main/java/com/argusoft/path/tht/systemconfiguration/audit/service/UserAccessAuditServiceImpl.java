package com.argusoft.path.tht.systemconfiguration.audit.service;

import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.stereotype.Service;

@Service
public class UserAccessAuditServiceImpl implements UserAccessAuditService{

    @Override
    public UserAccessAuditInfo createUserAccessAudit(UserAccessAuditInfo userAccessAuditInfo, ContextInfo contextInfo) {
        //convert info to entity
        //save and return
        return null;
    }

}
