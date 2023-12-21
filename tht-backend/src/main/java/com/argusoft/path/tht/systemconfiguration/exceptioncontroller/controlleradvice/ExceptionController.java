/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.exceptioncontroller.controlleradvice;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class is manage occurred exceptions and its response error massages.
 *
 * @author Dhruv
 */
@RestControllerAdvice
public class ExceptionController {

    @Autowired
    private Environment environment;

    @Value("${exception.stack-trace.enabled}")
    private Boolean isStackTraceEnabled;


    @ExceptionHandler(value = OperationFailedException.class)
    public ResponseEntity<Object> handleException(OperationFailedException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DoesNotExistException.class)
    public ResponseEntity<Object> handleException(DoesNotExistException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = PermissionDeniedException.class)
    public ResponseEntity<Object> handleException(PermissionDeniedException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = MissingParameterException.class)
    public ResponseEntity<Object> handleException(MissingParameterException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = VersionMismatchException.class)
    public ResponseEntity<Object> handleException(VersionMismatchException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataValidationErrorException.class)
    public ResponseEntity<Object> handleException(DataValidationErrorException exception) {
        return new ResponseEntity<>(exception.getValidationResults(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidParameterException.class)
    public ResponseEntity<Object> handleException(InvalidParameterException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PropertyReferenceException.class)
    public ResponseEntity<Object> handleException(PropertyReferenceException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleException(ObjectOptimisticLockingFailureException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException exception) {
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ValidationResultInfo calErrorResponse(Exception exception) {
        ValidationResultInfo resultInfo = new ValidationResultInfo();
        resultInfo.setLevel(ErrorLevel.ERROR);
        resultInfo.setMessage(exception.getMessage());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        resultInfo.setStackTrace(isStackTraceEnabled ? sw.toString() : null);
        return resultInfo;
    }
}
