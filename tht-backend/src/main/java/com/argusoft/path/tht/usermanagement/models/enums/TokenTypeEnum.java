package com.argusoft.path.tht.usermanagement.models.enums;

import java.util.Arrays;

/**
 * This TokenTypeEnum provides all the types for the token.
 *
 * @author Hardik
 */
public enum TokenTypeEnum {

    VERIFICATION("verification.token.type.verification"),
    FORGOT_PASSWORD("verification.token.type.forgot.password");

    private final String key;

    TokenTypeEnum(String key) {
        this.key = key;
    }

    public static boolean isValidKey(String key) {
        return Arrays.stream(TokenTypeEnum.values())
                .anyMatch(tokenTypeEnum -> tokenTypeEnum.getKey().equals(key));
    }

    public String getKey() {
        return key;
    }

}
