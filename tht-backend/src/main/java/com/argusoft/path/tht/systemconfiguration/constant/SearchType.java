package com.argusoft.path.tht.systemconfiguration.constant;

/**
 * Type of the search.
 *
 * @author Dhruv
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
