package com.argusoft.path.tht.reportmanagement.validator;

import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.service.TestResultRelationService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.RefObjectUriAndRefIdValidator;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestResultRelationValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseResultValidator.class);

    private static RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidator;

    @Autowired
    public void setRefObjectUriAndRefIdValidator(RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidatorIdValidator) {
        TestResultRelationValidator.refObjectUriAndRefIdValidator = refObjectUriAndRefIdValidatorIdValidator;
    }

    public static void validateCreateUpdateTestResultRelation(String validationTypeKey, TestResultRelationEntity testResultRelationEntity, TestResultRelationService testResultRelationService, TestcaseResultService testcaseResultService, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestResultRelation(validationTypeKey,
                        testResultRelationService,
                        testcaseResultService,
                        testResultRelationEntity,
                        contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error(ValidateConstant.DATA_VALIDATION_EXCEPTION + TestResultRelationValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
    }

    public static List<ValidationResultInfo> validateTestResultRelation(String validationTypeKey, TestResultRelationService testResultRelationService, TestcaseResultService testcaseResultService, TestResultRelationEntity testResultRelationEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        if (testResultRelationEntity == null) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + TestResultRelationValidator.class.getSimpleName());
            throw new InvalidParameterException("testResultRelationEntity is missing");
        }
        if (!StringUtils.hasLength(validationTypeKey)) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + TestResultRelationValidator.class.getSimpleName());
            throw new InvalidParameterException("validationTypeKey is missing");
        }

        List<ValidationResultInfo> errors = new ArrayList<>();
        trimTestResultRelation(testResultRelationEntity);

        validateCommonRequired(testResultRelationEntity, errors);

        validateCommonForeignKey(testResultRelationEntity, testcaseResultService, errors, contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testResultRelationEntity.getId() != null) {
                    try {
                        TestResultRelationEntity originalEntity = testResultRelationService
                                .getTestResultRelationById(testResultRelationEntity.getId(),
                                        contextInfo);
                        validateUpdateTestResultRelation(originalEntity, testResultRelationEntity, errors);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + TestResultRelationValidator.class.getSimpleName());
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR,
                                        "The id supplied to the update does not "
                                        + "exists"));
                        return errors;
                    }
                }
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateTestResultRelation(testResultRelationEntity, testResultRelationService, errors, contextInfo);
                break;
            default:
                LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + TestResultRelationValidator.class.getSimpleName());
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        refObjectUriAndRefIdValidator.refObjectUriAndRefIdValidation(testResultRelationEntity.getRefObjUri(), testResultRelationEntity.getRefId(), contextInfo, errors);

        return errors;

    }

    private static void validateCreateTestResultRelation(TestResultRelationEntity testResultRelationEntity, TestResultRelationService testResultRelationService, List<ValidationResultInfo> errors, ContextInfo contextInfo) {
        if (testResultRelationEntity.getId() != null) {
            try {
                testResultRelationService.getTestResultRelationById(testResultRelationEntity.getId(),
                        contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied to the create already exists"));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestResultRelationValidator.class.getSimpleName());
                // This is ok because created id should be unique
            }
        }
    }

    private static void validateUpdateTestResultRelation(TestResultRelationEntity originalEntity, TestResultRelationEntity testResultRelationEntity, List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testResultRelationEntity.getId(), "id", errors);

        //check the meta required
        if (testResultRelationEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        } // check meta version id
        else if (!testResultRelationEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the TestResultRelation since you"
                    + " started updating, you might want to"
                    + " refresh your copy."));
        }
        validateNotUpdatable(originalEntity, testResultRelationEntity, errors);
    }

    private static void validateNotUpdatable(TestResultRelationEntity originalEntity, TestResultRelationEntity testResultRelationEntity, List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotUpdatable(testResultRelationEntity.getRefObjUri(), originalEntity.getRefObjUri(), "refObjUri", errors);
        ValidationUtils.validateNotUpdatable(testResultRelationEntity.getRefId(), originalEntity.getRefId(), "refId", errors);
        ValidationUtils.validateNotUpdatable(testResultRelationEntity.getVersionOfRefEntity(), originalEntity.getVersionOfRefEntity(), "versionOfRefEntity", errors);
    }

    private static void validateCommonForeignKey(TestResultRelationEntity testResultRelationEntity, TestcaseResultService testcaseResultService, List<ValidationResultInfo> errors, ContextInfo contextInfo) {
        if (testResultRelationEntity.getTestcaseResultEntity().getId() != null) {
            try {
                testResultRelationEntity.setTestcaseResultEntity(
                        testcaseResultService.getTestcaseResultById(testResultRelationEntity.getTestcaseResultEntity().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestResultRelationValidator.class.getSimpleName());
                String fieldName = "testcaseResult";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the testcaseResult does not exists"));
            }
        }
    }

    private static void validateCommonRequired(TestResultRelationEntity testResultRelationEntity, List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testResultRelationEntity.getVersionOfRefEntity(), "version", errors);
        ValidationUtils.validateRequired(testResultRelationEntity.getRefObjUri(), "refObjUri", errors);
        ValidationUtils.validateRequired(testResultRelationEntity.getRefId(), "refId", errors);
        ValidationUtils.validateRequired(testResultRelationEntity.getTestcaseResultEntity(), "testcaseResultEntity", errors);
    }

    private static void trimTestResultRelation(TestResultRelationEntity testResultRelationEntity) {
        if (testResultRelationEntity.getRefId() != null) {
            testResultRelationEntity.setRefId(testResultRelationEntity.getRefId().trim());
        }
        if (testResultRelationEntity.getRefObjUri() != null) {
            testResultRelationEntity.setRefObjUri(testResultRelationEntity.getRefObjUri().trim());
        }
    }

}
