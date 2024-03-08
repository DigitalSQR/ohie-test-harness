package com.argusoft.path.tht.systemconfiguration.audit.service;

import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditEntity;
import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditInfo;
import com.argusoft.path.tht.systemconfiguration.audit.mapper.UserAccessAuditMapper;
import com.argusoft.path.tht.systemconfiguration.audit.repository.UserAccessAuditRepository;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccessAuditServiceImpl implements UserAccessAuditService{

    @Autowired
    UserAccessAuditMapper userAccessAuditMapper;

    @Autowired
    UserAccessAuditRepository userAccessAuditRepository;

    @Override
    public UserAccessAuditInfo createUserAccessAudit(UserAccessAuditInfo userAccessAuditInfo, ContextInfo contextInfo) {
        //convert info to entity
        UserAccessAuditEntity userAccessAuditEntity = userAccessAuditMapper.convertUserAccessAuditInfoToUserAccessAudit(userAccessAuditInfo);

        //save and return
        userAccessAuditRepository.save(userAccessAuditEntity);

        return userAccessAuditMapper.convertUserAccessAuditToUserAccessAuditInfo(userAccessAuditEntity);
    }

}
