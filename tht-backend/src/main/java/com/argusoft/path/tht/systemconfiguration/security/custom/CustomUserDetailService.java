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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


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
                if (!Objects.equals(user.getPassword(), password)) {
                    throw new UsernameNotFoundException("Invalid credentials.");
                }

                //If User is not active
                if (!Objects.equals(UserServiceConstants.USER_STATUS_ACTIVE, user.getState())) {
                    if (Objects.equals(UserServiceConstants.USER_STATUS_VERIFICATION_PENDING, user.getState())) {
                        //TODO: Add appropriate message.
                    } else if (Objects.equals(UserServiceConstants.USER_STATUS_APPROVAL_PENDING, user.getState())) {
                        //TODO: Add appropriate message.
                    } else if (Objects.equals(UserServiceConstants.USER_STATUS_REJECTED, user.getState())) {
                        //TODO: Add appropriate message.
                    } else if (Objects.equals(UserServiceConstants.USER_STATUS_INACTIVE, user.getState())) {
                        //TODO: Add appropriate message.
                    } else {
                        //TODO: Add appropriate message.
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

            } catch (NumberFormatException | UsernameNotFoundException | DoesNotExistException e) {
                throw new UsernameNotFoundException("Credential are incorrect.") {
                };
            }
        }
        throw new UsernameNotFoundException("requestAttributes is incorrect") {
        };
    }
}
