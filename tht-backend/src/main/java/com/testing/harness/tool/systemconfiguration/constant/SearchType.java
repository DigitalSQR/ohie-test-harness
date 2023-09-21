/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testing.harness.tool.systemconfiguration.constant;

/**
 * Type of the search.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public enum SearchType {
    STARTING("STARTING"),
    ENDING("ENDING"),
    CONTAINING("CONTAINING"),
    EXACTLY("EXACTLY");

    private final String name;

    private SearchType(String s) {
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
