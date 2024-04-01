package com.argusoft.path.tht.testprocessmanagement.models.dto;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultViewInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

public class TestRequestViewInfo extends IdStateNameMetaInfo {

    @ApiModelProperty(notes = "The assessee name of the testRequest",
            allowEmptyValue = false,
            example = "Abc",
            dataType = "String",
            required = true)
    String assesseeName;

    @ApiModelProperty(notes = "The assessee email of the testRequest",
            allowEmptyValue = false,
            example = "test@yopmail.com",
            dataType = "String",
            required = true)
    String assesseeEmail;

    @ApiModelProperty(notes = "The test result of components of the testRequest",
            allowEmptyValue = false,
            example = "1",
            dataType = "List<TestcaseResultViewInfo>",
            required = true)
    List<TestcaseResultViewInfo> testResultOfComponents;

    //add if testRequest is finished
    @ApiModelProperty(notes = "The success of the testRequest",
            allowEmptyValue = false,
            example = "true",
            dataType = "boolean",
            required = true)
    boolean isSuccess;

    @ApiModelProperty(notes = "The grade of the testRequest",
            allowEmptyValue = false,
            example = "A",
            dataType = "String",
            required = true)
    String grade;

    public TestRequestViewInfo() {
    }

    public TestRequestViewInfo(String assesseeName, String assesseeEmail, List<TestcaseResultViewInfo> testResultOfComponents, boolean isSuccess, String grade) {
        this.assesseeName = assesseeName;
        this.assesseeEmail = assesseeEmail;
        this.testResultOfComponents = testResultOfComponents;
        this.isSuccess = isSuccess;
        this.grade = grade;
    }

    public String getAssesseeName() {
        return assesseeName;
    }

    public void setAssesseeName(String assesseeName) {
        this.assesseeName = assesseeName;
    }

    public String getAssesseeEmail() {
        return assesseeEmail;
    }

    public void setAssesseeEmail(String assesseeEmail) {
        this.assesseeEmail = assesseeEmail;
    }

    public List<TestcaseResultViewInfo> getTestResultOfComponents() {
        return testResultOfComponents;
    }

    public void setTestResultOfComponents(List<TestcaseResultViewInfo> testResultOfComponents) {
        this.testResultOfComponents = testResultOfComponents;
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

    @Override
    public String toString() {
        return "TestRequestViewInfo{" +
                "assesseeName='" + assesseeName + '\'' +
                ", assesseeEmail='" + assesseeEmail + '\'' +
                ", testResultOfComponents=" + testResultOfComponents +
                ", isSuccess=" + isSuccess +
                ", grade='" + grade + '\'' +
                '}';
    }
}
