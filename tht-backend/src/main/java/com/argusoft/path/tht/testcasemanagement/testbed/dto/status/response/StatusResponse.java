
package com.argusoft.path.tht.testcasemanagement.testbed.dto.status.response;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * JSON schema for the tests' status operation response payload
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sessions"
})
@Generated("jsonschema2pojo")
public class StatusResponse {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sessions")
    private List<SessionStatus> sessions;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sessions")
    public List<SessionStatus> getSessions() {
        return sessions;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sessions")
    public void setSessions(List<SessionStatus> sessions) {
        this.sessions = sessions;
    }

}
