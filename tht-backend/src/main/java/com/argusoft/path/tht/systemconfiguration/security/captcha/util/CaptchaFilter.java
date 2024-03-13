package com.argusoft.path.tht.systemconfiguration.security.captcha.util;


import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.captcha.service.CaptchaService;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
public class CaptchaFilter implements Filter {

    public static final Logger LOGGER = LoggerFactory.getLogger(CaptchaFilter.class);

    @Autowired
    private CaptchaService captchaService;

    @Value("${captcha}")
    private boolean isCaptchaRequired;

    List<String> captchaProtectedUrls = List.of("/api/user/register",
                                                "/api/oauth/token");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                                                        FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestUrl = request.getServletContext().getContextPath() + request.getServletPath();

        if(isCaptchaRequired && (captchaProtectedUrls.contains(requestUrl))) {
                // Captcha validation is required for this URL
                String captchaCode = request.getHeader("captchaCode");
                String captcha = request.getHeader("captcha");

                List<ValidationResultInfo> errors = new ArrayList<>();

                if(captchaCode == null && captcha!=null){
                    ValidationResultInfo error = new ValidationResultInfo();
                    error.setMessage("Invalid captcha");
                    error.setLevel(ErrorLevel.ERROR);
                    error.setElement("captchaCode");
                    errors.add(error);
                }

                if(captcha == null){
                    ValidationResultInfo error = new ValidationResultInfo();
                    error.setMessage("Something went wrong while fetching captcha");
                    error.setLevel(ErrorLevel.ERROR);
                    error.setElement("captcha");
                    errors.add(error);
                }

                if(ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)){
                    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                    httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    httpResponse.setContentType("application/json");
                    ObjectMapper objectMapper = new ObjectMapper();
                    httpResponse.getWriter().write(objectMapper.writeValueAsString(errors));
                    return;
                }


                try {
                    errors = captchaService.validateCaptcha(captchaCode, captcha, (ContextInfo) request.getAttribute("contextInfo"));
                } catch (Exception e) {
                    LOGGER.error("caught exception in CaptchaFilter ",e);
                    return;
                }

                if(ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)){
                    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                    httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    httpResponse.setContentType("application/json");
                    ObjectMapper objectMapper = new ObjectMapper();
                    httpResponse.getWriter().write(objectMapper.writeValueAsString(errors));
                    return;
                }

        }
        filterChain.doFilter(request, servletResponse);
    }
}
