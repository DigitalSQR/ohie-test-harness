package com.argusoft.path.tht.systemconfiguration.audit.mapper;

import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditEntity;
import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class UserAccessAuditMapper {
    public UserAccessAuditInfo convertUserAccessAuditToUserAccessAuditInfo(
            UserAccessAuditEntity userAccessAudit) {

        UserAccessAuditInfo userAccessAuditInfo = new UserAccessAuditInfo();
        userAccessAuditInfo.setId(userAccessAuditInfo.getId());
        userAccessAuditInfo.setUsername(userAccessAudit.getUsername());
        userAccessAuditInfo.setTimestamp(userAccessAudit.getTimestamp());
        userAccessAuditInfo.setLocation(userAccessAudit.getLocation());
        userAccessAuditInfo.setMessage(userAccessAudit.getMessage());
        userAccessAuditInfo.setIpAddress(userAccessAudit.getIpAddress());
        userAccessAuditInfo.setTypeId(userAccessAudit.getTypeId());
        userAccessAuditInfo.setStateId(userAccessAudit.getStateId());
        userAccessAuditInfo.setAccessUrl(userAccessAudit.getAccessUrl());

        return userAccessAuditInfo;
    }
    public UserAccessAuditEntity convertUserAccessAuditInfoToUserAccessAudit(UserAccessAuditInfo userAccessAuditInfo) {

        UserAccessAuditEntity userAccessAudit = new UserAccessAuditEntity();
        if (userAccessAuditInfo.getId() != null) {
            userAccessAudit.setId(Long.parseLong(userAccessAuditInfo.getId()));
        }
        userAccessAudit.setUsername(userAccessAuditInfo.getUsername());
        userAccessAudit.setTimestamp(userAccessAuditInfo.getTimestamp());
        userAccessAudit.setLocation(userAccessAuditInfo.getLocation());
        userAccessAudit.setMessage(userAccessAuditInfo.getMessage());
        userAccessAudit.setIpAddress(userAccessAuditInfo.getIpAddress());
        userAccessAudit.setTypeId(userAccessAuditInfo.getTypeId());
        userAccessAudit.setStateId(userAccessAuditInfo.getStateId());
        userAccessAudit.setAccessUrl(userAccessAuditInfo.getAccessUrl());

        return userAccessAudit;
    }
}
