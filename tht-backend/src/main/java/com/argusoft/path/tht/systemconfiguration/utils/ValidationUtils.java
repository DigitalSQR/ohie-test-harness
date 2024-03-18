package com.argusoft.path.tht.systemconfiguration.utils;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This ValidationUtils provides methods for validation.
 *
 * @author Dhruv
 */
public final class ValidationUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);

    public static final String PROVIDED_KEY = "Provided key:";

    private ValidationUtils() {
    }

    /**
     * Validation method for Not Empty.
     *
     * @param field     contains field values
     * @param fieldName name of the field
     * @param errors
     */
    public static void validateNotEmpty(
            String field,
            String fieldName,
            List<ValidationResultInfo> errors) {
        if (field != null && field.isEmpty()) {
            errors
                    .add(new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            fieldName + " field must not be empty"));
        }
    }

    /**
     * Validation method for Required field.
     *
     * @param field     contains field values
     * @param fieldName name of the field
     * @param error
     */
    public static void validateRequired(
            Object field,
            String fieldName,
            List<ValidationResultInfo> error) {
        if (field == null) {
            error.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + ValidateConstant.MUST_PROVIDED));
        }
    }

    /**
     * Validation method for not updatable field.
     *
     * @param field         contains field values
     * @param originalField contains original fields values
     * @param fieldName     name of the field
     * @param errors
     */
    public static void validateNotUpdatable(
            Object field,
            Object originalField,
            String fieldName,
            List<ValidationResultInfo> errors) {
        if ((field != null || originalField != null)
                && (originalField == null
                || field == null
                || !field.equals(originalField))) {
            errors
                    .add(new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            fieldName + " field is not updatable"));
        }
    }

    /**
     * Validation method for list size.
     *
     * @param field     contains field values
     * @param fieldName name of the field
     * @param minValue
     * @param maxValue
     * @param errors
     */
    public static void validateCollectionSize(
            Collection<?> field,
            String fieldName,
            Integer minValue,
            Integer maxValue,
            List<ValidationResultInfo> errors) {
        if (field != null) {
            if (minValue != null
                    && maxValue != null
                    && minValue.equals(maxValue)
                    && !minValue.equals(field.size())) {
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " must contain "
                                        + minValue + " item"));
            } else {
                if (minValue != null && field.size() < minValue) {
                    errors
                            .add(new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    fieldName + " must contain more than "
                                            + minValue + " items"));
                } else if (maxValue != null && field.size() > maxValue) {
                    errors
                            .add(new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    fieldName + " must contain less than "
                                            + maxValue + " items"));
                }
            }
        }
    }

    /**
     * Validation method for Integer.
     *
     * @param field     contains field values
     * @param fieldName name of the field
     * @param minValue
     * @param maxValue
     * @param errors
     */
    public static void validateIntegerRange(
            Integer field,
            String fieldName,
            Integer minValue,
            Integer maxValue,
            List<ValidationResultInfo> errors) {
        if (field != null) {
            if (minValue != null && field < minValue) {
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " field must be greater than equal to " + minValue));
            } else if (maxValue != null && field > maxValue) {
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " field must be less than equal to " + maxValue));
            }
        }
    }

    /**
     * Validation method for pattern match.
     *
     * @param fieldValue    contains field values
     * @param fieldName     name of the field
     * @param patternString pattern for the field
     * @param errorMassage
     * @param errors
     */
    public static void validatePattern(String fieldValue,
                                       String fieldName,
                                       String patternString,
                                       String errorMassage,
                                       List<ValidationResultInfo> errors) {
        Pattern pattern = Pattern.compile(patternString);
        if (fieldValue != null && !pattern.matcher(fieldValue).matches()) {
            errors
                    .add(new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            errorMassage));
        }
    }

    /**
     * Validation method for Length.
     *
     * @param fieldValue contains field values
     * @param fieldName  name of the field
     * @param minLength  minimum required length for field
     * @param maxLength  maximum required length for field
     * @param errors
     */
    public static void validateLength(String fieldValue,
                                      String fieldName,
                                      int minLength,
                                      int maxLength,
                                      List<ValidationResultInfo> errors) {
        if (fieldValue != null) {
            int fieldLength = fieldValue.length();
            if (minLength > fieldLength) {
                errors.add(new ValidationResultInfo(fieldName,
                        ErrorLevel.ERROR,
                        fieldName + " must have more than "
                                + minLength + " characters"));
            } else if (fieldLength > maxLength) {
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " must have less than "
                                        + maxLength + " characters"));
            }
        }
    }

    public static void throwDataValidationOnValidationErrors(
            List<ValidationResultInfo> validationResultInfos
    ) throws DataValidationErrorException {
        if (ValidationUtils.containsErrors(validationResultInfos, ErrorLevel.ERROR)) {
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, ValidationUtils.class.getSimpleName());
            throw new DataValidationErrorException("Error(s) occurred validating", validationResultInfos);
        }
    }

    /**
     * Check if errors contains WARN thresh hold Error.
     *
     * @param errors     list of errors
     * @param errorLevel
     * @return true if it contains WARN thresh hold Error.
     */
    public static boolean containsErrors(
            List<ValidationResultInfo> errors,
            ErrorLevel errorLevel) {
        return hasValidationErrors(errors,
                errorLevel,
                new ArrayList<>());
    }

    public static boolean hasValidationErrors(
            List<ValidationResultInfo> validationResults,
            ErrorLevel threshold, List<String> ignoreFields) {
        if (validationResults == null) {
            return false;
        }
        return validationResults.stream().anyMatch(validationResult
                -> //Ignore any fields that are in the list
                ((ignoreFields == null
                        || (!ignoreFields.contains(validationResult.getElement())))
                        && ValidationResultInfo.isSurpassingThreshold(validationResult.getLevel(),
                        threshold))
        );
    }

    public static boolean arePairsNullConsistent(Object... variables) {
        boolean hasNonNull = false;
        boolean hasNull = false;

        for (Object variableValue : variables) {
            if (variableValue != null) {
                hasNonNull = true;
            } else {
                hasNull = true;
            }

            if (hasNonNull && hasNull) {
                return true;
            }
        }

        return false;
    }

    // check if particular status present for a given service
    public static void statusPresent(List<String> serviceList, String stateKey, List<ValidationResultInfo> errors) {

        if (!serviceList.contains(stateKey)) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("state");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("provided state is not valid ");
            errors.add(validationResultInfo);
        }
    }

    //check if transition from one state to other is possible
    public static void transitionValid(Multimap<String, String> serviceMap, String currentState, String nextState, List<ValidationResultInfo> errors) {
        if (!serviceMap.containsKey(currentState) || !serviceMap.get(currentState).contains(nextState)) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("state");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("provided transition is not valid [Current state : " + currentState + " , Next state : " + nextState + " ]");
            errors.add(validationResultInfo);
        }
    }

}
