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
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TestcaseValidator {

    public static void validateCreateUpdateTestCase(String validationTypeKey, TestcaseEntity testcaseEntity, TestcaseService testcaseService, SpecificationService specificationService, ApplicationContext applicationContext, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestCase(validationTypeKey,
                testcaseEntity,
                testcaseService,
                specificationService,
                applicationContext,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }

    }

    public static List<ValidationResultInfo> validateTestCase(String validationTypeKey,
                                                                          TestcaseEntity testcaseEntity,
                                                                          TestcaseService testcaseService,
                                                                          SpecificationService specificationService,
                                                                          ApplicationContext applicationContext,
                                                                          ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        if (testcaseEntity == null) {
            throw new InvalidParameterException("testcaseEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestcaseEntity originalEntity = null;
        trimTestcase(testcaseEntity);

        // check Common Required
        validateCommonRequired(testcaseEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(testcaseEntity, errors, specificationService, contextInfo);

        // check Common Unique
        validateCommonUnique(testcaseEntity,
                validationTypeKey,
                errors,
                testcaseService,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testcaseEntity.getId() != null) {
                    try {
                        originalEntity = testcaseService
                                .getTestcaseById(testcaseEntity.getId(),
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

                validateUpdateTestcase(errors,
                        testcaseEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateTestcase(errors, testcaseEntity, testcaseService, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateTestcaseEntityId(testcaseEntity,
                errors);
        // For :Name
        validateTestcaseEntityName(testcaseEntity,
                errors);
        // For :ClassName
        validateTestcaseEntityClassName(testcaseEntity, applicationContext,
                errors);
        // For :Order
        validateTestcaseEntityOrder(testcaseEntity,
                errors);
        // For :IsManual
        validateTestcaseEntityIsManual(testcaseEntity,
                errors);
        // For :IsRequired
        validateTestcaseEntityIsRequired(testcaseEntity,
                errors);

        return errors;

    }


    //validate update
    private static void validateUpdateTestcase(List<ValidationResultInfo> errors,
                                               TestcaseEntity testcaseEntity,
                                               TestcaseEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(testcaseEntity.getId(), "id", errors);
        //check the meta required
        if (testcaseEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!testcaseEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the Testcase since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        validateNotUpdatable(errors, testcaseEntity, originalEntity);
    }


    //validate create
    private static void validateCreateTestcase(
            List<ValidationResultInfo> errors,
            TestcaseEntity testcaseEntity,
            TestcaseService testcaseService,
            ContextInfo contextInfo) {
        if (testcaseEntity.getId() != null) {
            try {
                testcaseService.getTestcaseById(testcaseEntity.getId(),
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
    private static void validateCommonRequired(TestcaseEntity testcaseEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testcaseEntity.getSpecification(), "specification", errors);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             TestcaseEntity testcaseEntity,
                                             TestcaseEntity originalEntity) {
        //className can't be updatable
        ValidationUtils.validateNotUpdatable(testcaseEntity.getBeanName(), originalEntity.getBeanName(), "beanName", errors);
    }

    //Validation For :Id
    private static void validateTestcaseEntityId(TestcaseEntity testcaseEntity,
                                                 List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(testcaseEntity.getId(), "id", errors);
    }

    //Validation For :Name
    private static void validateTestcaseEntityName(TestcaseEntity testcaseEntity,
                                                   List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //Validation For :ClassName
    private static void validateTestcaseEntityClassName(TestcaseEntity testcaseEntity,
                                                        ApplicationContext applicationContext,
                                                        List<ValidationResultInfo> errors) {
        try {
            TestCase cRTestCases = (TestCase) applicationContext.getBean(testcaseEntity.getBeanName());
        } catch (Exception e) {
            errors
                    .add(new ValidationResultInfo("beanName",
                            ErrorLevel.ERROR,
                            "beanName doesn't exist with " + testcaseEntity.getBeanName()));
        }
    }

    //Validation For :Order
    private static void validateTestcaseEntityOrder(TestcaseEntity testcaseEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(testcaseEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For :IsManual
    private static void validateTestcaseEntityIsManual(TestcaseEntity testcaseEntity,
                                                       List<ValidationResultInfo> errors) {
    }

    //Validation For :IsRequired
    private static void validateTestcaseEntityIsRequired(TestcaseEntity testcaseEntity,
                                                         List<ValidationResultInfo> errors) {
    }

    //trim all Testcase field
    private static void trimTestcase(TestcaseEntity TestcaseEntity) {
        if (TestcaseEntity.getId() != null) {
            TestcaseEntity.setId(TestcaseEntity.getId().trim());
        }
        if (TestcaseEntity.getName() != null) {
            TestcaseEntity.setName(TestcaseEntity.getName().trim());
        }
        if (TestcaseEntity.getDescription() != null) {
            TestcaseEntity.setDescription(TestcaseEntity.getDescription().trim());
        }
        if (TestcaseEntity.getBeanName() != null) {
            TestcaseEntity.setBeanName(TestcaseEntity.getBeanName().trim());
        }
    }

    private static void validateCommonForeignKey(TestcaseEntity testcaseEntity,
                                                 List<ValidationResultInfo> errors,
                                                 SpecificationService specificationService,
                                                 ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        if (testcaseEntity.getSpecification() != null) {
            try {
                testcaseEntity.setSpecification(
                        specificationService.getSpecificationById(testcaseEntity.getSpecification().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "specification";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the specification does not exists"));
            }
        }
    }

    //Validate Common Unique
    private static void validateCommonUnique(TestcaseEntity testcaseEntity,
                                             String validationTypeKey,
                                             List<ValidationResultInfo> errors,
                                             TestcaseService testcaseService,
                                             ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || testcaseEntity.getId() != null)
                && StringUtils.isEmpty(testcaseEntity.getName())) {
            TestcaseSearchFilter searchFilter = new TestcaseSearchFilter();
            searchFilter.setName(testcaseEntity.getName());
            Page<TestcaseEntity> testcaseEntities = testcaseService
                    .searchTestcases(
                            null,
                            searchFilter,
                            Constant.TWO_VALUE_PAGE,
                            contextInfo);

            // if info found with same name than and not current id
            boolean flag
                    = testcaseEntities.stream().anyMatch(c -> (validationTypeKey.equals(Constant.CREATE_VALIDATION)
                    || !c.getId().equals(testcaseEntity.getId()))
            );
            if (flag) {
                String fieldName = "name";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Given Testcase with same name already exists."));
            }
        }
    }


}
