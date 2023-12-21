/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception;

/**
 * This exception mainly used when user don't have permission for particular operation.
 * It Also contains error message with HTTP status.
 *
 * @author Dhruv
 */
public class PermissionDeniedException extends Exception {

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

}
