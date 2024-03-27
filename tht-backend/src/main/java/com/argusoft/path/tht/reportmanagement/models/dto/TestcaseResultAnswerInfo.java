package com.argusoft.path.tht.reportmanagement.models.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

public class TestcaseResultAnswerInfo {
    @ApiModelProperty(notes = "The test case result id of the TestcaseResult",
            allowEmptyValue = false,
            example = "testcaseResultId",
            dataType = "String",
            required = true)
    private String testcaseResultId;

    @ApiModelProperty(notes = "Selected option for a question in the TestcaseResult",
            allowEmptyValue = false,
            example = "[]",
            dataType = "Set<String>",
            required = true)
    private Set<String> selectedTestcaseOptionIds;

    public String getTestcaseResultId() {
        return testcaseResultId;
    }

    public void setTestcaseResultId(String testcaseResultId) {
        this.testcaseResultId = testcaseResultId;
    }

    public Set<String> getSelectedTestcaseOptionIds() {
        return selectedTestcaseOptionIds;
    }

    public void setSelectedTestcaseOptionIds(Set<String> selectedTestcaseOptionIds) {
        this.selectedTestcaseOptionIds = selectedTestcaseOptionIds;
    }

}
