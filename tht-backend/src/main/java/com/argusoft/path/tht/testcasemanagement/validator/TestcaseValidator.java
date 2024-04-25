package com.argusoft.path.tht.testcasemanagement.validator;

import com.argusoft.path.tht.fileservice.constant.TestcaseEntityDocumentTypes;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestcaseValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseValidator.class);

    private TestcaseValidator() {
    }

    public static void validateCreateUpdateTestCase(String validationTypeKey, TestcaseEntity testcaseEntity, MultipartFile zipFileForAutomationTest, TestcaseService testcaseService, SpecificationService specificationService, ApplicationContext applicationContext, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = validateTestCase(validationTypeKey,
                testcaseEntity,
                zipFileForAutomationTest,
                testcaseService,
                specificationService,
                applicationContext,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, TestcaseValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    validationResultEntities);
        }

    }

    public static List<ValidationResultInfo> validateTestCase(String validationTypeKey,
                                                              TestcaseEntity testcaseEntity,
                                                              MultipartFile zipFileForAutomationTest,
                                                              TestcaseService testcaseService,
                                                              SpecificationService specificationService,
                                                              ApplicationContext applicationContext,
                                                              ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        if (!StringUtils.hasLength(validationTypeKey)) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseValidator.class.getSimpleName());
            throw new InvalidParameterException(ValidateConstant.MISSING_VALIDATION_TYPE_KEY);
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        trimTestcase(testcaseEntity);

        // check Common Required
        validateCommonRequired(testcaseEntity, zipFileForAutomationTest ,errors);

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
                        TestcaseEntity originalEntity = testcaseService.getTestcaseById(testcaseEntity.getId(), contextInfo);
                        validateUpdateTestcase(errors, testcaseEntity, originalEntity);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error("{}{}", ValidateConstant.DOES_NOT_EXIST_EXCEPTION, TestcaseValidator.class.getSimpleName());
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
                validateCreateTestcase(errors, zipFileForAutomationTest,testcaseEntity, testcaseService, contextInfo);
                break;
            default:
                LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseValidator.class.getSimpleName());
                throw new InvalidParameterException(ValidateConstant.INVALID_VALIDATION_TYPE_KEY);
        }

        // For : Id
        validateTestcaseEntityId(testcaseEntity,
                errors);
        // For :Name
        validateTestcaseEntityName(testcaseEntity,
                errors);

        // For :Name Regex
        validateTestcaseNameForAlphaNumeric(testcaseEntity.getName(), errors);
        // For :BeanName
        validateTestcaseEntityBeanName(testcaseEntity, applicationContext,
                errors);
        // For :testSuitId
        validateTestcaseEntityTestSuitId(testcaseEntity, applicationContext,
                errors);
        // For :Order
        validateTestcaseEntityOrder(testcaseEntity,
                errors);
        //For : description
        validateTestcaseEntityDescription(testcaseEntity,
                errors);
        //For : failureMessage
        validateTestcaseEntityFailureMessage(testcaseEntity,
                errors);

        return errors;

    }

    private static void validateTestcaseNameForAlphaNumeric(String testcaseEntityName, List<ValidationResultInfo> errors) {
        if(StringUtils.hasLength(testcaseEntityName) && testcaseEntityName.contains("~")){
            errors.add(new ValidationResultInfo("name",
                    ErrorLevel.ERROR,
                            "The name must not include the character '~'"));
        }
    }

    //validate update
    private static void validateUpdateTestcase(List<ValidationResultInfo> errors,
                                               TestcaseEntity testcaseEntity,
                                               TestcaseEntity originalEntity) {
        // required validation
        ValidationUtils.validateRequired(testcaseEntity.getId(), "id", errors);
        //check the meta required
        if (testcaseEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + ValidateConstant.MUST_PROVIDED));
        } // check meta version id
        else if (!testcaseEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    ValidateConstant.SOMEONE_UPDATED + "Test case" + ValidateConstant.REFRESH_COPY));
        }
        // check not updatable fields
        validateNotUpdatable(errors, testcaseEntity, originalEntity);
    }

    //validate create
    private static void validateCreateTestcase(
            List<ValidationResultInfo> errors,
            MultipartFile zipFileForAutomationTest,
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
                                ValidateConstant.ID_SUPPLIED + "create" + ValidateConstant.ALREADY_EXIST));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                // This is ok because created id should be unique
            }
        }

        if(TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENT_EU_TESTBED.equals(testcaseEntity.getTestcaseRunEnvironment()) &&
                zipFileForAutomationTest == null){
                errors.add(new ValidationResultInfo("testSuiteZip",
                        ErrorLevel.ERROR,
                        "Zip file for test suite " + ValidateConstant.MUST_PROVIDED));
        }
    }

    //Validate Required
    private static void validateCommonRequired(TestcaseEntity testcaseEntity,
                                               MultipartFile zipFileForAutomationTest,
                                               List<ValidationResultInfo> errors) {
        //check for specification
        ValidationUtils
                .validateRequired(testcaseEntity.getSpecification(), "specification", errors);

        if(Boolean.FALSE.equals(testcaseEntity.getManual())){
            //check for rank
            ValidationUtils
                    .validateRequired(testcaseEntity.getTestcaseRunEnvironment(), "testcaseRunEnvironment", errors);

            if(testcaseEntity.getTestcaseRunEnvironment()!=null
                    && !TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENTS.contains(testcaseEntity.getTestcaseRunEnvironment())){
                errors.add(new ValidationResultInfo("testcaseRunEnvironment",
                        ErrorLevel.ERROR,
                        "provided environment "+testcaseEntity.getTestcaseRunEnvironment()+" is out of expected scenario"));
            }
            else{
                if(TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENT_JAVA_THT.equals(testcaseEntity.getTestcaseRunEnvironment())){
                    ValidationUtils.validateRequired(testcaseEntity.getBeanName(), "beanName", errors);
                }
            }
        }
        else {
            ValidationUtils.validateRequired(testcaseEntity.getManual(), "manual", errors);
        }
        //check for rank
        ValidationUtils
                .validateRequired(testcaseEntity.getRank(), "rank", errors);
        //check for state
        ValidationUtils
                .validateRequired(testcaseEntity.getState(), "state", errors);
        //check for name
        ValidationUtils
                .validateRequired(testcaseEntity.getName(), "name", errors);
        //check for description
        ValidationUtils
                .validateRequired(testcaseEntity.getDescription(), "description", errors);

        ValidationUtils.validateFileType(zipFileForAutomationTest, TestcaseEntityDocumentTypes.TESTCASE_TESTSUITE_AUTOMATION_ZIP.getAllowedFileTypes(), errors);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             TestcaseEntity testcaseEntity,
                                             TestcaseEntity originalEntity) {
        // state can't be updated
        ValidationUtils.validateNotUpdatable(testcaseEntity.getState(), originalEntity.getState(), "state", errors);
        ValidationUtils.validateNotUpdatable(testcaseEntity.getTestSuiteId(), originalEntity.getTestSuiteId(), "testSuiteId", errors);
        ValidationUtils.validateNotUpdatable(testcaseEntity.getSutActorApiKey(), originalEntity.getSutActorApiKey(), "sutActorApiKey", errors);
    }

    //Validation For :Id
    private static void validateTestcaseEntityId(TestcaseEntity testcaseEntity,
                                                 List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseEntity.getId(),
                "id",
                0,
                255,
                errors);
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

    //Validation For :Description
    private static void validateTestcaseEntityDescription(TestcaseEntity testcaseEntity,
                                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseEntity.getDescription(),
                "description",
                0,
                1000,
                errors);
    }

    //Validation For :Failure Message
    private static void validateTestcaseEntityFailureMessage(TestcaseEntity testcaseEntity,
                                                             List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseEntity.getFailureMessage(),
                "failureMessage",
                0,
                2000,
                errors);
    }

    //Validation For :BeanName
    private static void validateTestcaseEntityBeanName(TestcaseEntity testcaseEntity,
                                                       ApplicationContext applicationContext,
                                                       List<ValidationResultInfo> errors) {
        if (!StringUtils.hasLength(testcaseEntity.getBeanName())) {
            return;
        }
        ValidationUtils.validateLength(testcaseEntity.getBeanName(),
                "beanName",
                0,
                255,
                errors);
        try {
            applicationContext.getBean(testcaseEntity.getBeanName());
        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + TestcaseValidator.class.getSimpleName(), e);
            errors
                    .add(new ValidationResultInfo("beanName",
                            ErrorLevel.ERROR,
                            "beanName doesn't exist with " + testcaseEntity.getBeanName()));
        }
    }

    //Validation For :TestSuitId
    private static void validateTestcaseEntityTestSuitId(TestcaseEntity testcaseEntity,
                                                       ApplicationContext applicationContext,
                                                       List<ValidationResultInfo> errors) {
        if (!StringUtils.hasLength(testcaseEntity.getBeanName())) {
            return;
        }
        ValidationUtils.validateLength(testcaseEntity.getBeanName(),
                "testSuitId",
                0,
                1300,
                errors);
        try {
            applicationContext.getBean(testcaseEntity.getBeanName());
        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + TestcaseValidator.class.getSimpleName(), e);
            errors
                    .add(new ValidationResultInfo("beanName",
                            ErrorLevel.ERROR,
                            "beanName doesn't exist with " + testcaseEntity.getBeanName()));
        }
    }

    //Validation For :Order
    public static void validateTestcaseEntityOrder(TestcaseEntity testcaseEntity,
                                                   List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testcaseEntity.getRank(), "rank", errors);
        ValidationUtils.validateIntegerRange(testcaseEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //trim all Testcase field
    private static void trimTestcase(TestcaseEntity testcaseEntity) {
        if (testcaseEntity.getId() != null) {
            testcaseEntity.setId(testcaseEntity.getId().trim());
        }
        if (testcaseEntity.getName() != null) {
            testcaseEntity.setName(testcaseEntity.getName().trim());
        }
        if (testcaseEntity.getDescription() != null) {
            testcaseEntity.setDescription(testcaseEntity.getDescription().trim());
        }
        if (testcaseEntity.getBeanName() != null) {
            testcaseEntity.setBeanName(testcaseEntity.getBeanName().trim());
        }
    }

    private static void validateCommonForeignKey(TestcaseEntity testcaseEntity,
                                                 List<ValidationResultInfo> errors,
                                                 SpecificationService specificationService,
                                                 ContextInfo contextInfo)
            throws InvalidParameterException {
        if (testcaseEntity.getSpecification() != null) {
            try {
                testcaseEntity.setSpecification(
                        specificationService.getSpecificationById(testcaseEntity.getSpecification().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + TestcaseValidator.class.getSimpleName(), ex);
                String fieldName = "specification";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
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
                && StringUtils.hasLength(testcaseEntity.getName())) {

            TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter = new TestcaseCriteriaSearchFilter();
            testcaseCriteriaSearchFilter.setName(testcaseEntity.getName());
            testcaseCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<TestcaseEntity> testcaseEntities = testcaseService.searchTestcases(testcaseCriteriaSearchFilter, contextInfo);

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
                                "Given Testcase" + ValidateConstant.NAME_ALREADY_EXIST));
            }
        }
    }

}
