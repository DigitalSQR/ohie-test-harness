package com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception;

/**
 * This exception mainly used when updating old data that has been already updated.
 * It Also contains error message with HTTP status.
 *
 * @author dhruv
 */
public class VersionMismatchException extends Exception {

    public VersionMismatchException(String message) {
        super(message);
    }

    public VersionMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

}
