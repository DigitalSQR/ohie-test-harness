package com.argusoft.path.tht.systemconfiguration.audit.config;

import com.argusoft.path.tht.systemconfiguration.audit.constant.UserAccessAuditConstant;
import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditInfo;
import com.argusoft.path.tht.systemconfiguration.audit.service.UserAccessAuditServiceImpl;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.service.impl.ComponentServiceServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;
import java.util.Date;

@Component
public class CustomApplicationEventListener {

    public static final String LOGON_SUCCESS_EVENT = "LOGON_SUCCESS";
    public static final String LOGOFF_SUCCESS_EVENT = "LOGOFF_SUCCESS";
    public static final String PERMISSION_DENIED_EVENT = "PERMISSION_DENIED";
    public static final String OPERATION_FAILED_EVENT = "OPERATION_FAILED";
    public static final String AUTHENTICATION_FAILURE_EVENT = "AUTHENTICATION_FAILURE";
    public static final String AUTHENTICATION_SUCCESS_EVENT = "AUTHENTICATION_SUCCESS";
    public static final String AUTHORIZATION_FAILURE_EVENT = "AUTHORIZATION_FAILURE";
    public static final String DETAILS = "details";
    public static final String BRACKET = " : {} ";
    @Autowired
    UserAccessAuditServiceImpl userAccessAuditService;

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentServiceServiceImpl.class);

    @EventListener
    public void auditEventHappened(AuditApplicationEvent auditApplicationEvent)
            throws InvalidParameterException,
            DataValidationErrorException,
            OperationFailedException{
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = null;
        if (servletRequestAttributes != null) {
            request = servletRequestAttributes.getRequest();
        }
        ContextInfo contextInfo = new ContextInfo();
        UserAccessAuditInfo userAccessAuditInfo = new UserAccessAuditInfo();
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();

        contextInfo.setEmail(auditEvent.getPrincipal());

        userAccessAuditInfo.setUsername(auditEvent.getPrincipal());
        userAccessAuditInfo.setTimestamp(Date.from(auditEvent.getTimestamp()));
        userAccessAuditInfo.setAccessUrl((String) auditEvent.getData().get("requestUrl"));
        userAccessAuditInfo.setMessage((String) auditEvent.getData().get("message"));

        if (StringUtils.isEmpty(userAccessAuditInfo.getAccessUrl()) && request != null) {
            userAccessAuditInfo.setAccessUrl(request.getRequestURI());
        }

        this.setIpAddressToUserAccessAuditInfo(userAccessAuditInfo, request, auditEvent);

        switch (auditEvent.getType()) {
            case LOGON_SUCCESS_EVENT:
                userAccessAuditInfo.setTypeId(UserAccessAuditConstant.USER_ACCESS_AUDIT_LOGON_TYPE);
                userAccessAuditInfo.setStateId(UserAccessAuditConstant.USER_ACCESS_AUDIT_SUCCESS_STATE);
                LOGGER.info(LOGON_SUCCESS_EVENT + BRACKET, userAccessAuditInfo);
                break;
            case LOGOFF_SUCCESS_EVENT:
                userAccessAuditInfo.setTypeId(UserAccessAuditConstant.USER_ACCESS_AUDIT_LOGOFF_TYPE);
                userAccessAuditInfo.setStateId(UserAccessAuditConstant.USER_ACCESS_AUDIT_SUCCESS_STATE);
                LOGGER.info(LOGOFF_SUCCESS_EVENT + BRACKET, userAccessAuditInfo);
                break;
            case AUTHORIZATION_FAILURE_EVENT:
                if (StringUtils.isEmpty(userAccessAuditInfo.getAccessUrl())) {
                    return;
                }
                userAccessAuditInfo.setTypeId(UserAccessAuditConstant.USER_ACCESS_AUDIT_ACCESS_DENIED_TYPE);
                userAccessAuditInfo.setStateId(UserAccessAuditConstant.USER_ACCESS_AUDIT_EXCEPTION_STATE);
                LOGGER.info(AUTHENTICATION_FAILURE_EVENT + BRACKET, userAccessAuditInfo);
                break;
            case PERMISSION_DENIED_EVENT:
                userAccessAuditInfo.setTypeId(UserAccessAuditConstant.USER_ACCESS_AUDIT_PERMISSION_DENIED_TYPE);
                userAccessAuditInfo.setStateId(UserAccessAuditConstant.USER_ACCESS_AUDIT_EXCEPTION_STATE);
                LOGGER.info(PERMISSION_DENIED_EVENT + BRACKET, userAccessAuditInfo);
                break;
            case OPERATION_FAILED_EVENT:
                userAccessAuditInfo.setTypeId(UserAccessAuditConstant.USER_ACCESS_AUDIT_OPERATION_FAILED_TYPE);
                userAccessAuditInfo.setStateId(UserAccessAuditConstant.USER_ACCESS_AUDIT_EXCEPTION_STATE);
                LOGGER.info(OPERATION_FAILED_EVENT + BRACKET, userAccessAuditInfo);
                break;
            default:
                return;
        }

        userAccessAuditService.createUserAccessAudit(userAccessAuditInfo, contextInfo);
    }

    private void setIpAddressToUserAccessAuditInfo(
            UserAccessAuditInfo userAccessAuditInfo,
            HttpServletRequest request,
            AuditEvent auditEvent) {
        if (auditEvent.getData().get(DETAILS) instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails details =
                    (OAuth2AuthenticationDetails) auditEvent.getData().get(DETAILS);
            if (details != null) {
                userAccessAuditInfo.setIpAddress(details.getRemoteAddress());
            }
        } else if (auditEvent.getData().get(DETAILS) instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails details =
                    (WebAuthenticationDetails) auditEvent.getData().get(DETAILS);
            if (details != null) {
                userAccessAuditInfo.setIpAddress(details.getRemoteAddress());
            }
        }

        if (StringUtils.isEmpty(userAccessAuditInfo.getIpAddress())) {
            if (!StringUtils.isEmpty(auditEvent.getData().get("remoteAddress"))) {
                userAccessAuditInfo.setIpAddress((String) auditEvent.getData().get("remoteAddress"));
            } else if (request != null) {
                userAccessAuditInfo.setIpAddress(request.getRemoteAddr());
            }
        }
    }


}
