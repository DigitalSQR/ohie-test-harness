package com.argusoft.path.tht.reportmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;

import java.io.Serializable;

public class TestcaseResultAttributesInfo implements Serializable {

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
