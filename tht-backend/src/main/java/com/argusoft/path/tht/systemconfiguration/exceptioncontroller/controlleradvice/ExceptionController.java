package com.argusoft.path.tht.systemconfiguration.exceptioncontroller.controlleradvice;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class is manage occurred exceptions and its response error massages.
 *
 * @author Dhruv
 */
@RestControllerAdvice
public class ExceptionController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    private Environment environment;

    @Value("${exception.stack-trace.enabled}")
    private Boolean isStackTraceEnabled;


    @ExceptionHandler(value = OperationFailedException.class)
    public ResponseEntity<Object> handleException(OperationFailedException exception) {
        LOGGER.error(ValidateConstant.OPERATION_FAILED_EXCEPTION + ExceptionController.class.getSimpleName(), exception);
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DoesNotExistException.class)
    public ResponseEntity<Object> handleException(DoesNotExistException exception) {
        LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + ExceptionController.class.getSimpleName(), exception);
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = VersionMismatchException.class)
    public ResponseEntity<Object> handleException(VersionMismatchException exception) {
        LOGGER.error(ValidateConstant.VERSION_MISMATCHED_EXCEPTION + ExceptionController.class.getSimpleName(), exception);
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataValidationErrorException.class)
    public ResponseEntity<Object> handleException(DataValidationErrorException exception) {
        LOGGER.error(ValidateConstant.DATA_VALIDATION_EXCEPTION + ExceptionController.class.getSimpleName(), exception);
        return new ResponseEntity<>(exception.getValidationResults(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidParameterException.class)
    public ResponseEntity<Object> handleException(InvalidParameterException exception) {
        LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + ExceptionController.class.getSimpleName(), exception);
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PropertyReferenceException.class)
    public ResponseEntity<Object> handleException(PropertyReferenceException exception) {
        LOGGER.error(ValidateConstant.PROPERTY_REFERENCE_EXCEPTION + ExceptionController.class.getSimpleName(), exception);
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleException(ObjectOptimisticLockingFailureException exception) {
        LOGGER.error(ValidateConstant.OBJECT_OPTIMISTIC_LOCKING_FAILURE + ExceptionController.class.getSimpleName(), exception);
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException exception) {
        LOGGER.error(ValidateConstant.NULL_POINTER_EXCEPTION + ExceptionController.class.getSimpleName(), exception);
        return new ResponseEntity<>(calErrorResponse(exception), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleExceptionToJustPrintLog(Exception exception) throws Exception {
        LOGGER.error(ValidateConstant.EXCEPTION + ExceptionController.class.getSimpleName(), exception);
        throw exception;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        ValidationResultInfo error = new ValidationResultInfo();
        error.setMessage("File size exceeds the maximum limit of 2MB");
        error.setLevel(ErrorLevel.ERROR);
        error.setElement("file");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
