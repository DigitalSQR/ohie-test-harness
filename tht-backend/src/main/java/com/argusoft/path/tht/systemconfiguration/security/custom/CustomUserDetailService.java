/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.security.custom;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.usermanagement.service.UserService;
import com.codahale.metrics.annotation.Timed;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import io.astefanutti.metrics.aspectj.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

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
                UserSearchFilter searchFilter = new UserSearchFilter();
                searchFilter.setEmail(username);
                Page<UserEntity> usersPage = userService.searchUsers(
                        new ArrayList<>(),
                        searchFilter,
                        Constant.SINGLE_VALUE_PAGE,
                        Constant.SUPER_USER_CONTEXT);
                if (usersPage.getTotalElements() == 0) {
                    throw new UsernameNotFoundException("Invalid credentials.");
                }
                UserEntity user = usersPage.getContent().get(0);
                if(!Objects.equals(user.getPassword(), password)) {
                    throw new UsernameNotFoundException("Invalid credentials.");
                }
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("SIMPLE_LOGIN"));

                return new ContextInfo(
                        user.getEmail(),
                        user.getId(),
                        password,
                        true,
                        true,
                        true,
                        true,
                        authorities);

            } catch (OperationFailedException | NumberFormatException | UsernameNotFoundException |
                     InvalidParameterException | PermissionDeniedException |
                     MissingParameterException e) {
                throw new UsernameNotFoundException("Credential are "
                        + "incorrect.") {
                };
            }
        }
        throw new UsernameNotFoundException("requestAttributes is incorrect") {
        };
    }
}
