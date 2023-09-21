/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testing.harness.tool.systemconfiguration.security.filters;

import com.testing.harness.tool.systemconfiguration.models.dto.ContextInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

/**
 * This middle ware filter is for setting ConextInfo in our request.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Component
public class ContextSetterFilter extends OncePerRequestFilter {

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    /**
     * This method set contextInfo as Attribute in Request.
     *
     * @param request
     * @param response
     * @param chain
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String token = null;
        ContextInfo contextInfo = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            token = (requestTokenHeader.substring(7));
            try {
                OAuth2Authentication authority
                        = defaultTokenServices.loadAuthentication(token);
                WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetails(request);
                authority.setDetails(webAuthenticationDetails);
                contextInfo = ((ContextInfo) authority.getPrincipal());
                SecurityContextHolder.getContext().setAuthentication(authority);
            } catch (AuthenticationException | InvalidTokenException e) {
                SecurityContextHolder.getContext().setAuthentication(null);
                contextInfo = new ContextInfo();
            }
            contextInfo.setAccessToken(requestTokenHeader);
        } else {
            contextInfo = new ContextInfo();
        }

        contextInfo.setCurrentDate(Calendar.getInstance().getTime());

        request.setAttribute("contextInfo", contextInfo);
        chain.doFilter(request, response);
    }
}
