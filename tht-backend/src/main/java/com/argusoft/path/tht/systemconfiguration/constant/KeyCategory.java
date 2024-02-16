package com.argusoft.path.tht.systemconfiguration.constant;

/**
 * Type of the key category.
 *
 * @author Dhruv
 */
public enum KeyCategory {
    BOOLEAN("BOOLEAN"),
    INTEGER("INTEGER"),
    STRING("STRING"),
    DATE("DATE"),
    PERCENTAGE("PERCENTAGE"),
    BIGDECIMAL("BIGDECIMAL");

    private final String name;

    private KeyCategory(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
