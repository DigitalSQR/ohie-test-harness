package com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception;

/**
 * This exception mainly used when data are invalid. It Also contains
 * error message with HTTP status.
 *
 * @author Hardik
 */

public class InvalidFileTypeException extends RuntimeException {

    public InvalidFileTypeException(String message) {
        super(message);
    }
}
