package com.argusoft.path.tht.systemconfiguration.audit.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

public class UserAccessAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "timestamp")
    private Date timestamp;

    @Column(name = "access_url")
    private String accessUrl;

    @Column(name = "location")
    private String location;

    @Column(name = "message")
    private String message;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "type_id")
    private String typeId;

    @Column(name = "state_id")
    private String stateId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
        return "UserAccessAuditEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", accessUrl='" + accessUrl + '\'' +
                ", location='" + location + '\'' +
                ", message='" + message + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", typeId='" + typeId + '\'' +
                ", stateId='" + stateId + '\'' +
                '}';
    }
}
