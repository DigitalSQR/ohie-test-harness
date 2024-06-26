package com.argusoft.path.tht.systemconfiguration.security.repository;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class CustomCsrfTokenRepository implements CsrfTokenRepository {

    private static final String CSRF_TOKEN_ATTR_NAME = "XSRF-TOKEN";
    private static final String CSRF_TOKEN_ATTR_RET_NAME = "X-XSRF-TOKEN";


    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new DefaultCsrfToken(CSRF_TOKEN_ATTR_NAME, CSRF_TOKEN_ATTR_NAME, UUID.randomUUID().toString());
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String requestUri = request.getRequestURI();
        // Checking if the request is for the specific URL
        if (requestUri.equals("/api/user/principal")) {
            // Save the CSRF token in a cookie
            Cookie cookie = new Cookie(CSRF_TOKEN_ATTR_NAME, token.getToken());
            cookie.setPath("/");
            cookie.setMaxAge(36000);
            response.addCookie(cookie);
        }
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        // Always return null for GET requests
        if ("GET".equals(request.getMethod())) {
            return null;
        }
        // Otherwise, check if the CSRF token exists in the request header
        String tokenValue = request.getHeader(CSRF_TOKEN_ATTR_RET_NAME);
        return tokenValue != null ? new DefaultCsrfToken(CSRF_TOKEN_ATTR_RET_NAME, CSRF_TOKEN_ATTR_RET_NAME, tokenValue) : null;
    }

}
