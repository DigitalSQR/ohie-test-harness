package com.argusoft.path.tht.systemconfiguration.security.confige;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This configuration creates bean for the restTemplate.
 *
 * @author Dhruv
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
