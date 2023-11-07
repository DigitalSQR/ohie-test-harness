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
import org.springframework.security.oauth2.core.user.OAuth2User;

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

    public ContextInfo() {
        super("username", "password", new ArrayList<>());
    }

    public ContextInfo(OAuth2User oAuth2User) {
        super(oAuth2User.getAttribute("email"), "password", oAuth2User.getAuthorities());
    }
    
    public  ContextInfo(
            String email,
            String userName,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(userName,
                password,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
        this.email = email;
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

}
