package com.argusoft.path.tht.systemconfiguration.security.adapter;

import com.argusoft.path.tht.systemconfiguration.security.custom.CustomOauth2UserService;
import com.argusoft.path.tht.systemconfiguration.security.exceptionhandler.CustomAccessDeniedHandler;
import com.argusoft.path.tht.systemconfiguration.security.exceptionhandler.CustomAuthenticationEntryPoint;
import com.argusoft.path.tht.systemconfiguration.security.filters.CorsFilter;
import com.argusoft.path.tht.systemconfiguration.security.handler.OnSsoAuthenticationSuccessHandler;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.session.SessionManagementFilter;

/**
 * This ResourceServerConfig configures Authorisation for APIs and CORS, CSRF,
 * Authentication configurations.
 *
 * @author Dhruv
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    CorsFilter corsFilter;
    @Autowired
    public void setCorsFilter(CorsFilter corsFilter){
        this.corsFilter = corsFilter;
    }

    private DefaultTokenServices defaultTokenServices;
    @Autowired
    public void setDefaultTokenServices(DefaultTokenServices defaultTokenServices){
        this.defaultTokenServices = defaultTokenServices;
    }

    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    public void setCustomAccessDeniedHandler(CustomAccessDeniedHandler customAccessDeniedHandler){
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    private CustomOauth2UserService oauthUserService;
    @Autowired
    public void setCustomOauth2UserService(CustomOauth2UserService oauthUserService){
        this.oauthUserService = oauthUserService;
    }

    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Autowired
    public void setCustomAuthenticationEntryPoint(CustomAuthenticationEntryPoint customAuthenticationEntryPoint){
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    private UserRepository userRepository;
    @Autowired
    public void setUserRepository(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    private OnSsoAuthenticationSuccessHandler onSsoAuthenticationSuccessHandler;
    @Autowired
    public void setOnSsoAuthenticationSuccessHandler(OnSsoAuthenticationSuccessHandler onSsoAuthenticationSuccessHandler){
        this.onSsoAuthenticationSuccessHandler = onSsoAuthenticationSuccessHandler;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(corsFilter, SessionManagementFilter.class);
        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //TODO: Before release add all antMatchers
        http.authorizeRequests()
                .antMatchers("/socket/**").permitAll()
                .antMatchers("/user/register").permitAll()
                .antMatchers("/user/logout").permitAll()
                .antMatchers("/user/verify/**").permitAll()
                .antMatchers("/user/forgot/password/**").permitAll()
                .antMatchers("/user/update/password/**").permitAll()
                .antMatchers("/user/resend/verification/**").permitAll()
                .antMatchers("/swagger-ui**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/**").permitAll()
                .antMatchers("/v3/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/captcha/**").permitAll()
                .anyRequest().authenticated();

        http.headers().frameOptions().and()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                //                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(oauthUserService)
                .and()
                .successHandler(onSsoAuthenticationSuccessHandler);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(defaultTokenServices);
        resources.resourceId("resource_id");
    }

}
