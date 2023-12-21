package com.argusoft.path.tht.usermanagement.constant;

/**
 * Constant for UserService.
 *
 * @author Dhruv
 */
public class UserServiceConstants {

    //User status
    public static final String USER_STATUS_VERIFICATION_PENDING = "user.status.verification.pending";
    public static final String USER_STATUS_APPROVAL_PENDING = "user.status.approval.pending";
    public static final String USER_STATUS_REJECTED = "user.status.rejected";
    public static final String USER_STATUS_ACTIVE = "user.status.active";
    public static final String USER_STATUS_INACTIVE = "user.status.inactive";

    //Role Ids
    public static final String ROLE_ID_ADMIN = "role.admin";
    public static final String ROLE_ID_TESTER = "role.tester";
    public static final String ROLE_ID_ASSESSEE = "role.assessee";

    //validation
    public static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
}
