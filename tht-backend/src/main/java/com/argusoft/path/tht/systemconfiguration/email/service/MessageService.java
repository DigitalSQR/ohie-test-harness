package com.argusoft.path.tht.systemconfiguration.email.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MessageService {


    public static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private JavaMailSender javaMailSender;


    private final String emailSender;

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public MessageService( @Value("${mailSender.username}") String emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendMessage(
            String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(emailSender);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // Set the second parameter to true for HTML content

        javaMailSender.send(message);
    }
}
