package com.argusoft.path.tht.reportmanagement.models.dto;

import io.swagger.annotations.ApiModelProperty;

public class TestcaseResultViewInfo {

    @ApiModelProperty(notes = "The name of the Component",
            allowEmptyValue = false,
            example = "abc",
            dataType = "String",
            required = true)
    String componentName;

    @ApiModelProperty(notes = "The Id of the Component",
            allowEmptyValue = false,
            example = "component.client.registry",
            dataType = "String",
            required = false)
    String componentId;

    //Fill this only if testRequest it self is accepted/inprogress/finished
    @ApiModelProperty(notes = "The state of the Testcase Result",
            allowEmptyValue = false,
            example = "tht.state.viapointinfo.active",
            dataType = "String",
            required = false)
    String testcaseResultState;
    //if it is skipped because it was inactive then add inactive state as well here. and testcaseResultId null.

    //Fill this only if testRequest it-self is in finished state
    @ApiModelProperty(notes = "The success of the Testcase Result",
            allowEmptyValue = false,
            example = "true",
            dataType = "boolean",
            required = false)
    boolean isSuccess;

    @ApiModelProperty(notes = "The grade of the Testcase Result",
            allowEmptyValue = false,
            example = "A",
            dataType = "String",
            required = false)
    String grade;

    public TestcaseResultViewInfo() {
    }

    public TestcaseResultViewInfo(String componentName, String componentId, String testcaseResultState, boolean isSuccess, String grade) {
        this.componentName = componentName;
        this.componentId = componentId;
        this.testcaseResultState = testcaseResultState;
        this.isSuccess = isSuccess;
        this.grade = grade;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getTestcaseResultState() {
        return testcaseResultState;
    }

    public void setTestcaseResultState(String testcaseResultState) {
        this.testcaseResultState = testcaseResultState;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

}
