/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.utils;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.KeyCategory;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
                    fieldName + " must be provided"));
        }
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
     * Validation method for RefObjectUri.
     *
     * @param refObjectUri contains refObjectUri values
     * @param fieldName    name of the field
     * @param infoName     name of the info
     * @param errors
     */
    public static void validateRefObjectUri(
            String refObjectUri,
            String fieldName,
            String infoName,
            List<ValidationResultInfo> errors) {
        if (StringUtils.isEmpty(refObjectUri) || !refObjectUri.equals(infoName)) {
            errors
                    .add(new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            "Provided " + fieldName
                                    + " must be for " + infoName));
        }
    }

    /**
     * Validation method for RefObjectUri.
     *
     * @param keyCatagory KeyCatagory values
     * @param fieldName   name of the field
     * @param key         key of the info
     * @param minValue
     * @param maxValue
     * @param value       value of the info
     * @param errors
     */
    public static void validateKeyValue(
            KeyCategory keyCatagory,
            String key,
            String value,
            BigDecimal minValue,
            BigDecimal maxValue,
            String fieldName,
            List<ValidationResultInfo> errors) {
        switch (keyCatagory) {
            case BIGDECIMAL: {
                try {
                    Float f = Float.parseFloat(value);
                    validateFloatRange(
                            f,
                            fieldName,
                            minValue == null ? null : minValue.floatValue(),
                            maxValue == null ? null : maxValue.floatValue(),
                            errors);
                } catch (NumberFormatException e) {
                    LOGGER.error("caught NumberFormatException in ValidationUtils ", e);
                    errors
                            .add(new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    PROVIDED_KEY + key
                                            + "'s value must be for BigDecimal"));
                }

                break;
            }
            case PERCENTAGE: {
                try {
                    Float f = Float.parseFloat(value);
                    validateFloatRange(
                            f,
                            fieldName,
                            minValue == null ? null : minValue.floatValue(),
                            maxValue == null ? null : maxValue.floatValue(),
                            errors);
                } catch (NumberFormatException e) {
                    LOGGER.error("caught NumberFormatException in ValidationUtils ", e);
                    errors
                            .add(new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    PROVIDED_KEY + key
                                            + "'s value must be for Percentage"));
                }

                break;
            }
            case BOOLEAN: {
                if (StringUtils.isEmpty(value)
                        || (!value.toLowerCase().equals(Constant.TRUE_STRING)
                        && !value.toLowerCase().equals(Constant.FALSE_STRING))) {
                    errors
                            .add(new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    PROVIDED_KEY + key
                                            + "'s value must be for BOOLEAN"));
                }
                break;
            }
            case INTEGER: {
                try {
                    Integer i = Integer.parseInt(value);
                    validateIntegerRange(
                            i,
                            fieldName,
                            minValue == null ? null : minValue.intValue(),
                            maxValue == null ? null : maxValue.intValue(),
                            errors);
                } catch (NumberFormatException e) {
                    LOGGER.error("caught NumberFormatException in ValidationUtils ", e);
                    errors
                            .add(new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    PROVIDED_KEY + key
                                            + "'s value must be for INTEGER"));
                }
                break;
            }
            case STRING: {
                if (StringUtils.isEmpty(value)) {
                    errors
                            .add(new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    PROVIDED_KEY + key
                                            + "'s value must be for STRING"));
                }
                break;
            }
            case DATE: {
                DateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMATE_STRING);
                sdf.setLenient(false);
                try {
                    sdf.parse(value);
                } catch (ParseException e) {
                    LOGGER.error("caught ParseException in ValidationUtils ", e);
                    errors
                            .add(new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    PROVIDED_KEY + key
                                            + "'s value must be for DATE with format: " + Constant.DATE_FORMATE_STRING
                            ));
                }
                break;
            }
        }
    }

    /**
     * Validation method for Float.
     *
     * @param field     contains field values
     * @param fieldName name of the field
     * @param minValue
     * @param maxValue
     * @param errors
     */
    public static void validateFloat(
            String field,
            String fieldName,
            Float minValue,
            Float maxValue,
            List<ValidationResultInfo> errors) {
        if (field != null) {
            try {
                Float floatValue = Float.parseFloat(field);
                validateFloatRange(floatValue,
                        fieldName,
                        minValue,
                        maxValue,
                        errors);
            } catch (NumberFormatException e) {
                LOGGER.error("caught NumberFormatException in ValidationUtils ", e);
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " field must contain decimal value"));
            }
        }
    }

    /**
     * Validation method for Float.
     *
     * @param field     contains field values
     * @param fieldName name of the field
     * @param minValue
     * @param maxValue
     * @param errors
     */
    public static void validateFloatRange(
            Float field,
            String fieldName,
            Float minValue,
            Float maxValue,
            List<ValidationResultInfo> errors) {
        if (field != null) {
            if (minValue != null && field < minValue) {
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " field must be greater than " + minValue));
            } else if (maxValue != null && field > maxValue) {
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " field must be less than " + maxValue));
            }
        }
    }

    /**
     * Validation method for effectiveDates.
     *
     * @param effectiveDate  contains effectiveDate
     * @param expirationDate contain expirationDate
     * @param error
     */
    public static void validateEffectiveDates(
            Date effectiveDate,
            Date expirationDate,
            List<ValidationResultInfo> error) {
        if (effectiveDate != null
                && expirationDate != null
                && effectiveDate.after(expirationDate)) {
            error
                    .add(new ValidationResultInfo("effectiveDates",
                            ErrorLevel.ERROR,
                            "expirationDate must come after effectiveDate"));
        }
    }

    /**
     * Validation method for effectiveDates.
     *
     * @param effectiveDate  contains effectiveDate
     * @param expirationDate contain expirationDate
     * @param error
     */
    public static void validateEffectiveDates(
            LocalDate effectiveDate,
            LocalDate expirationDate,
            List<ValidationResultInfo> error) {
        if (effectiveDate != null
                && expirationDate != null
                && effectiveDate.isAfter(expirationDate)) {
            error
                    .add(new ValidationResultInfo("effectiveDates",
                            ErrorLevel.ERROR,
                            "expirationDate must come after effectiveDate"));
        }
    }

    /**
     * Check if date is effective.
     *
     * @param date           date to check
     * @param effectiveDate  contains effectiveDate
     * @param expirationDate contain expirationDate
     * @return true date is effective.
     */
    public static boolean isEffectiveDate(
            Date date,
            Date effectiveDate,
            Date expirationDate) {
        return date != null
                && (effectiveDate == null || date.after(effectiveDate))
                && (expirationDate == null || date.before(expirationDate));
    }

