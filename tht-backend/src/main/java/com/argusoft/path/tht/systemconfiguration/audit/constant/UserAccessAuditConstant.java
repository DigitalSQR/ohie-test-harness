package com.argusoft.path.tht.systemconfiguration.audit.constant;

public class UserAccessAuditConstant {

    private UserAccessAuditConstant() {
    }

    public static final String USER_ACCESS_AUDIT_SUCCESS_STATE = "jm.org.ta.state.useraccessauditinfo.success";
    public static final String USER_ACCESS_AUDIT_FAIL_STATE = "jm.org.ta.state.useraccessauditinfo.fail";
    public static final String USER_ACCESS_AUDIT_EXCEPTION_STATE = "jm.org.ta.state.useraccessauditinfo.exception";

    public static final String USER_ACCESS_AUDIT_REGULAR_TYPE = "jm.org.ta.type.useraccessauditinfo.regular";
    public static final String USER_ACCESS_AUDIT_LOGOFF_TYPE = "jm.org.ta.type.useraccessauditinfo.logoff";
    public static final String USER_ACCESS_AUDIT_ACCESS_DENIED_TYPE = "jm.org.ta.type.useraccessauditinfo.accessdenied";
    public static final String USER_ACCESS_AUDIT_LOGON_TYPE = "jm.org.ta.type.useraccessauditinfo.logon";
    public static final String USER_ACCESS_AUDIT_PERMISSION_DENIED_TYPE = "jm.org.ta.type.useraccessauditinfo.permissiondenied";
    public static final String USER_ACCESS_AUDIT_OPERATION_FAILED_TYPE = "jm.org.ta.type.useraccessauditinfo.operationfailed";
}
