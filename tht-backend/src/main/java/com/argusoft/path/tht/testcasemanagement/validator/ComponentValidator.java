package com.argusoft.path.tht.testcasemanagement.validator;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComponentValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentValidator.class);

    public static void validateCreateUpdateComponent(String validationTypeKey, ComponentService componentService, SpecificationService specificationService, ComponentEntity componentEntity, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, OperationFailedException {
        List<ValidationResultInfo> validationResultEntities
                = validateComponent(validationTypeKey,
                componentEntity,
                componentService,
                specificationService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error(ValidateConstant.DATA_VALIDATION_EXCEPTION + ComponentValidator.class.getSimpleName());
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
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION+ ComponentValidator.class.getSimpleName());
            throw new InvalidParameterException(ValidateConstant.MISSING_VALIDATION_TYPE_KEY);
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        ComponentEntity originalEntity = null;
        trimComponent(componentEntity);

        // check Common Required
        validateCommonRequired(componentEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(componentEntity, errors, specificationService, contextInfo);

        // check Common Unique
        validateCommonUnique(componentEntity,
                validationTypeKey,
                errors,
                componentService,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (componentEntity.getId() != null) {
                    try {
                        originalEntity = componentService
                                .getComponentById(componentEntity.getId(),
                                        contextInfo);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + ComponentValidator.class.getSimpleName(), ex);
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR,ValidateConstant.ID_SUPPLIED+ "update" + ValidateConstant.DOES_NOT_EXIST));
                    }
                }

                if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
                    return errors;
                }

                validateUpdateComponent(errors,
                        componentEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateComponent(errors, componentEntity, componentService, contextInfo);
                break;
            default:
                LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION+ ComponentValidator.class.getSimpleName());
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
                                ValidateConstant.ID_SUPPLIED+ fieldName + ValidateConstant.DOES_NOT_EXIST));
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
        }
        // check meta version id
        else if (!componentEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    ValidateConstant.SOMEONE_UPDATED+ " Component " + ValidateConstant.REFRESH_COPY));
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
                                ValidateConstant.ID_SUPPLIED+ "create" + ValidateConstant.ALREADY_EXIST));
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
                                "Given Component"+ ValidateConstant.NAME_ALREADY_EXIST));
            }
        }
    }

    //Validation For :Id
    private static void validateComponentEntityId(ComponentEntity componentEntity,
                                                  List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(componentEntity.getId(), "id", errors);
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
    private static void validateComponentEntityRank(ComponentEntity componentEntity,
                                                     List<ValidationResultInfo> errors) {
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
    private static void trimComponent(ComponentEntity ComponentEntity) {
        if (ComponentEntity.getId() != null) {
            ComponentEntity.setId(ComponentEntity.getId().trim());
        }
        if (ComponentEntity.getName() != null) {
            ComponentEntity.setName(ComponentEntity.getName().trim());
        }
        if (ComponentEntity.getDescription() != null) {
            ComponentEntity.setDescription(ComponentEntity.getDescription().trim());
        }
    }
}

