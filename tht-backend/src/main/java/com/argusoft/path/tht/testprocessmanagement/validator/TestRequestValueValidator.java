package com.argusoft.path.tht.testprocessmanagement.validator;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestValueEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * validation methods for testrequest
 *
 * @author Aastha
 */

@Component
public class TestRequestValueValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestRequestValueValidator.class);

    public static void validateUpdateTestRequestValue(List<TestRequestValueEntity> testRequestValueEntities, TestRequestService testRequestService, ContextInfo contextInfo) throws DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestRequest(testRequestValueEntities,
                testRequestService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, TestRequestValueValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    validationResultEntities);

        }
    }

    private static void validateCommonRequired (List<TestRequestValueEntity> testRequestValueEntities, List<ValidationResultInfo> errors) {
        for(TestRequestValueEntity testRequestValueEntity : testRequestValueEntities) {
            ValidationUtils.validateRequired(testRequestValueEntity, "value" , errors);
        }
    }

    private static void validateCommonForeignKey(List<TestRequestValueEntity> testRequestValueEntities, TestRequestService testRequestService, ContextInfo contextInfo, List<ValidationResultInfo> errors) {
        for (TestRequestValueEntity testRequestValueEntity : testRequestValueEntities) {
            try {
                if (testRequestValueEntity.getTestRequest() != null) {
                    testRequestValueEntity.setTestRequest(testRequestService.getTestRequestById(testRequestValueEntity.getTestRequest().getId(), contextInfo));
                }
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestRequestValidator.class.getSimpleName(), ex);
                String fieldName = "testRequestValues.testRequestId";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
            }
        }
    }

    private static List<ValidationResultInfo> validateTestRequest(List<TestRequestValueEntity> testRequestValueEntities,
                                                                 TestRequestService testRequestService,
                                                                 ContextInfo contextInfo) {

        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        trimTestRequestValues(testRequestValueEntities);

        // check Common Required
        validateCommonRequired(testRequestValueEntities, errors);

        // check Common ForeignKey
        validateCommonForeignKey(testRequestValueEntities, testRequestService, contextInfo, errors);

        //For: testRequestValue
        validateTestRequestEntityTestRequestValue(testRequestValueEntities,
                errors);

        return errors;
    }



    private static void validateTestRequestEntityTestRequestValue(List<TestRequestValueEntity> testRequestValueEntities,
                                                                  List<ValidationResultInfo> errors) {

        ValidationUtils.validateCollectionSize(testRequestValueEntities, "testRequestValue", 1, null, errors);

        //loop to check length of value
        for (TestRequestValueEntity entity : testRequestValueEntities) {
            //check for value
            ValidationUtils
                    .validateLength(entity.getTestRequestValueInput(),
                            "value",
                            0,
                            255,
                            errors);
        }
    }

    //trim all TestRequest field
    private static void trimTestRequestValues(List<TestRequestValueEntity> testRequestValueEntities) {
        for(TestRequestValueEntity testRequestValueEntity : testRequestValueEntities){
            if (testRequestValueEntity.getTestRequestValueInput() != null) {
                testRequestValueEntity.setTestRequestValueInput(testRequestValueEntity.getTestRequestValueInput().trim());
            }
        }
    }

}
