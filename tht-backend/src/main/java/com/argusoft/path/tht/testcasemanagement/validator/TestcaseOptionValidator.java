package com.argusoft.path.tht.testcasemanagement.validator;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestcaseOptionValidator {

    public static void validateCreateUpdateTestcaseOption(String validationTypeKey, TestcaseOptionService testcaseOptionService, TestcaseService testcaseService, TestcaseOptionEntity testcaseOptionEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestcaseOption(validationTypeKey,
                testcaseOptionEntity,
                testcaseOptionService,
                testcaseService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }

    }

    public static List<ValidationResultInfo> validateTestcaseOption(String validationTypeKey, TestcaseOptionEntity testcaseOptionEntity, TestcaseOptionService testcaseOptionService, TestcaseService testcaseService, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        if (testcaseOptionEntity == null) {
            throw new InvalidParameterException("testcaseOptionEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestcaseOptionEntity originalEntity = null;
        trimTestcaseOption(testcaseOptionEntity);

        // check Common Required
        validateCommonRequired(testcaseOptionEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(testcaseOptionEntity, testcaseService, errors, contextInfo);

        // check Common Unique
        validateCommonUnique(testcaseOptionEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testcaseOptionEntity.getId() != null) {
                    try {
                        originalEntity = testcaseOptionService
                                .getTestcaseOptionById(testcaseOptionEntity.getId(),
                                        contextInfo);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR,
                                        "The id supplied to the update does not "
                                                + "exists"));
                    }
                }

                if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
                    return errors;
                }

                validateUpdateTestcaseOption(errors,
                        testcaseOptionEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateTestcaseOption(errors, testcaseOptionEntity, testcaseOptionService, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
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
        // For :IsFunctional
        validateTestcaseOptionEntityIsSuccess(testcaseOptionEntity,
                errors);
        return errors;

    }

    private static void validateCommonForeignKey(TestcaseOptionEntity testcaseOptionEntity
            , TestcaseService testcaseService,
                                                 List<ValidationResultInfo> errors,
                                                 ContextInfo contextInfo) {
        Set<TestcaseEntity> testcaseEntitySet = new HashSet<>();

        if (testcaseOptionEntity.getTestcase() != null) {
            try {
                testcaseOptionEntity.setTestcase(
                        testcaseService.getTestcaseById(testcaseOptionEntity.getTestcase().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "testcase";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the testcase does not exists"));
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
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!testcaseOptionEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the TestcaseOption since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        validateNotUpdatable(errors, testcaseOptionEntity, originalEntity);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             TestcaseOptionEntity testcaseOptionEntity,
                                             TestcaseOptionEntity originalEntity) {
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
                                "The id supplied to the create already exists"));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                // This is ok because created id should be unique
            }
        }
    }

    //Validate Required
    private static void validateCommonRequired(TestcaseOptionEntity testcaseOptionEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testcaseOptionEntity.getName(), "name", errors);
        ValidationUtils.validateRequired(testcaseOptionEntity.getTestcase(), "component", errors);
    }

    //Validate Common Unique
    private static void validateCommonUnique(TestcaseOptionEntity testcaseOptionEntity,
                                             String validationTypeKey,
                                             List<ValidationResultInfo> errors,
                                             ContextInfo contextInfo)
            throws OperationFailedException {
        // check unique field
    }

    //Validation For :Id
    private static void validateTestcaseOptionEntityId(TestcaseOptionEntity testcaseOptionEntity,
                                                       List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(testcaseOptionEntity.getId(), "id", errors);
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

    //Validation For :IsFunctional
    private static void validateTestcaseOptionEntityIsSuccess(TestcaseOptionEntity testcaseOptionEntity,
                                                              List<ValidationResultInfo> errors) {
    }

    //Validation For :ComponentId
    private static void validateTestcaseOptionEntityComponentId(TestcaseOptionEntity testcaseOptionEntity,
                                                                List<ValidationResultInfo> errors) {
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