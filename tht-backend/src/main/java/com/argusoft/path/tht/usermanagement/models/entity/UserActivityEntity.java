package com.argusoft.path.tht.usermanagement.models.entity;

import com.argusoft.path.tht.systemconfiguration.models.entity.IdEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_activity")
public class UserActivityEntity extends IdEntity {

    @Column(name="accessTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date accessTime;

    @Column(name="userId")
    private String userId;

    @Column(name = "apiUrl")
    private String apiUrl;

    @Column(name = "scope")
    private String scope;

    public UserActivityEntity(){

    }

    public UserActivityEntity(String id){
        this.setId(id);
    }

    public UserActivityEntity(UserActivityEntity userActivity){
        this.setId(userActivity.getId());
        this.setAccessTime(userActivity.getAccessTime());
        this.setApiUrl(userActivity.getApiUrl());
        this.setScope(userActivity.getScope());
        this.setUserId(userActivity.getUserId());
    }


    @Override
    public String toString() {
        return "UserActivity{" +
                ", accessTime='" + accessTime + '\'' +
                ", userId='" + userId + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
