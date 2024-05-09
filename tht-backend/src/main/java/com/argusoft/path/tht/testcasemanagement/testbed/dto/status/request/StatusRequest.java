
package com.argusoft.path.tht.testcasemanagement.testbed.dto.status.request;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * JSON schema for the tests' status operation request payload
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "session",
    "withLogs",
    "withReports"
})
@Generated("jsonschema2pojo")
public class StatusRequest {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("session")
    private List<String> session;
    @JsonProperty("withLogs")
    private Boolean withLogs;
    @JsonProperty("withReports")
    private Boolean withReports;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("session")
    public List<String> getSession() {
        return session;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("session")
    public void setSession(List<String> session) {
        this.session = session;
    }

    @JsonProperty("withLogs")
    public Boolean getWithLogs() {
        return withLogs;
    }

    @JsonProperty("withLogs")
    public void setWithLogs(Boolean withLogs) {
        this.withLogs = withLogs;
    }

    @JsonProperty("withReports")
    public Boolean getWithReports() {
        return withReports;
    }

    @JsonProperty("withReports")
    public void setWithReports(Boolean withReports) {
        this.withReports = withReports;
    }

}
