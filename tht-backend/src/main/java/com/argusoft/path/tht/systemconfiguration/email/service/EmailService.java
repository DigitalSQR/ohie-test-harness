package com.argusoft.path.tht.systemconfiguration.email.service;

import com.argusoft.path.tht.systemconfiguration.constant.MessageConstant;
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

    private final MessageService messageService;

    public EmailService(MessageService messageService) {
        this.messageService = messageService;
    }



    public void verifyEmailMessage(String to, String username, String link) {
        String subject = "Verify your email id";
        String templateFileName = "templates/verification-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, link);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    public void forgotPasswordMessage(String to, String username, String link) {
        String subject = "Reset your password";
        String templateFileName = "templates/reset-password-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, link);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    public void accountApprovedMessage(String to, String username) {
        String subject = "Account approval";
        String templateFileName = "templates/account-approval-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }

    }

    public void accountRejectedMessage(String to, String username) {
        String subject = "Account Rejection";
        String templateFileName = "templates/account-rejection-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }

    }

    public void accountInactiveMessage(String to, String username) {
        String subject = "Account Deactivated";
        String templateFileName = "templates/account-inactive-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }

    }
    public void accountActiveMessage(String to, String username) {
        String subject = "Account Re-activated";
        String templateFileName = "templates/account-active-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.APPROVED_MESSAGING_EXCEPTION_LOG, e);
        }

    }

    public void testRequestCreatedMessage(String to, String username, String currentEmail) {
        String subject = "Test Request Created";
        String templateFileName = "templates/test-request-created-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${currentEmail}", currentEmail);
            messageService.sendMessage(to, subject, htmlContent);
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

    public void verifiedAndWaitingForAdminApproval(String to, String username, String currentEmail) {
        String subject = "Account Created. Waiting for approval.";
        String templateFileName = "templates/account-created-waiting-for-approval.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${currentEmail}", currentEmail);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WAITING_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    public void testRequestAcceptedMessage(String to, String username, String testRequestName) {
        String subject = "Test Request Accepted";
        String templateFileName = "templates/test-request-accepted.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WAITING_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    public void testRequestRejectedMessage(String to, String username, String testRequestName) {
        String subject = "Test Request Rejected";
        String templateFileName = "templates/test-request-rejected.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WAITING_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    public void testRequestFinishedMessage(String to, String username, String testRequestName) {
        String subject = "Test Request Finished";
        String templateFileName = "templates/test-request-finished.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WAITING_MESSAGING_EXCEPTION_LOG, e);
        }
    }

    public void welcomeToTestingHarnessTool(String to, String username){
        String subject = "Welcome Message";
        String templateFileName = "templates/welcome-message.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            messageService.sendMessage(to, subject, htmlContent);
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WELCOME_IOEXCEPTION_LOG, e);
        } catch (MessagingException e) {
            LOGGER.error(MessageConstant.WELCOME_MESSAGING_EXCEPTION_LOG, e);
        }
    }
}