//    /**
//     * Replace CommonFuzzyText in name and split from space
//     *
//     * @param name name to replace
//     * @return set of words.
//     */
//    public static Set<String> getSetReplacingCommonFuzzyText(String name) {
//        Set<String> word = new HashSet<>();
//        if (name != null) {
//            name = name
//                    .replaceAll("[^a-zA-Z0-9\\s]", "")
//                    .toLowerCase();
//            for (Map.Entry<String, String> entry
//                    : Constant.COMMON_FUZZY_TEXT.entrySet()) {
//                name = name.replace(entry.getKey(), entry.getValue());
//            }
//            Collections.addAll(word, name.split("[\\s]+"));
//        }
//        return word;
//    }

    /**
     * Validation method for Integer.
     *
     * @param field     contains field values
     * @param fieldName name of the field
     * @param errors
     */
    public static void validateInteger(
            String field,
            String fieldName,
            List<ValidationResultInfo> errors) {
        if (field != null) {
            try {
                Integer.parseInt(field);
            } catch (NumberFormatException e) {
                LOGGER.error("caught NumberFormatException in ValidationUtils ", e);
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " field must contain integer value"));
            }
        }
    }

    /**
     * Validation method for Long.
     *
     * @param field     contains field values
     * @param fieldName name of the field
     * @param errors
     */
    public static void validateLong(
            String field,
            String fieldName,
            List<ValidationResultInfo> errors) {
        if (field != null) {
            try {
                Long.parseLong(field);
            } catch (NumberFormatException e) {
                LOGGER.error("caught NumberFormatException in ValidationUtils ", e);
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " field must contain numeric value"));
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
                                fieldName + " field must be greater than " + minValue));
            } else if (maxValue != null && field > maxValue) {
                errors
                        .add(new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                fieldName + " field must be less than " + maxValue));
            }
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

    public static void throwDataValidationOnValidationErrors(
            List<ValidationResultInfo> validationResultInfos
    ) throws DataValidationErrorException {
        if (ValidationUtils.containsErrors(validationResultInfos, ErrorLevel.ERROR)) {
            LOGGER.error("caught DataValidationErrorException in ValidationUtils ");
            throw new DataValidationErrorException("Error(s) occurred validating", validationResultInfos);
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
     * Validation method for password match.
     *
     * @param password  contains field values
     * @param fieldName name of the field
     * @param minLen    Integer password length
     * @param maxLen    Integer password length
     * @param errors    error list where we can add details.
     */
    public static void validatePasswordString(
            String password,
            String fieldName,
            Integer minLen,
            Integer maxLen,
            List<ValidationResultInfo> errors) {
        StringBuilder msg = new StringBuilder();

        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        boolean special = false;
        boolean white = false;

        boolean minLength = false;
        boolean maxLength = false;

        if (password.length() >= minLen) {
            minLength = true;
        }

        if (password.length() <= maxLen) {
            maxLength = true;
        }

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                upper = true;
            }

            if (Character.isLowerCase(c)) {
                lower = true;
            }
            if (Character.isDigit(c)) {
                digit = true;
            }
            if (Character.isWhitespace(c)) {
                white = true;
            }
            if (c == '!' || c == '@' || c == '#' || c == '$' || c == '5' || c == '&') {
                special = true;
            }
        }
        if (!digit) {
            msg.append("Must have at least one numeric character\\n");
        }
        if (!lower) {
            msg.append("Must have at least one lowercase character\n");
        }
        if (!upper) {
            msg.append("Must have at least one uppercase character\n");
        }
        if (white) {
            msg.append("Must not contain white space.\n");
        }
        if (!special) {
            msg.append("Must have at least one special symbol among !@#$%&\n");
        }
        if (!minLength || !maxLength) {
            msg.append("Password length should be between " + minLen + " and " + maxLen + "\n");
        }

        String errMsg = msg.toString();
        if (errMsg.length() > 0) {
            errors
                    .add(new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            errMsg));
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


    public static <T> boolean isAnyNull(T... objects) {
        for (T obj : objects) {
            if (obj == null) {
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
