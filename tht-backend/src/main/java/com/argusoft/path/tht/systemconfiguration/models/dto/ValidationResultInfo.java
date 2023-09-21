/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.models.dto;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

/**
 * Information about the results of a data validation.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public class ValidationResultInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "The name of the element that has error",
            example = "elementName",
            readOnly = true)
    private String element;

    @ApiModelProperty(notes = "Level of the error",
            example = "ERROR/WARN/OK",
            readOnly = true)
    private ErrorLevel level;

    @ApiModelProperty(notes = "Error message for the error",
            example = "message",
            readOnly = true)
    private String message;

    @ApiModelProperty(notes = "stackTrace for the error",
            example = "exception path",
            readOnly = true)
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

    public static boolean isSurpassingThreshold(ErrorLevel currentLevel,
            ErrorLevel threshold) {
        return currentLevel.compareTo(threshold) >= 0;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

}
