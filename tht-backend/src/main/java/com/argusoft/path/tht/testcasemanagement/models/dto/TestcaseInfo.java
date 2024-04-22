package com.argusoft.path.tht.testcasemanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

/**
 * This info is for Testcase DTO that contains all the Testcase model's data.
 *
 * @author Dhruv
 */
public class TestcaseInfo extends IdStateNameMetaInfo {

    @ApiModelProperty(notes = "The unique rank of the testcase",
            allowEmptyValue = false,
            example = "1",
            dataType = "Integer",
            required = true)
    private Integer rank;

    @ApiModelProperty(notes = "The className of the testcase",
            allowEmptyValue = false,
            example = "className",
            dataType = "String",
            required = false)
    private String className;

    @ApiModelProperty(notes = "The isManual of the testcase",
            allowEmptyValue = false,
            example = "true",
            dataType = "Boolean",
            required = false)
    private Boolean isManual;

    @ApiModelProperty(notes = "The questionType of the testcase",
            allowEmptyValue = false,
            example = "singleSelect, multiSelect",
            dataType = "String",
            required = false)
    private String questionType;

    @ApiModelProperty(notes = "The specificationId of the specification",
            allowEmptyValue = false,
            example = "specificationId",
            dataType = "String",
            required = true)
    private String specificationId;

    @ApiModelProperty(notes = "The failureMessage of the specification",
            allowEmptyValue = false,
            example = "failureMessage",
            dataType = "String",
            required = true)
    private String failureMessage;

    @ApiModelProperty(notes = "The failureMessage of the specification",
            allowEmptyValue = false,
            example = "failureMessage",
            dataType = "String",
            required = true)
    private String testcaseRunEnvironment;

    @ApiModelProperty(notes = "The testsuite id from the zip file",
            allowEmptyValue = false,
            example = "Verify_Patient_Test_Suite_ID",
            dataType = "String",
            required = true)
    private String testSuiteId;

    @ApiModelProperty(notes = "The actor api key from the sut actor",
            allowEmptyValue = false,
            example = "ACTOR_1",
            dataType = "String",
            required = true)
    private String sutActorApiKey;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public String getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(String specificationId) {
        this.specificationId = specificationId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getTestcaseRunEnvironment() {
        return testcaseRunEnvironment;
    }

    public void setTestcaseRunEnvironment(String testcaseRunEnvironment) {
        this.testcaseRunEnvironment = testcaseRunEnvironment;
    }

    public String getTestSuiteId() {
        return testSuiteId;
    }

    public void setTestSuiteId(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }

    public String getSutActorApiKey() {
        return sutActorApiKey;
    }

    public void setSutActorApiKey(String sutActorApiKey) {
        this.sutActorApiKey = sutActorApiKey;
    }
}
