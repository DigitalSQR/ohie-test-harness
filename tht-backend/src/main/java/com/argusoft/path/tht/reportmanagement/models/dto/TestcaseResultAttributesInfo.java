package com.argusoft.path.tht.reportmanagement.models.dto;

/**
 * This info is for TestcaseResultAttributes DTO that contains all the TestcaseResultAttributes model's data.
 *
 * @author Bhavi
 */

public class TestcaseResultAttributesInfo {

    private String key;

    private String value;

    public TestcaseResultAttributesInfo() {
    }

    public TestcaseResultAttributesInfo(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
