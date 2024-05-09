
package com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "testSuite",
    "testCases",
    "specifications"
})
@Generated("jsonschema2pojo")
public class Identifiers {

    @JsonProperty("testSuite")
    private String testSuite;
    @JsonProperty("testCases")
    private List<String> testCases;
    @JsonProperty("specifications")
    private List<Specification> specifications;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("testSuite")
    public String getTestSuite() {
        return testSuite;
    }

    @JsonProperty("testSuite")
    public void setTestSuite(String testSuite) {
        this.testSuite = testSuite;
    }

    @JsonProperty("testCases")
    public List<String> getTestCases() {
        return testCases;
    }

    @JsonProperty("testCases")
    public void setTestCases(List<String> testCases) {
        this.testCases = testCases;
    }

    @JsonProperty("specifications")
    public List<Specification> getSpecifications() {
        return specifications;
    }

    @JsonProperty("specifications")
    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
