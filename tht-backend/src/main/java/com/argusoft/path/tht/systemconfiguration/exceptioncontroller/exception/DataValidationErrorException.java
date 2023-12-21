/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception;

import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This exception mainly used when validation in model are not satisfied.
 * It Also contains error message with HTTP status.
 *
 * @author Dhruv
 */
public class DataValidationErrorException extends Exception {

    private static final long serialVersionUID = 1L;

    private final List<ValidationResultInfo> validationResults;

    public DataValidationErrorException() {
        this.validationResults = new ArrayList<>();
    }

    public DataValidationErrorException(String message, List<ValidationResultInfo> validationResults) {
        super(message);
        this.validationResults = validationResults;
    }

    public DataValidationErrorException(String message, List<ValidationResultInfo> validationResults, Throwable cause) {
        super(message, cause);
        this.validationResults = validationResults;
    }

    public DataValidationErrorException(String message, Throwable cause) {
        super(message, cause);
        this.validationResults = new ArrayList<>();
    }

    public DataValidationErrorException(String message) {
        super(message);
        this.validationResults = new ArrayList<>();
    }

    public DataValidationErrorException(Throwable cause) {
        super(cause);
        this.validationResults = new ArrayList<>();
    }

    public List<ValidationResultInfo> getValidationResults() {
        return validationResults;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage()).append("\n");

        if (!validationResults.isEmpty()) {
            sb.append("Validation Results: \n");
            validationResults.stream()
                    .forEach(info -> sb.append(info).append("\n"));
        } else {
            sb.append("Validation Results: None set.");
        }
        return sb.toString();
    }
}
