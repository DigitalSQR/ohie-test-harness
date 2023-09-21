/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testing.harness.tool.systemconfiguration.exceptioncontroller.exception;

/**
 * This exception mainly used when repository operations are failed.It Also
 * contains error message with HTTP status.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class OperationFailedException extends Exception {

    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
