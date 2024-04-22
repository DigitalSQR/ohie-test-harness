
package com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.request;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * JSON schema for the test suites' deploy operation request payload
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "specification",
    "testSuite",
    "ignoreWarnings",
    "replaceTestHistory",
    "updateSpecification"
})
@Generated("jsonschema2pojo")
public class DeployRequest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("specification")
    private String specification;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("testSuite")
    private String testSuite;
    @JsonProperty("ignoreWarnings")
    private Boolean ignoreWarnings = false;
    @JsonProperty("replaceTestHistory")
    private Boolean replaceTestHistory = false;
    @JsonProperty("updateSpecification")
    private Boolean updateSpecification = false;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("specification")
    public String getSpecification() {
        return specification;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("specification")
    public void setSpecification(String specification) {
        this.specification = specification;
    }

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

    @JsonProperty("ignoreWarnings")
    public Boolean getIgnoreWarnings() {
        return ignoreWarnings;
    }

    @JsonProperty("ignoreWarnings")
    public void setIgnoreWarnings(Boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }

    @JsonProperty("replaceTestHistory")
    public Boolean getReplaceTestHistory() {
        return replaceTestHistory;
    }

    @JsonProperty("replaceTestHistory")
    public void setReplaceTestHistory(Boolean replaceTestHistory) {
        this.replaceTestHistory = replaceTestHistory;
    }

    @JsonProperty("updateSpecification")
    public Boolean getUpdateSpecification() {
        return updateSpecification;
    }

    @JsonProperty("updateSpecification")
    public void setUpdateSpecification(Boolean updateSpecification) {
        this.updateSpecification = updateSpecification;
    }

}
