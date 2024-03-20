package com.argusoft.path.tht.testcasemanagement.validator;

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
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

public class ComponentValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentValidator.class);

    private ComponentValidator() {
    }

    public static void validateCreateUpdateComponent(String validationTypeKey, ComponentService componentService, SpecificationService specificationService, ComponentEntity componentEntity, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, OperationFailedException {
        List<ValidationResultInfo> validationResultEntities
                = validateComponent(validationTypeKey,
                componentEntity,
                componentService,
                specificationService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, ComponentValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    validationResultEntities);
        }
    }

    public static List<ValidationResultInfo> validateComponent(
            String validationTypeKey,
            ComponentEntity componentEntity,
            ComponentService componentService,
            SpecificationService specificationService,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {

        if (!StringUtils.hasLength(validationTypeKey)) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, ComponentValidator.class.getSimpleName());
            throw new InvalidParameterException(ValidateConstant.MISSING_VALIDATION_TYPE_KEY);
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        trimComponent(componentEntity);

        // check Common Required
        validateCommonRequired(componentEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(componentEntity, errors, specificationService, contextInfo);

        // check Common Unique
        validateCommonUnique(componentEntity, validationTypeKey, errors, componentService, contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (componentEntity.getId() != null) {
                    try {
                        ComponentEntity originalEntity = componentService.getComponentById(componentEntity.getId(), contextInfo);
                        validateUpdateComponent(errors, componentEntity, originalEntity);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + ComponentValidator.class.getSimpleName(), ex);
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR, ValidateConstant.ID_SUPPLIED + "update" + ValidateConstant.DOES_NOT_EXIST));
                        return errors;
                    }
                }
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateComponent(errors, componentEntity, componentService, contextInfo);
                break;
            default:
                LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, ComponentValidator.class.getSimpleName());
                throw new InvalidParameterException(ValidateConstant.INVALID_VALIDATION_TYPE_KEY);
        }

        // For : Id
        validateComponentEntityId(componentEntity,
                errors);
        // For :Name
        validateComponentEntityName(componentEntity,
                errors);
        // For :Desc
        validateComponentEntityDesc(componentEntity,
                errors);
        // For :Order
        validateComponentEntityRank(componentEntity,
                errors);
        return errors;
    }

    private static void validateCommonForeignKey(ComponentEntity componentEntity,
                                                 List<ValidationResultInfo> errors,
                                                 SpecificationService specificationService,
                                                 ContextInfo contextInfo) {
        //validate Component foreignKey.
        Set<SpecificationEntity> specificationEntitySet = new HashSet<>();
        componentEntity.getSpecifications().stream().forEach(item -> {
            try {
                specificationEntitySet.add(specificationService.getSpecificationById(item.getId(), contextInfo));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + ComponentValidator.class.getSimpleName(), ex);
                String fieldName = "specification";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
            }
        });
        componentEntity.setSpecifications(specificationEntitySet);
    }

    //validate update
    private static void validateUpdateComponent(List<ValidationResultInfo> errors,
                                                ComponentEntity componentEntity,
                                                ComponentEntity originalEntity) {
        // required validation
        ValidationUtils.validateRequired(componentEntity.getId(), "id", errors);
        //check the meta required
        if (componentEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + ValidateConstant.MUST_PROVIDED));
        } // check meta version id
        else if (!componentEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    ValidateConstant.SOMEONE_UPDATED + " Component " + ValidateConstant.REFRESH_COPY));
        }
        // check not updatable fields
        validateNotUpdatable(errors, componentEntity, originalEntity);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             ComponentEntity componentEntity,
                                             ComponentEntity originalEntity) {
        // state can't be updated
        ValidationUtils.validateNotUpdatable(componentEntity.getState(), originalEntity.getState(), "state", errors);
    }

    //validate create
    private static void validateCreateComponent(
            List<ValidationResultInfo> errors,
            ComponentEntity componentEntity,
            ComponentService componentService,
            ContextInfo contextInfo) {
        if (componentEntity.getId() != null) {
            try {
                componentService.getComponentById(componentEntity.getId(),
                        contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + "create" + ValidateConstant.ALREADY_EXIST));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + ComponentValidator.class.getSimpleName(), ex);
                // This is ok because created id should be unique
            }
        }
    }

    //Validate Required
    private static void validateCommonRequired(ComponentEntity componentEntity,
                                               List<ValidationResultInfo> errors) {
        //check for name
        ValidationUtils
                .validateRequired(componentEntity.getName(), "name", errors);
        //check for description
        ValidationUtils
                .validateRequired(componentEntity.getDescription(), "description", errors);
        //check for state
        ValidationUtils
                .validateRequired(componentEntity.getState(), "state", errors);
        //check for rank
        ValidationUtils
                .validateRequired(componentEntity.getRank(), "rank", errors);
    }

    //Validate Common Unique
    private static void validateCommonUnique(ComponentEntity componentEntity,
                                             String validationTypeKey,
                                             List<ValidationResultInfo> errors,
                                             ComponentService componentService,
                                             ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || componentEntity.getId() != null)
                && StringUtils.hasLength(componentEntity.getName())) {

            ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();

            componentCriteriaSearchFilter.setName(componentEntity.getName());
            componentCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<ComponentEntity> componentEntities = componentService.searchComponents(componentCriteriaSearchFilter, contextInfo);

            // if info found with same name than and not current id
            boolean flag
                    = componentEntities.stream().anyMatch(c -> (validationTypeKey.equals(Constant.CREATE_VALIDATION)
                    || !c.getId().equals(componentEntity.getId()))
            );
            if (flag) {
                String fieldName = "name";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Given Component" + ValidateConstant.NAME_ALREADY_EXIST));
            }
        }
    }

    //Validation For :Id
    private static void validateComponentEntityId(ComponentEntity componentEntity,
                                                  List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(componentEntity.getId(),
                "id",
                0,
                255,
                errors);
    }

    //Validation For :Name
    private static void validateComponentEntityName(ComponentEntity componentEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(componentEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //Validation For :Order
    public static void validateComponentEntityRank(ComponentEntity componentEntity,
                                                   List<ValidationResultInfo> errors) {

        ValidationUtils.validateRequired(componentEntity.getRank(), "rank", errors);
        ValidationUtils.validateIntegerRange(componentEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation for desc
    private static void validateComponentEntityDesc(ComponentEntity componentEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(componentEntity.getDescription(),
                "description",
                0,
                1000,
                errors);
    }

    //trim all Component field
    private static void trimComponent(ComponentEntity componentEntity) {
        if (componentEntity.getId() != null) {
            componentEntity.setId(componentEntity.getId().trim());
        }
        if (componentEntity.getName() != null) {
            componentEntity.setName(componentEntity.getName().trim());
        }
        if (componentEntity.getDescription() != null) {
            componentEntity.setDescription(componentEntity.getDescription().trim());
        }
    }

    public static List<TestcaseValidationResultInfo> validateTestCaseConfiguration(
            String refObjUri,
            String refId,
            ComponentService componentService,
            SpecificationService specificationService,
            TestcaseService testcaseService,
            TestcaseOptionService testcaseOptionService,
            ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        // VALIDATE
        List<TestcaseValidationResultInfo> errors = new ArrayList<>();
        if (StringUtils.hasLength(refObjUri)) {
            if (ComponentServiceConstants.COMPONENT_REF_OBJ_URI.equals(refObjUri)) {
                try {
                    ComponentEntity componentEntity = componentService.getComponentById(refId, contextInfo);
                    validateComponent(componentEntity, componentService, specificationService, testcaseService, testcaseOptionService, errors, contextInfo);
                } catch (DoesNotExistException e) {
                    errors.add(
                            new TestcaseValidationResultInfo(ErrorLevel.ERROR, ComponentServiceConstants.COMPONENT_REF_OBJ_URI, "component",
                                    ValidateConstant.ID_SUPPLIED + " validate component" + ValidateConstant.DOES_NOT_EXIST, false));
                }
            } else if (SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI.equals(refObjUri)) {
                try {
                    SpecificationEntity specificationEntity = specificationService.getSpecificationById(refId, contextInfo);
                    validateSpecification(specificationEntity, testcaseOptionService, errors, contextInfo);
                } catch (DoesNotExistException e) {
                    errors.add(
                            new TestcaseValidationResultInfo(ErrorLevel.ERROR, SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI, "component",
                                    ValidateConstant.ID_SUPPLIED + " validate specification" + ValidateConstant.DOES_NOT_EXIST, false));
                }
            } else if (TestcaseServiceConstants.TESTCASE_REF_OBJ_URI.equals(refObjUri)) {
                try {
                    TestcaseEntity testcaseEntity = testcaseService.getTestcaseById(refId, contextInfo);
                    validateTestcase(testcaseEntity, testcaseOptionService, errors, contextInfo);
                } catch (DoesNotExistException e) {
                    errors.add(
                            new TestcaseValidationResultInfo(ErrorLevel.ERROR, TestcaseServiceConstants.TESTCASE_REF_OBJ_URI, "component",
                                    ValidateConstant.ID_SUPPLIED + " validate testcase" + ValidateConstant.DOES_NOT_EXIST, false));
                }
            }
        } else {
            ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
            List<ComponentEntity> componentEntities = componentService.searchComponents(componentCriteriaSearchFilter, contextInfo);
            for (ComponentEntity componentEntity : componentEntities) {
                validateComponent(componentEntity, componentService, specificationService, testcaseService, testcaseOptionService, errors, contextInfo);
            }
        }

        return errors;
    }

    private static void validateComponent(
            ComponentEntity componentEntity,
            ComponentService componentService,
            SpecificationService specificationService,
            TestcaseService testcaseService,
            TestcaseOptionService testcaseOptionService,
            List<TestcaseValidationResultInfo> errors,
            ContextInfo contextInfo
    ) throws InvalidParameterException, OperationFailedException {
        if (!ComponentServiceConstants.COMPONENT_STATUS_ACTIVE.equals(componentEntity.getState())) {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.WARN,
                            ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                            componentEntity.getName(),
                            "The component is inactive.",
                            false));
        } else if (componentEntity.getSpecifications().stream().noneMatch(specificationEntity -> SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE.equals(specificationEntity.getState()))) {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.ERROR,
                            ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                            componentEntity.getName(),
                            "There are no active specification for this component.",
                            false));
        } else {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.OK,
                            ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                            componentEntity.getName(),
                            null,
                            false));
            for (SpecificationEntity specificationEntity : componentEntity.getSpecifications()) {
                validateSpecification(specificationEntity, testcaseOptionService, errors, contextInfo);
            }
        }
    }

    private static void validateSpecification(
            SpecificationEntity specificationEntity,
            TestcaseOptionService testcaseOptionService,
            List<TestcaseValidationResultInfo> errors,
            ContextInfo contextInfo
    ) throws InvalidParameterException, OperationFailedException {
        if (!SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE.equals(specificationEntity.getState())) {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.WARN,
                            SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                            specificationEntity.getName(),
                            "The specification is inactive.",
                            false));
        } else if (specificationEntity.getTestcases().stream().noneMatch(testcaseEntity -> TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE.equals(testcaseEntity.getState()))) {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.ERROR,
                            SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                            specificationEntity.getName(),
                            "There are no active testcases for this specification.",
                            false));
        } else {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.OK,
                            SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                            specificationEntity.getName(),
                            null,
                            false));
            for (TestcaseEntity testcaseEntity : specificationEntity.getTestcases()) {
                validateTestcase(testcaseEntity, testcaseOptionService, errors, contextInfo);
            }
        }
    }

    private static void validateTestcase(
            TestcaseEntity testcaseEntity,
            TestcaseOptionService testcaseOptionService,
            List<TestcaseValidationResultInfo> errors,
            ContextInfo contextInfo
    ) throws InvalidParameterException, OperationFailedException {
        if (Boolean.FALSE.equals(testcaseEntity.getManual())) {
            return;
        }

        if (!TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE.equals(testcaseEntity.getState())) {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.WARN,
                            TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                            testcaseEntity.getName(),
                            "The testcase is inactive",
                            false));
            return;
        }

        TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter = new TestcaseOptionCriteriaSearchFilter();
        testcaseOptionCriteriaSearchFilter.setTestcaseId(testcaseEntity.getId());
        testcaseOptionCriteriaSearchFilter.setState(Collections.singletonList(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_ACTIVE));
        List<TestcaseOptionEntity> testcaseOptionEntities = testcaseOptionService.searchTestcaseOptions(testcaseOptionCriteriaSearchFilter, contextInfo);

        if (testcaseOptionEntities.isEmpty()) {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.ERROR,
                            TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                            testcaseEntity.getName(),
                            "There are no active options available for the Testcase.",
                            true));
        } else if (testcaseOptionEntities.stream().noneMatch(TestcaseOptionEntity::getSuccess)) {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.ERROR,
                            TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                            testcaseEntity.getName(),
                            "The Testcase lacks any active options containing the correct answer, with all options being incorrect.",
                            false));
        } else {
            errors.add(
                    new TestcaseValidationResultInfo(
                            ErrorLevel.OK,
                            TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                            testcaseEntity.getName(),
                            null,
                            false));
        }
    }
}
