/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.models.dto;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import io.swagger.v3.oas.annotations.Parameter;

import java.io.Serializable;

/**
 * Information about the results of a data validation.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class ValidationResultInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Parameter(name = "element",
            description = "The name of the element that has error",
            example = "elementName")
    private String element;

    @Parameter(name = "level",
            description = "Level of the error",
            example = "ERROR/WARN/OK")
    private ErrorLevel level;

    @Parameter(name = "message",
            description = "Error message for the error",
            example = "message")
    private String message;

    @Parameter(name = "stackTrace",
            description = "stackTrace for the error",
            example = "exception path")
    private String stackTrace;

    public ValidationResultInfo() {
        this.level = ErrorLevel.OK;
    }

    public ValidationResultInfo(String element) {
        this();
        this.element = element;
    }

    public ValidationResultInfo(String element, ErrorLevel level, String message) {
        this.element = element;
        this.level = level;
        this.message = message;
    }

    public static boolean isSurpassingThreshold(ErrorLevel currentLevel,
                                                ErrorLevel threshold) {
        return currentLevel.compareTo(threshold) >= 0;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public ErrorLevel getLevel() {
        return this.level;
    }

    public void setLevel(ErrorLevel level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ValidationResultInfo{" + "element=" + element + ", level=" + level + ", message=" + message + '}';
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

}
