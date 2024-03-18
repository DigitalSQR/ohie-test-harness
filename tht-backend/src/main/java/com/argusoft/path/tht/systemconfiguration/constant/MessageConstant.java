package com.argusoft.path.tht.systemconfiguration.constant;

/**
 * exception messages
 *
 * @author Bhavi
 */
public final class MessageConstant {

    public static final String APPROVED_IOEXCEPTION_LOG = "Error sending Account Approved Message because of IOException";
    public static final String APPROVED_MESSAGING_EXCEPTION_LOG = "Error sending Account Approved Message because of MessagingException";
    public static final String WAITING_IOEXCEPTION_LOG = "Error sending Waiting For Approval Message because of IOException";
    public static final String WAITING_MESSAGING_EXCEPTION_LOG = "Error sending Waiting For Approval Message because of MessagingException";
    public static final String TEST_REQUEST_CREATE_IOEXCEPTION_LOG = "Error sending Test Request Created Message because of IOException";
    public static final String TEST_REQUEST_CREATE_MESSAGING_EXCEPTION_LOG = "Error sending Test Request Created Message because of MessagingException";
    public static final String WELCOME_IOEXCEPTION_LOG = "Error sending Welcome Message because of IOException";
    public static final String WELCOME_MESSAGING_EXCEPTION_LOG = "Error sending Welcome Message because of MessagingException";

    private MessageConstant() {
        throw new IllegalStateException("Utility class");
    }


}
