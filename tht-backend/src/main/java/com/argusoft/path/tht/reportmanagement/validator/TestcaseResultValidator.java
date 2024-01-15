package com.argusoft.path.tht.reportmanagement.validator;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestcaseResultValidator {

    public static List<ValidationResultInfo> validateCreateUpdateTestCaseResult(String validationTypeKey, TestcaseResultService testcaseResultService, UserService userService, TestcaseOptionService testcaseOptionService, TestcaseResultEntity testcaseResultEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestCaseResult(validationTypeKey,
                testcaseResultEntity,
                userService,
                testcaseResultService,
                testcaseOptionService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
        return validationResultEntities;
    }

    public static void validateSubmitTestcaseResult(String testcaseResultId, String selectedTestcaseOptionId, String validationTypekey, TestcaseResultService testcaseResultService, TestcaseOptionService testcaseOptionService, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, OperationFailedException {
        List<ValidationResultInfo> validationResultEntitys
                = validateTestcaseResultSubmit(
                testcaseResultId,
                selectedTestcaseOptionId,
                validationTypekey,
                testcaseResultService,
                testcaseOptionService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
    }

    public static List<ValidationResultInfo> validateTestCaseResult(String validationTypeKey, TestcaseResultEntity testcaseResultEntity, UserService userService, TestcaseResultService testcaseResultService, TestcaseOptionService testcaseOptionService, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        if (testcaseResultEntity == null) {
            throw new InvalidParameterException("TestcaseResultEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestcaseResultEntity originalEntity = null;
        trimTestcaseResult(testcaseResultEntity);

        // check Common Required
        validateCommonRequired(testcaseResultEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(testcaseResultEntity, userService, testcaseResultService, testcaseOptionService, errors, contextInfo);

        // check Common Unique
        validateCommonUnique(testcaseResultEntity,
                validationTypeKey,
                testcaseResultService,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testcaseResultEntity.getId() != null) {
                    try {
                        originalEntity = testcaseResultService
                                .getTestcaseResultById(testcaseResultEntity.getId(),
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

                validateUpdateTestcaseResult(errors,
                        testcaseResultEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateTestcaseResult(errors, testcaseResultEntity, testcaseResultService, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateTestcaseResultEntityId(testcaseResultEntity,
                errors);
        // For :Name
        validateTestcaseResultEntityName(testcaseResultEntity,
                errors);
        // For :Order
        validateTestcaseResultEntityOrder(testcaseResultEntity,
                errors);
        // For :TestcaseOption
        validateTestcaseResultEntityTestcaseOption(testcaseResultEntity,
                errors);
        // For :IsSuccess
        validateTestcaseResultEntityIsSuccess(testcaseResultEntity,
                errors);

        return errors;
    }

    private static void validateCommonForeignKey(TestcaseResultEntity testcaseResultEntity,
                                                 UserService userService,
                                                 TestcaseResultService testcaseResultService,
                                                 TestcaseOptionService testcaseOptionService,
                                                 List<ValidationResultInfo> errors,
                                                 ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        //validate TestcaseResult foreignKey.
        if (testcaseResultEntity.getTester() != null) {
            try {
                testcaseResultEntity.setTester(
                        userService.getUserById(testcaseResultEntity.getTester().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "tester";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the tester does not exists"));
            }
        }
        //validate TestcaseResult parentTestcaseResult.
        if (testcaseResultEntity.getParentTestcaseResult() != null) {
            try {
                testcaseResultEntity.setParentTestcaseResult(
                        testcaseResultService.getTestcaseResultById(testcaseResultEntity.getParentTestcaseResult().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "parentTestcaseResult";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the parentTestcaseResult does not exists"));
            }
        }
        //validate TestcaseOption foreignKey.
        if (testcaseResultEntity.getTestcaseOption() != null) {
            try {
                testcaseResultEntity.setTestcaseOption(
                        testcaseOptionService.getTestcaseOptionById(testcaseResultEntity.getTestcaseOption().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "testcaseOption";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the testcaseOption does not exists"));
            }
        }
    }

    //validate update
    private static void validateUpdateTestcaseResult(List<ValidationResultInfo> errors,
                                                     TestcaseResultEntity testcaseResultEntity,
                                                     TestcaseResultEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(testcaseResultEntity.getId(), "id", errors);
        //check the meta required
        if (testcaseResultEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!testcaseResultEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the TestcaseResult since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        validateNotUpdatable(errors, testcaseResultEntity, originalEntity);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             TestcaseResultEntity testcaseResultEntity,
                                             TestcaseResultEntity originalEntity) {
    }

    //validate create
    private static void validateCreateTestcaseResult(
            List<ValidationResultInfo> errors,
            TestcaseResultEntity testcaseResultEntity,
            TestcaseResultService testcaseResultService,
            ContextInfo contextInfo) {
        if (testcaseResultEntity.getId() != null) {
            try {
                testcaseResultService.getTestcaseResultById(testcaseResultEntity.getId(),
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
    private static void validateCommonRequired(TestcaseResultEntity testcaseResultEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testcaseResultEntity.getName(), "name", errors);
        ValidationUtils.validateRequired(testcaseResultEntity.getRank(), "rank", errors);
        if (TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED.equals(testcaseResultEntity.getState())
                && TestcaseServiceConstants.TESTCASE_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())
                && Objects.equals(Boolean.TRUE, testcaseResultEntity.getManual())
                && Objects.equals(Boolean.FALSE, testcaseResultEntity.getHasSystemError())) {
            ValidationUtils.validateRequired(testcaseResultEntity.getTestcaseOption(), "testcaseOption", errors);
        }
    }

    //Validate Common Unique
    private static void validateCommonUnique(TestcaseResultEntity testcaseResultEntity,
                                             String validationTypeKey,
                                             TestcaseResultService testcaseResultService,
                                             List<ValidationResultInfo> errors,
                                             ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || testcaseResultEntity.getId() != null)) {
            TestcaseResultSearchFilter searchFilter = new TestcaseResultSearchFilter();
            searchFilter.setTestRequestId(testcaseResultEntity.getTestRequestId());
            searchFilter.setRefId(testcaseResultEntity.getRefId());
            searchFilter.setRefObjUri(testcaseResultEntity.getRefObjUri());
            searchFilter.setIsManual(Objects.equals(Boolean.TRUE, testcaseResultEntity.getManual()));
            Page<TestcaseResultEntity> testcaseResultEntities = testcaseResultService
                    .searchTestcaseResults(
                            null,
                            searchFilter,
                            Constant.TWO_VALUE_PAGE,
                            contextInfo);

            // if info found with same testRequestId, refObjUri, refId than and not current id
            boolean flag
                    = testcaseResultEntities.stream().anyMatch(c -> (validationTypeKey.equals(Constant.CREATE_VALIDATION)
                    || !c.getId().equals(testcaseResultEntity.getId()))
            );
            if (flag) {
                String fieldName = "testRequestId, refObjUri and refId";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Given TestcaseResult with same testRequestId, refObjUri and refId already exists."));
            }
        }
    }

    //Validation For :Id
    private static void validateTestcaseResultEntityId(TestcaseResultEntity testcaseResultEntity,
                                                       List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(testcaseResultEntity.getId(), "id", errors);
    }

    //Validation For :Name
    private static void validateTestcaseResultEntityName(TestcaseResultEntity testcaseResultEntity,
                                                         List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseResultEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //Validation For :Order
    private static void validateTestcaseResultEntityOrder(TestcaseResultEntity testcaseResultEntity,
                                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(testcaseResultEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For :TestcaseOption
    private static void validateTestcaseResultEntityTestcaseOption(TestcaseResultEntity testcaseResultEntity,
                                                                   List<ValidationResultInfo> errors) {
    }

    //Validation For :isSuccess
    private static void validateTestcaseResultEntityIsSuccess(TestcaseResultEntity testcaseResultEntity,
                                                              List<ValidationResultInfo> errors) {
    }

    //trim all TestcaseResult field
    private static void trimTestcaseResult(TestcaseResultEntity testcaseResultEntity) {
        if (testcaseResultEntity.getId() != null) {
            testcaseResultEntity.setId(testcaseResultEntity.getId().trim());
        }
        if (testcaseResultEntity.getName() != null) {
            testcaseResultEntity.setName(testcaseResultEntity.getName().trim());
        }
        if (testcaseResultEntity.getDescription() != null) {
            testcaseResultEntity.setDescription(testcaseResultEntity.getDescription().trim());
        }
        if (testcaseResultEntity.getMessage() != null) {
            testcaseResultEntity.setMessage(testcaseResultEntity.getMessage().trim());
        }
        if (testcaseResultEntity.getRefObjUri() != null) {
            testcaseResultEntity.setRefObjUri(testcaseResultEntity.getRefObjUri().trim());
        }
        if (testcaseResultEntity.getRefId() != null) {
            testcaseResultEntity.setRefId(testcaseResultEntity.getRefId().trim());
        }
    }

    private static List<ValidationResultInfo> validateTestcaseResultSubmit(
            String testcaseResultId,
            String selectedTestcaseOptionId,
            String validationTypeKey,
            TestcaseResultService testcaseResultService,
            TestcaseOptionService testcaseOptionService,
            ContextInfo contextInfo) {
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestcaseResultEntity originalEntity;
        try {
            originalEntity = testcaseResultService
                    .getTestcaseResultById(testcaseResultId,
                            contextInfo);
        } catch (DoesNotExistException | InvalidParameterException ex) {
            String fieldName = "testcaseResultId";
            errors.add(
                    new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            "The testcaseResultId supplied for the submit does not "
                                    + "exists"));
            return errors;
        }
        try {
            TestcaseOptionEntity testcaseOption = testcaseOptionService
                    .getTestcaseOptionById(selectedTestcaseOptionId,
                            contextInfo);

            if (!originalEntity.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI)
                    && !testcaseOption.getTestcase().getId().equals(originalEntity.getRefId())
            ) {
                String fieldName = "selectedTestcaseOptionId";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The selectedTestcaseOptionId supplied for the submit is invalid for the testcaseResult."));
            }
        } catch (DoesNotExistException | InvalidParameterException ex) {
            String fieldName = "selectedTestcaseOptionId";
            errors.add(
                    new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            "The selectedTestcaseOptionId supplied for the submit does not "
                                    + "exists"));
        }
        return errors;
    }
}
