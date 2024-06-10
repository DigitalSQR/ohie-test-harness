package com.argusoft.path.tht.testprocessmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdInfo;
import io.swagger.annotations.ApiModelProperty;

/**
 * This info is for TestRequesrValue DTO that contains all the TestRequestValue model's data.
 *
 * @author Aastha
 */


public class TestRequestValueInfo extends IdInfo {
    @ApiModelProperty(notes = "The value for test request value",
            allowEmptyValue = false,
            example = "value",
            dataType = "String",
            required = true)
    private String value;

    @ApiModelProperty(notes = "The testcaseVariableId for test request value",
            allowEmptyValue = false,
            example = "testcaseVariableId",
            dataType = "String",
            required = true)
    private String testcaseVariableId;

    @ApiModelProperty(notes = "The testrequestId for test request value",
            allowEmptyValue = false,
            example = "testrequestId",
            dataType = "String",
            required = true)
    private String testRequestId;

    public TestRequestValueInfo(String id, String value, String testcaseVariableId, String testRequestId) {
        super(id);
        this.value = value;
        this.testcaseVariableId = testcaseVariableId;
        this.testRequestId = testRequestId;
    }

    public TestRequestValueInfo(){
       super();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTestcaseVariableId() {
        return testcaseVariableId;
    }

    public void setTestcaseVariableId(String testcaseVariableId) {
        this.testcaseVariableId = testcaseVariableId;
    }

    public String getTestRequestId() {
        return testRequestId;
    }

    public void setTestRequestId(String testRequestId) {
        this.testRequestId = testRequestId;
    }
}
