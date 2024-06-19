package com.argusoft.path.tht.systemconfiguration.security.filters;

import com.argusoft.path.tht.systemconfiguration.constant.Module;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.security.service.AuthenticationService;
import com.argusoft.path.tht.usermanagement.models.entity.UserActivityEntity;
import com.argusoft.path.tht.usermanagement.service.UserActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This middle ware filter is for setting ConextInfo in our request.
 *
 * @author Dhruv
 */
@Component
public class ContextSetterFilter extends OncePerRequestFilter {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContextSetterFilter.class);

    private AuthenticationService authenticationService;
    private UserActivityService userActivityService;

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void setUserActivityService(UserActivityService userActivityService){
        this.userActivityService=userActivityService;
    }

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

        String userId = null;
        String apiUrl = request.getRequestURI();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date accessTime = Calendar.getInstance().getTime();
        String scope = request.getMethod();

        ContextInfo contextInfo = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            token = (requestTokenHeader.substring(7));
            try {
                OAuth2Authentication authority = authenticationService.loadAuthentication(token);
                WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetails(request);
                authority.setDetails(webAuthenticationDetails);
                contextInfo = ((ContextInfo) authority.getPrincipal());
                SecurityContextHolder.getContext().setAuthentication(authority);
                userId = contextInfo.getUsername();
            } catch (AuthenticationException | InvalidTokenException e) {
                LOGGER.error(ValidateConstant.EXCEPTION + ContextSetterFilter.class.getSimpleName(), e);
                SecurityContextHolder.getContext().setAuthentication(null);
                contextInfo = new ContextInfo();
            }
            contextInfo.setAccessToken(requestTokenHeader);
        } else {
            contextInfo = new ContextInfo();
        }

        contextInfo.setModule(Module.UI);
        contextInfo.setCurrentDate(Calendar.getInstance().getTime());
        request.setAttribute("contextInfo", contextInfo);
        chain.doFilter(request, response);

        UserActivityEntity userActivityEntity=new UserActivityEntity();
        userActivityEntity.setAccessTime(accessTime);
        userActivityEntity.setUserId(userId);
        userActivityEntity.setScope(scope);
        userActivityEntity.setApiUrl(apiUrl);

        userActivityService.createUserActivity(userActivityEntity, contextInfo);
    }
}
