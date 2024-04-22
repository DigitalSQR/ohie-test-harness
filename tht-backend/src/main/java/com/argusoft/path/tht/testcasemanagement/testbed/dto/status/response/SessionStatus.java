
package com.argusoft.path.tht.testcasemanagement.testbed.dto.status.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "session",
    "result",
    "startTime",
    "endTime",
    "message",
    "logs",
    "report"
})
@Generated("jsonschema2pojo")
public class SessionStatus {

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
    @JsonProperty("result")
    private Result result;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("startTime")
    private String startTime;
    @JsonProperty("endTime")
    private String endTime;
    @JsonProperty("message")
    private String message;
    @JsonProperty("logs")
    private List<String> logs;
    @JsonProperty("report")
    private String report;

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

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("result")
    public Result getResult() {
        return result;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("result")
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("startTime")
    public String getStartTime() {
        return startTime;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("startTime")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("endTime")
    public String getEndTime() {
        return endTime;
    }

    @JsonProperty("endTime")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("logs")
    public List<String> getLogs() {
        return logs;
    }

    @JsonProperty("logs")
    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    @JsonProperty("report")
    public String getReport() {
        return report;
    }

    @JsonProperty("report")
    public void setReport(String report) {
        this.report = report;
    }

    @Generated("jsonschema2pojo")
    public enum Result {

        SUCCESS("SUCCESS"),
        FAILURE("FAILURE"),
        UNDEFINED("UNDEFINED");
        private final String value;
        private final static Map<String, Result> CONSTANTS = new HashMap<String, Result>();

        static {
            for (Result c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Result(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static Result fromValue(String value) {
            Result constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
