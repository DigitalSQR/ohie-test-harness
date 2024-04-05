package com.argusoft.path.tht.systemconfiguration.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private DefaultTokenServices defaultTokenServices;

    @Autowired
    public void setDefaultTokenServices(DefaultTokenServices defaultTokenServices) {
        this.defaultTokenServices = defaultTokenServices;
    }

    @Cacheable(value = "authenticationCache", key = "#token")
    public OAuth2Authentication loadAuthentication(String token) {
        return defaultTokenServices.loadAuthentication(token);
    }
}