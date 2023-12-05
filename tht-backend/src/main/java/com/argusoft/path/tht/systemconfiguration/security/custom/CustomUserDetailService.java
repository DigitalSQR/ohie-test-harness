/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.security.custom;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dhruv
 * @since 2023-09-13
 */
@Service
@Transactional
@Metrics(registry = "CustomUserDetailService")
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    @Timed(name = "loadUserByUsername")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String password = request.getParameter("password");
            try {
                UserEntity user = userService.getUserByEmail(username, Constant.SUPER_USER_CONTEXT);
                if (StringUtils.isEmpty(user.getPassword()) || !Objects.equals(user.getPassword(), password)) {
                    throw new UsernameNotFoundException("Credential are incorrect.");
                }

                //If User is not active
                if (!Objects.equals(UserServiceConstants.USER_STATUS_ACTIVE, user.getState())) {
                    if (Objects.equals(UserServiceConstants.USER_STATUS_VERIFICATION_PENDING, user.getState())) {
                        throw new UsernameNotFoundException("Pending email verification.") {
                        };
                    } else if (Objects.equals(UserServiceConstants.USER_STATUS_APPROVAL_PENDING, user.getState())) {
                        throw new UsernameNotFoundException("Pending admin approval.") {
                        };
                    } else if (Objects.equals(UserServiceConstants.USER_STATUS_REJECTED, user.getState())) {
                        throw new UsernameNotFoundException("Admin approval has been rejected.") {
                        };
                    } else {
                        //Only state left is UserServiceConstants.USER_STATUS_INACTIVE.
                        throw new UsernameNotFoundException("User is inactive.") {
                        };
                    }
                }

                List<GrantedAuthority> authorities
                        = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getId()))
                        .collect(Collectors.toList());

                return new ContextInfo(
                        user.getEmail(),
                        user.getId(),
                        password,
                        true,
                        true,
                        true,
                        true,
                        authorities);

            } catch (NumberFormatException | DoesNotExistException e) {
                throw new UsernameNotFoundException("Credential are incorrect.") {
                };
            }
        }
        throw new UsernameNotFoundException("requestAttributes is incorrect") {
        };
    }
}
