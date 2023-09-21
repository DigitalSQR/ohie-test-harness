/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception;

/**
 * This exception mainly used when data are not found or exist.It Also contains
 * error message with HTTP status.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class DoesNotExistException extends Exception {

    public DoesNotExistException(String message) {
        super(message);
    }

    public DoesNotExistException(String message, Throwable cause) {
        super(message);
    }

}
