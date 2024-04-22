package com.argusoft.path.tht.systemconfiguration.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;

@Service
public class AuthenticationService {

    private DefaultTokenServices defaultTokenServices;

    @Autowired
    public void setDefaultTokenServices(DefaultTokenServices defaultTokenServices) {
        this.defaultTokenServices = defaultTokenServices;
    }

    @CacheResult(cacheName = "authenticationCache")
    public OAuth2Authentication loadAuthentication(String token) {
        return defaultTokenServices.loadAuthentication(token);
    }

    @CacheRemove(cacheName = "authenticationCache")
    public Boolean revokeToken(String token) {
        return defaultTokenServices.revokeToken(token);
    }
}
