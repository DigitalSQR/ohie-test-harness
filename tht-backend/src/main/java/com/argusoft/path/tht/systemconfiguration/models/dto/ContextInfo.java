/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.models.dto;

import io.swagger.annotations.ApiParam;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * This info is for context DTO who has Request Information, Date and LoggeIn
 * UserId.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class ContextInfo extends User implements Serializable {

    @ApiParam(
            value = "currentDate of the context",
            hidden = true
    )
    private Date currentDate;

    @ApiParam(
            value = "accessToken of the context",
            hidden = true
    )
    private String accessToken;

    @ApiParam(
            value = "email of the context",
            hidden = true
    )
    private String email;

    @ApiParam(
            value = "principalId of the context",
            hidden = true
    )
    private String principalId;

    public ContextInfo() {
        super("username", "password", new ArrayList<>());
    }

    public ContextInfo(
            String email,
            String principalId,
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username,
                password,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
        this.email = email;
        this.principalId = principalId;
    }

    public Date getCurrentDate() {
        return this.currentDate != null
                ? new Date(currentDate.getTime())
                : Calendar.getInstance().getTime();
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate != null
                ? new Date(currentDate.getTime()) : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        if (accessToken != null && accessToken.length() > 0) {
            accessToken = accessToken.split(" ")[1].trim();
        }
        this.accessToken = accessToken;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public String toString() {
        return "ContextInfo{"
                + "currentDate=" + currentDate
                + ", accessToken=" + accessToken
                + ", email=" + email
                + ", principalId=" + principalId
                + '}';
    }
}
