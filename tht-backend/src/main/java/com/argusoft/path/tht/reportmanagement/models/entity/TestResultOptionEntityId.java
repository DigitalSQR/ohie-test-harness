package com.argusoft.path.tht.reportmanagement.models.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * This model is to provide identifier for the testRequestUrl table.
 *
 * @author Aastha
 */
public class TestResultOptionEntityId implements Serializable {

    private String testcaseResult;

    private String testcaseOption;

    public String getTestcaseResultId() {
        return testcaseResult;
    }

    public void setTestcaseResultId(String testcaseResult) {
        this.testcaseResult = testcaseResult;
    }

    public String getTestcaseOption() {
        return testcaseOption;
    }

    public void setTestcaseOption(String testcaseOption) {
        this.testcaseOption = testcaseOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestResultOptionEntityId testResultOptionEntityId = (TestResultOptionEntityId) o;
        return testcaseResult.equals(testResultOptionEntityId.testcaseResult)
                && testcaseOption.equals(testResultOptionEntityId.testcaseOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testcaseResult, testcaseOption);
    }
}