package com.argusoft.path.tht.testcasemanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateMetaInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.MetaInfo;
import io.swagger.annotations.ApiModelProperty;

/**
 * This info is for TestcaseVariables DTO that contains all the TestcaseVariables model's data.
 *
 * @author Aastha
 */


public class TestcaseVariableInfo extends IdStateMetaInfo {

    @ApiModelProperty(notes = "The key for test case variable",
            allowEmptyValue = false,
            example = "testcaseVariableKey",
            dataType = "String",
            required = true)
    private String testcaseVariableKey;

    @ApiModelProperty(notes = "The default value for test case variable",
            allowEmptyValue = false,
            example = "default value",
            dataType = "String",
            required = true)
    private String defaultValue;

    @ApiModelProperty(notes = "The role id for test case variable",
            allowEmptyValue = false,
            example = "roleId",
            dataType = "String",
            required = false)
    private String roleId;

    @ApiModelProperty(notes = "The role id for test case variable",
            allowEmptyValue = false,
            example = "testcaseId",
            dataType = "String",
            required = false)
    private String testcaseId;

    public TestcaseVariableInfo(){
        super();
    }

    public TestcaseVariableInfo(String testcaseVariableKey, String defaultValue, String roleId, String testcaseId, String state, String id, MetaInfo metaInfo) {
        super(id, state, metaInfo);
        this.testcaseVariableKey = testcaseVariableKey;
        this.defaultValue = defaultValue;
        this.roleId = roleId;
        this.testcaseId = testcaseId;
    }

    public String getTestcaseVariableKey() {
        return testcaseVariableKey;
    }

    public void setTestcaseVariableKey(String testcaseVariableKey) {
        this.testcaseVariableKey = testcaseVariableKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(String testcaseId) {
        this.testcaseId = testcaseId;
    }
}
