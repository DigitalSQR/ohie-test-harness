
package com.argusoft.path.tht.testcasemanagement.testbed.dto.start.request;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * JSON schema for the tests' start operation request payload
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "system",
    "actor",
    "forceSequentialExecution",
    "testSuite",
    "testCase",
    "inputMapping"
})
@Generated("jsonschema2pojo")
public class StartRequest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("system")
    private String system;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("actor")
    private String actor;
    @JsonProperty("forceSequentialExecution")
    private Boolean forceSequentialExecution;
    @JsonProperty("testSuite")
    private List<String> testSuite;
    @JsonProperty("testCase")
    private List<String> testCase;
    @JsonProperty("inputMapping")
    private List<Input> inputMapping;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("system")
    public String getSystem() {
        return system;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("system")
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("actor")
    public String getActor() {
        return actor;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("actor")
    public void setActor(String actor) {
        this.actor = actor;
    }

    @JsonProperty("forceSequentialExecution")
    public Boolean getForceSequentialExecution() {
        return forceSequentialExecution;
    }

    @JsonProperty("forceSequentialExecution")
    public void setForceSequentialExecution(Boolean forceSequentialExecution) {
        this.forceSequentialExecution = forceSequentialExecution;
    }

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

    @JsonProperty("inputMapping")
    public List<Input> getInputMapping() {
        return inputMapping;
    }

    @JsonProperty("inputMapping")
    public void setInputMapping(List<Input> inputMapping) {
        this.inputMapping = inputMapping;
    }

}
