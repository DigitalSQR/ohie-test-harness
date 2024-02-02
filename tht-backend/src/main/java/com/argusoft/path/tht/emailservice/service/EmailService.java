package com.argusoft.path.tht.emailservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * Email Service to do operations related to email.
 *
 * @author Hardik
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    @Async
    public void sendMessage(
            String to, String subject,String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("noreply@baeldung.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // Set the second parameter to true for HTML content

        javaMailSender.send(message);
    }

    public void verifyEmailMessage(String to,String username,String link) throws MessagingException, IOException {
        String subject="Verify your email id";
        String templateFileName="templates/verification-email.html";
        String htmlContent = readHtmlFile(templateFileName,username,link);
        sendMessage(to,subject,htmlContent);
    }

    public void forgotPasswordMessage(String to,String username,String link) throws MessagingException, IOException {
        String subject="Reset your password";
        String templateFileName="templates/reset-password-email.html";
        String htmlContent = readHtmlFile(templateFileName,username,link);
        sendMessage(to,subject,htmlContent);

    }

    public void accountApprovedMessage(String to,String username) throws MessagingException, IOException {
        String subject="Account approval";
        String templateFileName="templates/account-approval-email.html";
        String htmlContent = readHtmlFile(templateFileName,username,null);
        sendMessage(to,subject,htmlContent);

    }
    private String readHtmlFile(String fileName,String username,String link) throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (stream == null) {
                throw new IOException("File not found: " + fileName);
            }
            byte[] binaryData = stream.readAllBytes();
            String htmlContent = new String(binaryData, StandardCharsets.UTF_8);
            htmlContent = htmlContent.replace("${username}", username);

            if (link != null) {
                htmlContent = htmlContent.replace("${link}", link);
            }

            return htmlContent;
        }
    }



}
