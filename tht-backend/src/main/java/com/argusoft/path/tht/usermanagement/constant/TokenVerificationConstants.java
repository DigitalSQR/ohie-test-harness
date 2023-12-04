package com.argusoft.path.tht.usermanagement.constant;

public class TokenVerificationConstants {

    // states
    public static final String TOKEN_STATUS_ACTIVE = "verification.token.status.active";
    public static final String TOKEN_STATUS_INACTIVE = "verification.token.status.inactive";

    // types
    public enum TOKEN_TYPE{
        VERIFICATION("verification.token.type.verification"),
        FORGOT_PASSWORD("verification.token.type.forgot.password");

        private final String key;
        TOKEN_TYPE(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

}
