package com.argusoft.path.tht.systemconfiguration.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Email Service configuration.
 *
 * @author dhruv
 */
@Configuration
public class EmailConfig {

    private final String username;
    private final String password;
    private final String host;
    private final int port;

    public EmailConfig(@Value("${mailSender.username}") String username,
                       @Value("${mailSender.password}") String password,
                       @Value("${mailSender.host}") String host,
                       @Value("${mailSender.port}") int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
