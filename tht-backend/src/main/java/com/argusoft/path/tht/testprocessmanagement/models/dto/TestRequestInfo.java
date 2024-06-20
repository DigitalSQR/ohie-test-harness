package com.argusoft.path.tht.testprocessmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This info is for Component DTO that contains all the Component model's data.
 *
 * @author Dhruv
 */
public class TestRequestInfo extends IdStateNameMetaInfo {


    @ApiModelProperty(notes = "The assesseeId of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String assesseeId;

    @ApiModelProperty(notes = "The approverId of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "String",
            required = true)
    private String approverId;

    @ApiModelProperty(notes = "The testRequestUrls of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "Set<TestRequestUrlInfo>",
            required = true)
    private Set<TestRequestUrlInfo> testRequestUrls;

    @ApiModelProperty(notes = "The rejection message of the testRequest",
            allowEmptyValue = true,
            example = "Not a valid request",
            dataType = "String",
            required = false)
    private String message;

    @ApiModelProperty(notes = "The test request value",
            allowEmptyValue = false,
            example = "[]",
            dataType = "List<TestRequestValueInfo>",
            required = true)
    private Set<TestRequestValueInfo> testRequestValues;

    public Set<TestRequestUrlInfo> getTestRequestUrls() {
        if (testRequestUrls == null) {
            testRequestUrls = new HashSet<>();
        }
        return testRequestUrls;
    }

    public void setTestRequestUrls(Set<TestRequestUrlInfo> testRequestUrls) {
        this.testRequestUrls = testRequestUrls;
    }

    public String getAssesseeId() {
        return assesseeId;
    }

    public void setAssesseeId(String assesseeId) {
        this.assesseeId = assesseeId;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<TestRequestValueInfo> getTestRequestValues() {
        if (testRequestValues == null) {
            testRequestValues = new HashSet<>();
        }
        return testRequestValues;
    }

    public void setTestRequestValues(Set<TestRequestValueInfo> testRequestValues) {
        this.testRequestValues = testRequestValues;
    }
}
