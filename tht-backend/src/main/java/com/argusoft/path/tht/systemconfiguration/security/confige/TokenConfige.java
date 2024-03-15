package com.argusoft.path.tht.systemconfiguration.security.confige;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * This configuration creates bean for the tokenStore and tokenService.
 *
 * @author Dhruv
 */
@Configuration
public class TokenConfige {

    private DataSource dataSource;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final int refreshTokenValidity;
    private final int accessTokenValidity;

    public TokenConfige(@Value("${tokenService.refresh-token-validity}") int refreshTokenValidity,
                       @Value("${tokenService.access-token-validity}") int accessTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
        this.accessTokenValidity = accessTokenValidity;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setRefreshTokenValiditySeconds(refreshTokenValidity);
        tokenServices.setAccessTokenValiditySeconds(accessTokenValidity);
        tokenServices.setTokenStore(this.tokenStore());
        return tokenServices;
    }
}
