package com.argusoft.path.tht.systemconfiguration.audit.entity;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class UserAccessAuditInfo extends IdStateNameMetaInfo {

    @ApiModelProperty(notes = "The username for UserAccessAudit model",
            allowEmptyValue = true,
            example = "SYSTEM_USERabc@def.com",
            dataType = "String",
            required = false)
    private String username;

    @ApiModelProperty(notes = "The timestamp for UserAccessAudit model",
            allowEmptyValue = false,
            example = "2021-07-20T09:14:29.448Z",
            dataType = "Date",
            required = true)
    private Date timestamp;

    @ApiModelProperty(notes = "The location for UserAccessAudit model",
            allowEmptyValue = true,
            example = "Jamaica",
            dataType = "String",
            required = false)
    private String location;

    @ApiModelProperty(notes = "The accessUrl for UserAccessAudit model",
            allowEmptyValue = true,
            example = "/api/parish",
            dataType = "String",
            required = false)
    private String accessUrl;

    @ApiModelProperty(notes = "The message for UserAccessAudit model",
            allowEmptyValue = true,
            example = "message",
            dataType = "String",
            required = false)
    private String message;

    @ApiModelProperty(notes = "The ip_address for UserAccessAudit model",
            allowEmptyValue = true,
            example = "message",
            dataType = "String",
            required = true)
    private String ipAddress;

    @ApiModelProperty(notes = "The type_id for UserAccessAudit model",
            allowEmptyValue = true,
            example = "jm.org.ta.type.useraccessauditinfo.regular",
            dataType = "String",
            required = true)
    private String typeId;

    @ApiModelProperty(notes = "The state_id for UserAccessAudit model",
            allowEmptyValue = true,
            example = "jm.org.ta.state.useraccessauditinfo.exception",
            dataType = "String",
            required = true)
    private String stateId;

    public UserAccessAuditInfo() {
    }

//    /**
//     * This is copy ConStructure that make deep copy of the UserAccessAuditInfo.
//     *
//     * @param userAccessAuditInfo UserAccessAuditInfo
//     */
//    public UserAccessAuditInfo(UserAccessAudit userAccessAuditInfo) {
//        super(userAccessAuditInfo);
//        if (userAccessAuditInfo != null) {
//            this.username = userAccessAuditInfo.getUsername();
//            this.timestamp = new Date(userAccessAuditInfo.getTimestamp().getTime());
//            this.location = userAccessAuditInfo.getLocation();
//            this.message = userAccessAuditInfo.getMessage();
//            this.ipAddress = userAccessAuditInfo.getIpAddress();
//            this.typeId = userAccessAuditInfo.getTypeId();
//            this.stateId = userAccessAuditInfo.getStateId();
//            this.accessUrl = userAccessAuditInfo.getAccessUrl();
//        }
//    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    @Override
    public String toString() {
        return "UserAccessAuditInfo{" +
                "username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", location='" + location + '\'' +
                ", accessUrl='" + accessUrl + '\'' +
                ", message='" + message + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", typeId='" + typeId + '\'' +
                ", stateId='" + stateId + '\'' +
                '}';
    }
}
