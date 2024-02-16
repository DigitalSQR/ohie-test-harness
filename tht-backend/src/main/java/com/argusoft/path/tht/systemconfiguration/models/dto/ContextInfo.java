package com.argusoft.path.tht.systemconfiguration.models.dto;

import com.argusoft.path.tht.systemconfiguration.constant.Module;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * This info is for context DTO who has Request Information, Date and LoggeIn
 * UserId.
 *
 * @author Dhruv
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

    private Boolean isAssessee;

    private Boolean isTester;

    private Boolean isAdmin;

    private Module module;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public ContextInfo() {
        super("username", "password", new ArrayList<>());
    }

    public ContextInfo(
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
        setIsAssessee();
        setIsAdmin();
        setIsTester();
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

    private void setIsAssessee() {
        this.isAssessee = this.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().contains(UserServiceConstants.ROLE_ID_ASSESSEE));
    }

    private void setIsAdmin() {
        this.isAdmin = this.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().contains(UserServiceConstants.ROLE_ID_ADMIN));
    }

    private void setIsTester() {
        this.isTester = this.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().contains(UserServiceConstants.ROLE_ID_TESTER));
    }

    public boolean isAssessee() {
        return isAssessee;
    }

    public boolean isTesterOrAdmin() {
        return isTester || isAdmin;
    }

}
