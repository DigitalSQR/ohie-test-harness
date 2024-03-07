package com.argusoft.path.tht.systemconfiguration.auditor.confige;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Auditor-config to configure AuditorAwareImpl.
 *
 * @author Dhruv
 */
@Configuration
@EnableJpaAuditing
public class AuditorConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
