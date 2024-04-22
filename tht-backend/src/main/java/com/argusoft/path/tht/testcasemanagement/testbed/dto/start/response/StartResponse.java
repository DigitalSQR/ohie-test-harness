
package com.argusoft.path.tht.testcasemanagement.testbed.dto.start.response;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * JSON schema for the tests' start operation response payload
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "createdSessions"
})
@Generated("jsonschema2pojo")
public class StartResponse {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("createdSessions")
    private List<SessionInfo> createdSessions;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("createdSessions")
    public List<SessionInfo> getCreatedSessions() {
        return createdSessions;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("createdSessions")
    public void setCreatedSessions(List<SessionInfo> createdSessions) {
        this.createdSessions = createdSessions;
    }

}
