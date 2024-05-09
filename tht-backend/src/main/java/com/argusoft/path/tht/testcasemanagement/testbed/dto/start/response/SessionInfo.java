
package com.argusoft.path.tht.testcasemanagement.testbed.dto.start.response;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "testSuite",
    "testCase",
    "session"
})
@Generated("jsonschema2pojo")
public class SessionInfo {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("testSuite")
    private String testSuite;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("testCase")
    private String testCase;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("session")
    private String session;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("testSuite")
    public String getTestSuite() {
        return testSuite;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("testSuite")
    public void setTestSuite(String testSuite) {
        this.testSuite = testSuite;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("testCase")
    public String getTestCase() {
        return testCase;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("testCase")
    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("session")
    public String getSession() {
        return session;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("session")
    public void setSession(String session) {
        this.session = session;
    }

}
