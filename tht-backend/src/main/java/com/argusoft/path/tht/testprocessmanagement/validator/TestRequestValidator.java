package com.argusoft.path.tht.testprocessmanagement.validator;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
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
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.ComponentValidator;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, TestRequestValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
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
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, TestRequestValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
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
        if (!StringUtils.hasLength(testRequestId)
                || StringUtils.isEmpty(refObjUri)
                || StringUtils.isEmpty(refId)
                || StringUtils.isEmpty(validationTypeKey)) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestRequestValidator.class.getSimpleName());
            throw new InvalidParameterException("inputData is missing");
        }
        List<ValidationResultInfo> errors = new ArrayList<>();

        refObjectUriAndRefIdValidator.refObjectUriAndRefIdValidation(refObjUri, refId, contextInfo, errors);
        if (!errors.isEmpty()) {
            return errors;
        }

        if (validationTypeKey.equals(Constant.START_PROCESS_VALIDATION)) {
            TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
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
            try {
                TestcaseResultEntity testcaseResult = testcaseResultService.getTestcaseResultStatus(testcaseResultEntities.get(0).getId(),
                        isManual,
                        isAutomated,
                        isRequired,
                        isRecommended,
                        isWorkflow,
                        isFunctional,
                        contextInfo);
                if (testcaseResult.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS)) {
                    String fieldName = "inputData";
                    errors.add(
                            new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    "Process for the requested input has been already started."));
                }
            } catch (DoesNotExistException ex) {
                String fieldName = "inputData";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Process for the requested input doesn't have active testcaseResults."));
            }
        } else if (validationTypeKey.equals(Constant.STOP_PROCESS_VALIDATION)) {

        } else if (validationTypeKey.equals(Constant.RESET_PROCESS_VALIDATION)) {

        }
        return errors;
    }

    public static List<ValidationResultInfo> validateTestRequest(String validationTypeKey,
                                                                 TestRequestEntity testRequestEntity,
                                                                 TestRequestService testRequestService,
                                                                 UserService userService,
                                                                 ComponentService componentService,
                                                                 ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        if (!StringUtils.hasLength(validationTypeKey)) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestRequestValidator.class.getSimpleName());
            throw new InvalidParameterException(ValidateConstant.MISSING_VALIDATION_TYPE_KEY);
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        trimTestRequest(testRequestEntity);

        // check Common Required
        validateCommonRequired(testRequestEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(testRequestEntity, errors, userService, componentService, contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testRequestEntity.getId() != null) {
                    try {
                        TestRequestEntity originalEntity = testRequestService.getTestRequestById(testRequestEntity.getId(), contextInfo);
                        validateUpdateTestRequest(errors, testRequestEntity, originalEntity);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestRequestValidator.class.getSimpleName(), ex);
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
                validateCreateTestRequest(errors, testRequestEntity, testRequestService, contextInfo);
                break;
            default:
                LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestRequestValidator.class.getSimpleName());
                throw new InvalidParameterException(ValidateConstant.INVALID_VALIDATION_TYPE_KEY);
        }

        // For : Id
        validateTestRequestEntityId(testRequestEntity,
                errors);
        // For :Name
        validateTestRequestEntityName(testRequestEntity,
                errors);
        // For :Description
        validateTestRequestEntityDescription(testRequestEntity,
                errors);
        //For: testRequestUrl
        validateTestRequestEntityTestRequestUrl(testRequestEntity,
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
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestRequestValidator.class.getSimpleName(), ex);
                String fieldName = "approver";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
            }
        }
        if (testRequestEntity.getAssessee() != null) {
            String fieldName = "assessee";
            try {
                UserEntity userEntity = userService.getUserById(testRequestEntity.getAssessee().getId(), contextInfo);
                if (userEntity.getRoles().stream().anyMatch(roleEntity -> UserServiceConstants.ROLE_ID_ASSESSEE.equals(roleEntity.getId()))) {
                    if (userEntity.getState().equals("user.status.active")) {
                        testRequestEntity.setAssessee(userEntity);
                    } else {
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR,
                                        "The id supplied for the assessee does not have active state"));
                    }
                } else {
                    errors.add(
                            new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    "The id supplied for the assessee does not have assessee role"));
                }
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestRequestValidator.class.getSimpleName(), ex);
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
            }
        }
        if (!testRequestEntity.getTestRequestUrls().isEmpty()) {
            for (TestRequestUrlEntity testRequestUrlEntity : testRequestEntity.getTestRequestUrls()) {
                try {
                    if (testRequestUrlEntity.getComponent() != null) {
                        testRequestUrlEntity.setComponent(componentService.getComponentById(testRequestUrlEntity.getComponent().getId(), contextInfo));
                    }
                } catch (DoesNotExistException | InvalidParameterException ex) {
                    LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestRequestValidator.class.getSimpleName(), ex);
                    String fieldName = "testRequestUrls.component";
                    errors.add(
                            new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
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
                    fieldName + ValidateConstant.MUST_PROVIDED));
        }
        // check meta version id
        else if (!testRequestEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    ValidateConstant.SOMEONE_UPDATED + "TestRequest" + ValidateConstant.REFRESH_COPY));
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
                                ValidateConstant.ID_SUPPLIED + "create" + ValidateConstant.ALREADY_EXIST));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestRequestValidator.class.getSimpleName(), ex);
                // This is ok because created id should be unique
            }
        }
    }

    //Validate Required
    private static void validateCommonRequired(TestRequestEntity testRequestEntity,
                                               List<ValidationResultInfo> errors) {
        //check the app name required
        ValidationUtils.validateRequired(testRequestEntity.getName(), "name", errors);
        //check the app assessee required
        ValidationUtils.validateRequired(testRequestEntity.getAssessee(), "assessee", errors);
        //check the state required
        ValidationUtils.validateRequired(testRequestEntity.getState(), "state", errors);

        //check at least one component/testRequestUrl required
        Set<TestRequestUrlEntity> urlEntitySet = testRequestEntity.getTestRequestUrls();
        ValidationUtils.validateRequired(urlEntitySet, "test request url", errors);

        //loop to check email and password not null in testrequest url
        for (TestRequestUrlEntity entity : urlEntitySet) {
            //check for username
            ValidationUtils.validateRequired(entity.getUsername(), "username", errors);
            //check for password
            ValidationUtils.validateRequired(entity.getPassword(), "password", errors);
            //check for baseurl
            ValidationUtils.validateRequired(entity.getFhirApiBaseUrl(), "fhirApiBaseUrl", errors);
        }
    }

    //Validation For :Id
    private static void validateTestRequestEntityId(TestRequestEntity testRequestEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testRequestEntity.getId(),
                "id",
                0,
                1000,
                errors);
    }

    //Validation For :Name
    private static void validateTestRequestEntityName(TestRequestEntity testRequestEntity,
                                                      List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testRequestEntity.getName(),
                "application name",
                3,
                1000,
                errors);
    }

    //Validation For :Desc
    private static void validateTestRequestEntityDescription(TestRequestEntity testRequestEntity,
                                                             List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testRequestEntity.getDescription(),
                "description",
                0,
                1000,
                errors);
    }


    //Validation For :TestRequestUrl
    private static void validateTestRequestEntityTestRequestUrl(TestRequestEntity testRequestEntity,
                                                                List<ValidationResultInfo> errors) {
        Set<TestRequestUrlEntity> urlEntitySet = testRequestEntity.getTestRequestUrls();
        //loop to check length of email , password, baseUrl in testrequest url
        for (TestRequestUrlEntity entity : urlEntitySet) {
            //check for username
            ValidationUtils
                    .validateLength(entity.getUsername(),
                            "username",
                            0,
                            255,
                            errors);
            //check for password
            ValidationUtils
                    .validateLength(entity.getPassword(),
                            "password",
                            0,
                            255,
                            errors);
            //check for baseurl
            ValidationUtils
                    .validateLength(entity.getFhirApiBaseUrl(),
                            "fhirApiBaseUrl",
                            0,
                            255,
                            errors);

            //check for websiteUIBAseUrl
            ValidationUtils
                    .validateLength(entity.getWebsiteUIBaseUrl(),
                            "websiteUiBaseUrl",
                            0,
                            255,
                            errors);
        }
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
        testRequestEntity.getTestRequestUrls().stream().forEach(testRequestUrlEntity -> {
            if (testRequestUrlEntity.getFhirApiBaseUrl() != null) {
                testRequestUrlEntity.setFhirApiBaseUrl(testRequestUrlEntity.getFhirApiBaseUrl().trim());
            }
            if (testRequestUrlEntity.getWebsiteUIBaseUrl() != null) {
                testRequestUrlEntity.setWebsiteUIBaseUrl(testRequestUrlEntity.getWebsiteUIBaseUrl().trim());
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

    public static void validateChangeState(TestRequestEntity testRequestEntity,
                                           String nextStateKey,
                                           ComponentService componentService,
                                           SpecificationService specificationService,
                                           TestcaseService testcaseService,
                                           TestcaseOptionService testcaseOptionService,
                                           List<ValidationResultInfo> errors,
                                           ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DoesNotExistException {
        if (TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED.equals(nextStateKey)) {

            Set<TestRequestUrlEntity> testRequestUrls = testRequestEntity.getTestRequestUrls();

            if (testRequestUrls.isEmpty()) {
                errors.add(new ValidationResultInfo("component", ErrorLevel.ERROR, "Components not found to test"));
            } else {
                for (TestRequestUrlEntity testRequestUrlEntity : testRequestUrls) {
                    if (!ComponentServiceConstants.COMPONENT_STATUS_ACTIVE.equals(testRequestUrlEntity.getComponent().getState())) {
                        errors.add(new ValidationResultInfo("component", ErrorLevel.WARN, "Component " + testRequestUrlEntity.getComponent().getName() + " will be skipped as it is inactive in Testcase Configuration. To activate this component, please contact administrator."));
                    } else {
                        List<TestcaseValidationResultInfo> validationResultEntities = ComponentValidator.validateTestCaseConfiguration(
                                ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                                testRequestUrlEntity.getComponent().getId(),
                                componentService,
                                specificationService,
                                testcaseService,
                                testcaseOptionService,
                                contextInfo);


                        if (containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
                            errors.add(
                                    new ValidationResultInfo(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                                            ErrorLevel.ERROR,
                                            "Testcase configuration for Component " + testRequestUrlEntity.getComponent().getName() + " are invalid. Please contact administrator."));
                        }
                    }
                }
                TestRequestValidator.validateBaseFhirUrl(testRequestEntity, errors);
            }
        }
    }

    public static void validateBaseFhirUrl(TestRequestEntity testRequestEntity, List<ValidationResultInfo> errors) throws InvalidParameterException, DoesNotExistException {
        Set<TestRequestUrlEntity> urlEntitySet = testRequestEntity.getTestRequestUrls();
        for (TestRequestUrlEntity entity : urlEntitySet) {
            //check for baseurl
            ValidationUtils.validateNotEmpty(entity.getFhirApiBaseUrl(), "fhirApiBaseUrl", errors);
        }
    }

    private static boolean containsErrors(
            List<TestcaseValidationResultInfo> errors,
            ErrorLevel errorLevel) {
        return hasValidationErrors(errors,
                errorLevel,
                new ArrayList<>());
    }

    private static boolean hasValidationErrors(
            List<TestcaseValidationResultInfo> validationResults,
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

    @Autowired
    public void setRefObjectUriAndRefIdValidator(RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidatorIdValidator) {
        TestRequestValidator.refObjectUriAndRefIdValidator = refObjectUriAndRefIdValidatorIdValidator;
    }
}