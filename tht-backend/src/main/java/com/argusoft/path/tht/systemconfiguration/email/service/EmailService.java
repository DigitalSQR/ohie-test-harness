package com.argusoft.path.tht.systemconfiguration.email.service;

import com.argusoft.path.tht.systemconfiguration.constant.MessageConstant;
import com.argusoft.path.tht.systemconfiguration.email.event.EmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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

    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void verifyEmailMessage(String to, String username, String link) {
        String subject = "Verify Your Email for Testing Harness Tool";
        String templateFileName = "templates/verification-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, link);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        }
    }

    public void forgotPasswordMessage(String to, String username, String link) {
        String subject = "Reset Your Password";
        String templateFileName = "templates/reset-password-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, link);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        }
    }

    public void accountApprovedMessage(String to, String username) {
        String subject = "Account Approved: Welcome to Testing Harness Tool (THT)!";
        String templateFileName = "templates/account-approval-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        }
    }

    public void accountRejectedMessage(String to, String username, String message) {
        String subject = "Registration Request Declined ";
        String templateFileName = "templates/account-rejection-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${message}", message);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        }
    }

    public void accountInactiveMessage(String to, String username, String message) {
        String subject = "Account Disabled";
        String templateFileName = "templates/account-inactive-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${message}", message);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        }
    }

    public void accountActiveMessage(String to, String username) {
        String subject = "Account Enabled ";
        String templateFileName = "templates/account-active-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.APPROVED_IOEXCEPTION_LOG, e);
        }
    }

    public void testRequestCreatedMessage(String to, String username, String currentEmail) {
        String subject = "Test Request Created";
        String templateFileName = "templates/test-request-created-email.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${currentEmail}", currentEmail);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.TEST_REQUEST_CREATE_IOEXCEPTION_LOG, e);
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
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        }
    }

    public void testRequestAcceptedMessage(String to, String username, String testRequestName) {
        String subject = "Application Testing Request Accepted";
        String templateFileName = "templates/test-request-accepted.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        }
    }

    public void testRequestRejectedMessage(String to, String username, String testRequestName, String message) {
        String subject = "Application Testing Request Accepted";
        String templateFileName = "templates/test-request-rejected.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            htmlContent = htmlContent.replace("${message}", message);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        }
    }

    public void testRequestFinishedMessage(String to, String username, String testRequestName, String reportLink) {
        String subject = "Application Testing Request Completed";
        String templateFileName = "templates/test-request-finished.html";
        String htmlContent = null;
        try {
            htmlContent = readHtmlFile(templateFileName, username, null);
            htmlContent = htmlContent.replace("${name}", testRequestName);
            htmlContent = htmlContent.replace("${link}", reportLink);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        }
    }
    public void adminOrTesterAccountCreatedMessage(String to, String username){
        String subject = "Admin/Tester Creation Completed";
        String templateFileName = "templates/admin-tester-created.html";
        String htmlContent = null;
        try{
            htmlContent = readHtmlFile(templateFileName, username, null);
            applicationEventPublisher.publishEvent(new EmailEvent(to, subject, htmlContent));
        } catch (IOException e) {
            LOGGER.error(MessageConstant.WAITING_IOEXCEPTION_LOG, e);
        }
    }

}
