package com.argusoft.path.tht.systemconfiguration.security.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

import javax.sql.DataSource;

/**
 * This OAuth2AuthorizationServerConfig configures tokenService in spring
 * security.
 *
 * @author Dhruv
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private DefaultTokenServices defaultTokenServices;
    @Autowired
    public void setDefaultTokenServices(DefaultTokenServices defaultTokenServices){
        this.defaultTokenServices = defaultTokenServices;
    }

    private DataSource dataSource;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    private AuthenticationManager authenticationManager;
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    private PasswordEncoder passwordEncoder;
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .jdbc(dataSource).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenServices(defaultTokenServices)
                .authenticationManager(authenticationManager);
    }

}
