package com.argusoft.path.tht.systemconfiguration.audit.controller;

import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditInfo;
import com.argusoft.path.tht.systemconfiguration.audit.validator.UserAccessAuditValidator;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-access-audits")
public class UserAccessAuditController {

    @Autowired
    UserAccessAuditValidator userAccessAuditValidator;

    @PostMapping("")
    public UserAccessAuditInfo createUserAccessAudit(@RequestBody UserAccessAuditInfo userAccessAuditInfo,
                                                     @RequestAttribute(name = "contextInfo") ContextInfo contextInfo){
        return this.userAccessAuditValidator.createUserAccessAudit(userAccessAuditInfo, contextInfo);
    }

}
