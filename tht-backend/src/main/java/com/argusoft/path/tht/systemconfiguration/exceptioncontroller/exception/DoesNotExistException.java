package com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception;

/**
 * This exception mainly used when data are not found or exist. It Also contains
 * error message with HTTP status.
 *
 * @author Dhruv
 */
public class DoesNotExistException extends Exception {

    public DoesNotExistException(String message) {
        super(message);
    }

    public DoesNotExistException(String message, Throwable cause) {
        super(message);
    }

}
