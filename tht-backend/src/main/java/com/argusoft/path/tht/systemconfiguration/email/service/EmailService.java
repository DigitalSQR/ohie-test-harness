package com.argusoft.path.tht.systemconfiguration.email.service;

import com.argusoft.path.tht.systemconfiguration.constant.MessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Email Service to do operations related to email.
 *
 * @author Hardik
 */
@Service
public class EmailService {

    public static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private JavaMailSender javaMailSender;

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendMessage(
            String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("noreply@baeldung.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // Set the second parameter to true for HTML content

        javaMailSender.send(message);
    }

    @Async
    public void verifyEmailMessage(String to, String username, String link) {
        String subject = "Verify your email id";
        String templateFileName = "templates/verification-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, link);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    @Async
    public void forgotPasswordMessage(String to, String username, String link) {
        String subject = "Reset your password";
        String templateFileName = "templates/reset-password-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, link);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    @Async
    public void accountApprovedMessage(String to, String username) {
        String subject = "Account approval";
        String templateFileName = "templates/account-approval-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }

    }

    @Async
    public void accountRejectedMessage(String to, String username) {
        String subject = "Account Rejection";
        String templateFileName = "templates/account-rejection-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }

    }

    @Async
    public void accountInactiveMessage(String to, String username) {
        String subject = "Account Deactivated";
        String templateFileName = "templates/account-inactive-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }

    }

    @Async
    public void testRequestCreatedMessage(String to, String username, String currentEmail) {
        String subject = "Test Request Created";
        String templateFileName = "templates/test-request-created-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${currentEmail}", currentEmail);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.TEST_REQUEST_CREATE_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.TEST_REQUEST_CREATE_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    private String readHtmlFile(String fileName, String username, String link) throws IOException {
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

    @Async
    public void verifiedAndWaitingForAdminApproval(String to, String username, String currentEmail) {
        String subject = "Account Created. Waiting for approval.";
        String templateFileName = "templates/account-created-waiting-for-approval.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${currentEmail}", currentEmail);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WAITING_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    @Async
    public void testRequestAcceptedMessage(String to, String username, String testRequestName) {
        String subject = "Test Request Accepted";
        String templateFileName = "templates/test-request-accepted.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WAITING_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    @Async
    public void testRequestRejectedMessage(String to, String username, String testRequestName) {
        String subject = "Test Request Rejected";
        String templateFileName = "templates/test-request-rejected.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WAITING_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    @Async
    public void testRequestFinishedMessage(String to, String username, String testRequestName) {
        String subject = "Test Request Finished";
        String templateFileName = "templates/test-request-finished.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WAITING_MESSAGING_EXCEPTION_LOG, e);
        }
    }

}
