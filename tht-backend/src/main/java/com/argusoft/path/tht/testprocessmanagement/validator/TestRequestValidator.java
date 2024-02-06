package com.argusoft.path.tht.testprocessmanagement.validator;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
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
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class TestRequestValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestRequestValidator.class);

    private static RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidator;

    public static void validateCreateUpdateTestRequest(String validationTypeKey, TestRequestEntity testRequestEntity, TestRequestService testRequestService, UserService userService, ComponentService componentService, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, OperationFailedException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestRequest(validationTypeKey,
                testRequestEntity,
                testRequestService,
                userService,
                componentService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error("caught DataValidationErrorException in TestRequestValidator ");
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);

        }
    }

    public static void validateTestRequestStartReinitializeProcess(String testRequestId,
                                                                   String refObjUri,
                                                                   String refId,
                                                                   Boolean isManual,
                                                                   Boolean isAutomated,
                                                                   Boolean isRequired,
                                                                   Boolean isRecommended,
                                                                   Boolean isWorkflow,
                                                                   Boolean isFunctional,
                                                                   String validationTypeKey,
                                                                   TestcaseResultService testcaseResultService,
                                                                   ContextInfo contextInfo)
            throws DataValidationErrorException,
            InvalidParameterException,
            OperationFailedException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestRequestProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                validationTypeKey,
                testcaseResultService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error("caught DataValidationErrorException in TestRequestValidator ");
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
    }

    public static List<ValidationResultInfo> validateTestRequestProcess(
            String testRequestId,
            String refObjUri,
            String refId,
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            String validationTypeKey,
            TestcaseResultService testcaseResultService,
            ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException {
        if (StringUtils.isEmpty(testRequestId)
                || StringUtils.isEmpty(refObjUri)
                || StringUtils.isEmpty(refId)
                || StringUtils.isEmpty(validationTypeKey)) {
            LOGGER.error("caught InvalidParameterException in TestRequestValidator ");
            throw new InvalidParameterException("inputData is missing");
        }
        List<ValidationResultInfo> errors = new ArrayList<>();

        refObjectUriAndRefIdValidator.refObjectUriAndRefIdValidation(refObjUri, refId, contextInfo, errors);
        if (!errors.isEmpty()) {
            return errors;
        }

        if (validationTypeKey.equals(Constant.START_PROCESS_VALIDATION)) {
            TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
            searchFilter.setManual(isManual);
            searchFilter.setAutomated(isAutomated);
            searchFilter.setRequired(isRequired);
            searchFilter.setRecommended(isRecommended);
            searchFilter.setWorkflow(isWorkflow);
            searchFilter.setFunctional(isFunctional);
            searchFilter.setRefObjUri(refObjUri);
            searchFilter.setRefId(refId);
            searchFilter.setTestRequestId(testRequestId);

            List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(searchFilter, contextInfo);
            if (testcaseResultEntities.isEmpty()) {
                String fieldName = "inputData";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Process for the requested input doesn't have active testcaseResults."));
            }
            if (testcaseResultEntities.stream().anyMatch(testcaseResultEntity -> testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS))) {
                String fieldName = "inputData";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Process for the requested input has been already started."));
            }
        } else {
            TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
            searchFilter.setManual(isManual);
            searchFilter.setAutomated(isAutomated);
            searchFilter.setRequired(isRequired);
            searchFilter.setRecommended(isRecommended);
            searchFilter.setFunctional(isFunctional);
            searchFilter.setWorkflow(isWorkflow);
            searchFilter.setRefObjUri(refId);
            searchFilter.setRefId(refObjUri);
            searchFilter.setTestRequestId(testRequestId);

            List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(searchFilter, contextInfo);
            if (testcaseResultEntities.stream().anyMatch(testcaseResultEntity ->
                    !testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED))) {
                String fieldName = "inputData";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Process for the requested input is not finished yet."));
            }
        }
        return errors;
    }

    public static List<ValidationResultInfo> validateTestRequest(String validationTypeKey,
                                                                 TestRequestEntity testRequestEntity,
                                                                 TestRequestService testRequestService,
                                                                 UserService userService,
                                                                 ComponentService componentService,
                                                                 ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        if (testRequestEntity == null) {
            LOGGER.error("caught InvalidParameterException in TestRequestValidator ");
            throw new InvalidParameterException("TestRequestEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            LOGGER.error("caught InvalidParameterException in TestRequestValidator ");
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestRequestEntity originalEntity = null;
        trimTestRequest(testRequestEntity);

        // check Common Required
        validateCommonRequired(testRequestEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(testRequestEntity, errors, userService, componentService, contextInfo);

        // check Common Unique
        validateCommonUnique(testRequestEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testRequestEntity.getId() != null) {
                    try {
                        originalEntity = testRequestService
                                .getTestRequestById(testRequestEntity.getId(),
                                        contextInfo);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error("caught DoesNotExistException in TestRequestValidator ", ex);
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

                validateUpdateTestRequest(errors,
                        testRequestEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateTestRequest(errors, testRequestEntity, testRequestService, contextInfo);
                break;
            default:
                LOGGER.error("caught InvalidParameterException in TestRequestValidator ");
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateTestRequestEntityId(testRequestEntity,
                errors);
        // For :Name
        validateTestRequestEntityName(testRequestEntity,
                errors);
        return errors;

    }

    private static void validateCommonForeignKey(TestRequestEntity testRequestEntity,
                                                 List<ValidationResultInfo> errors,
                                                 UserService userService,
                                                 ComponentService componentService,
                                                 ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        //validate TestRequest foreignKey.
        if (testRequestEntity.getApprover() != null) {
            try {
                testRequestEntity.setApprover(
                        userService.getUserById(testRequestEntity.getApprover().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error("caught DoesNotExistException in TestRequestValidator ", ex);
                String fieldName = "approver";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the approver does not exists"));
            }
        }
        if (testRequestEntity.getAssessee() != null) {
            try {
                testRequestEntity.setAssessee(
                        userService.getUserById(testRequestEntity.getAssessee().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error("caught DoesNotExistException in TestRequestValidator ", ex);
                String fieldName = "assessee";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the assessee does not exists"));
            }
        }
        if (!testRequestEntity.getTestRequestUrls().isEmpty()) {
            for (TestRequestUrlEntity testRequestUrlEntity : testRequestEntity.getTestRequestUrls()) {
                try {
                    if (testRequestUrlEntity.getComponent() != null) {
                        testRequestUrlEntity.setComponent(componentService.getComponentById(testRequestUrlEntity.getComponent().getId(), contextInfo));
                    }
                } catch (DoesNotExistException | InvalidParameterException ex) {
                    LOGGER.error("caught DoesNotExistException in TestRequestValidator ", ex);
                    String fieldName = "testRequestUrls.component";
                    errors.add(
                            new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    "The id supplied for the component does not exists"));
                }
            }
        }
    }

    //validate update
    private static void validateUpdateTestRequest(List<ValidationResultInfo> errors,
                                                  TestRequestEntity testRequestEntity,
                                                  TestRequestEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(testRequestEntity.getId(), "id", errors);
        //check the meta required
        if (testRequestEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!testRequestEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the TestRequest since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        validateNotUpdatable(errors, testRequestEntity, originalEntity);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             TestRequestEntity testRequestEntity,
                                             TestRequestEntity originalEntity) {
        // state can't be updated
        ValidationUtils.validateNotUpdatable(testRequestEntity.getState(), originalEntity.getState(), "state", errors);
    }

    //validate create
    private static void validateCreateTestRequest(
            List<ValidationResultInfo> errors,
            TestRequestEntity testRequestEntity,
            TestRequestService testRequestService,
            ContextInfo contextInfo) {
        if (testRequestEntity.getId() != null) {
            try {
                testRequestService.getTestRequestById(testRequestEntity.getId(),
                        contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied to the create already exists"));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error("caught DoesNotExistException in TestRequestValidator ", ex);
                // This is ok because created id should be unique
            }
        }
    }

    //Validate Required
    private static void validateCommonRequired(TestRequestEntity testRequestEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testRequestEntity.getName(), "name", errors);
    }

    //Validate Common Unique
    private static void validateCommonUnique(TestRequestEntity testRequestEntity,
                                             String validationTypeKey,
                                             List<ValidationResultInfo> errors,
                                             ContextInfo contextInfo)
            throws OperationFailedException {
        // check unique field
    }

    //Validation For :Id
    private static void validateTestRequestEntityId(TestRequestEntity testRequestEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(testRequestEntity.getId(), "id", errors);
    }

    //Validation For :Name
    private static void validateTestRequestEntityName(TestRequestEntity testRequestEntity,
                                                      List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(testRequestEntity.getName(),
                "name",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(testRequestEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //trim all TestRequest field
    private static void trimTestRequest(TestRequestEntity testRequestEntity) {
        if (testRequestEntity.getId() != null) {
            testRequestEntity.setId(testRequestEntity.getId().trim());
        }
        if (testRequestEntity.getName() != null) {
            testRequestEntity.setName(testRequestEntity.getName().trim());
        }
        if (testRequestEntity.getDescription() != null) {
            testRequestEntity.setDescription(testRequestEntity.getDescription().trim());
        }
        if (testRequestEntity.getEvaluationVersionId() != null) {
            testRequestEntity.setEvaluationVersionId(testRequestEntity.getEvaluationVersionId().trim());
        }
        testRequestEntity.getTestRequestUrls().stream().forEach(testRequestUrlEntity -> {
            if (testRequestUrlEntity.getBaseUrl() != null) {
                testRequestUrlEntity.setBaseUrl(testRequestUrlEntity.getBaseUrl().trim());
            }
            if (testRequestUrlEntity.getUsername() != null) {
                testRequestUrlEntity.setUsername(testRequestUrlEntity.getUsername().trim());
            }
            if (testRequestUrlEntity.getPassword() != null) {
                testRequestUrlEntity.setPassword(testRequestUrlEntity.getPassword().trim());
            }
            if (testRequestUrlEntity.getFhirVersion() != null) {
                testRequestUrlEntity.setFhirVersion(testRequestUrlEntity.getFhirVersion().trim());
            }
        });
    }

    @Autowired
    public void setRefObjectUriAndRefIdValidator(RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidatorIdValidator) {
        TestRequestValidator.refObjectUriAndRefIdValidator = refObjectUriAndRefIdValidatorIdValidator;
    }
}