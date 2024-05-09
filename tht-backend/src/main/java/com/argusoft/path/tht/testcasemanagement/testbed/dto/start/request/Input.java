
package com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "testSuite",
    "testCase",
    "input"
})
@Generated("jsonschema2pojo")
public class Input {

    @JsonProperty("testSuite")
    private List<String> testSuite;
    @JsonProperty("testCase")
    private List<String> testCase;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("input")
    private AnyContent input;

    @JsonProperty("testSuite")
    public List<String> getTestSuite() {
        return testSuite;
    }

    @JsonProperty("testSuite")
    public void setTestSuite(List<String> testSuite) {
        this.testSuite = testSuite;
    }

    @JsonProperty("testCase")
    public List<String> getTestCase() {
        return testCase;
    }

    @JsonProperty("testCase")
    public void setTestCase(List<String> testCase) {
        this.testCase = testCase;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("input")
    public AnyContent getInput() {
        return input;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("input")
    public void setInput(AnyContent input) {
        this.input = input;
    }

}
