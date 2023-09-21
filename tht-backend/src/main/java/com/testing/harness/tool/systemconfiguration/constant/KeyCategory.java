/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testing.harness.tool.systemconfiguration.constant;

/**
 * Type of the key category.
 *
 * @author dhruv
 * @since 2023-09-13
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
