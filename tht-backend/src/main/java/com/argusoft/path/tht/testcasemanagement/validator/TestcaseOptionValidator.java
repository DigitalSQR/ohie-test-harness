package com.argusoft.path.tht.testcasemanagement.validator;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

public class TestcaseOptionValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseOptionValidator.class);

    public static void validateCreateUpdateTestcaseOption(String validationTypeKey, TestcaseOptionService testcaseOptionService, TestcaseService testcaseService, TestcaseOptionEntity testcaseOptionEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestcaseOption(validationTypeKey,
                        testcaseOptionEntity,
                        testcaseOptionService,
                        testcaseService,
                        contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, TestcaseOptionValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    validationResultEntities);
        }

    }

    public static List<ValidationResultInfo> validateTestcaseOption(String validationTypeKey, TestcaseOptionEntity testcaseOptionEntity, TestcaseOptionService testcaseOptionService, TestcaseService testcaseService, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        if (!StringUtils.hasLength(validationTypeKey)) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseOptionValidator.class.getSimpleName());
            throw new InvalidParameterException(ValidateConstant.MISSING_VALIDATION_TYPE_KEY);
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        trimTestcaseOption(testcaseOptionEntity);

        // check Common Required
        validateCommonRequired(testcaseOptionEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(testcaseOptionEntity, testcaseService, errors, contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testcaseOptionEntity.getId() != null) {
                    try {
                        TestcaseOptionEntity originalEntity = testcaseOptionService.getTestcaseOptionById(testcaseOptionEntity.getId(), contextInfo);
                        validateUpdateTestcaseOption(errors, testcaseOptionEntity, originalEntity);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestcaseOptionValidator.class.getSimpleName(), ex);
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR,
                                        ValidateConstant.ID_SUPPLIED + "update" + ValidateConstant.DOES_NOT_EXIST));
                        return errors;
                    }
                }
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateTestcaseOption(errors, testcaseOptionEntity, testcaseOptionService, contextInfo);
                break;
            default:
                LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseOptionValidator.class.getSimpleName());
                throw new InvalidParameterException(ValidateConstant.INVALID_VALIDATION_TYPE_KEY);
        }

        // For : Id
        validateTestcaseOptionEntityId(testcaseOptionEntity,
                errors);
        // For :Name
        validateTestcaseOptionEntityName(testcaseOptionEntity,
                errors);
        // For :Order
        validateTestcaseOptionEntityOrder(testcaseOptionEntity,
                errors);
        // For : Description
        validateTestcaseOptionDescription(testcaseOptionEntity,
                errors);
        return errors;

    }

    private static void validateCommonForeignKey(TestcaseOptionEntity testcaseOptionEntity,
             TestcaseService testcaseService,
            List<ValidationResultInfo> errors,
            ContextInfo contextInfo) {
        Set<TestcaseEntity> testcaseEntitySet = new HashSet<>();

        if (testcaseOptionEntity.getTestcase() != null) {
            try {
                testcaseOptionEntity.setTestcase(
                        testcaseService.getTestcaseById(testcaseOptionEntity.getTestcase().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestcaseOptionValidator.class.getSimpleName(), ex);
                String fieldName = "testcase";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
            }
        }
    }

    //validate update
    private static void validateUpdateTestcaseOption(List<ValidationResultInfo> errors,
            TestcaseOptionEntity testcaseOptionEntity,
            TestcaseOptionEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(testcaseOptionEntity.getId(), "id", errors);
        //check the meta required
        if (testcaseOptionEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + ValidateConstant.MUST_PROVIDED));
        } // check meta version id
        else if (!testcaseOptionEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    ValidateConstant.SOMEONE_UPDATED + "TestcaseOption" + ValidateConstant.REFRESH_COPY));
        }
        // check not updatable fields
        validateNotUpdatable(errors, testcaseOptionEntity, originalEntity);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
            TestcaseOptionEntity testcaseOptionEntity,
            TestcaseOptionEntity originalEntity) {
        // state can't be updated
        ValidationUtils.validateNotUpdatable(testcaseOptionEntity.getState(), originalEntity.getState(), "state", errors);
    }

    //validate create
    private static void validateCreateTestcaseOption(
            List<ValidationResultInfo> errors,
            TestcaseOptionEntity testcaseOptionEntity,
            TestcaseOptionService testcaseOptionService,
            ContextInfo contextInfo) {
        if (testcaseOptionEntity.getId() != null) {
            try {
                testcaseOptionService.getTestcaseOptionById(testcaseOptionEntity.getId(),
                        contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + "create" + ValidateConstant.ALREADY_EXIST));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestcaseOptionValidator.class.getSimpleName(), ex);
                // This is ok because created id should be unique
            }
        }
    }

    //Validate Required
    private static void validateCommonRequired(TestcaseOptionEntity testcaseOptionEntity,
            List<ValidationResultInfo> errors) {
        //check for name
        ValidationUtils
                .validateRequired(testcaseOptionEntity.getName(), "name", errors);
        //check for testcaseId
        ValidationUtils
                .validateRequired(testcaseOptionEntity.getTestcase(), "testcase", errors);
        //check for state
        ValidationUtils
                .validateRequired(testcaseOptionEntity.getState(), "state", errors);
        //check for success
        ValidationUtils
                .validateRequired(testcaseOptionEntity.getSuccess(), "success", errors);
        //check for rank
        ValidationUtils
                .validateRequired(testcaseOptionEntity.getRank(), "rank", errors);
    }

    //Validation For :Id
    private static void validateTestcaseOptionEntityId(TestcaseOptionEntity testcaseOptionEntity,
            List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseOptionEntity.getId(),
                "id",
                0,
                255,
                errors);
    }

    //Validation For :Name
    private static void validateTestcaseOptionEntityName(TestcaseOptionEntity testcaseOptionEntity,
            List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseOptionEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //Validation For :Order
    private static void validateTestcaseOptionEntityOrder(TestcaseOptionEntity testcaseOptionEntity,
            List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(testcaseOptionEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For:Description
    private static void validateTestcaseOptionDescription(TestcaseOptionEntity testcaseOption,
            List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseOption.getDescription(),
                "description",
                0,
                1000,
                errors);
    }

    //trim all TestcaseOption field
    private static void trimTestcaseOption(TestcaseOptionEntity TestcaseOptionEntity) {
        if (TestcaseOptionEntity.getId() != null) {
            TestcaseOptionEntity.setId(TestcaseOptionEntity.getId().trim());
        }
        if (TestcaseOptionEntity.getName() != null) {
            TestcaseOptionEntity.setName(TestcaseOptionEntity.getName().trim());
        }
        if (TestcaseOptionEntity.getDescription() != null) {
            TestcaseOptionEntity.setDescription(TestcaseOptionEntity.getDescription().trim());
        }
    }

}
