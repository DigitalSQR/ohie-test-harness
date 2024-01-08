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
import com.argusoft.path.tht.testcasemanagement.filter.ComponentSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComponentValidator {


    public static void validateComponent(String validationTypeKey, ComponentService componentService, ComponentEntity componentEntity, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, OperationFailedException {
        List<ValidationResultInfo> validationResultEntities
                = componentService.validateComponent(validationTypeKey,
                componentEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
    }

    public static List<ValidationResultInfo> validateCreateUpdateComponent(
            String validationTypeKey,
            ComponentEntity componentEntity,
            ComponentService componentService,
            SpecificationService specificationService,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (componentEntity == null) {
            throw new InvalidParameterException("componentEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
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

                validateUpdateComponent(errors,
                        componentEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateComponent(errors, componentEntity, componentService, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateComponentEntityId(componentEntity,
                errors);
        // For :Name
        validateComponentEntityName(componentEntity,
                errors);
        // For :Order
        validateComponentEntityOrder(componentEntity,
                errors);
        // For :Order
        validateComponentEntityOrder(componentEntity,
                errors);
        return errors;
    }

    private static void validateCommonForeignKey(ComponentEntity componentEntity,
                                                 List<ValidationResultInfo> errors,
                                                 SpecificationService specificationService,
                                                 ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        //validate Component foreignKey.
        Set<SpecificationEntity> specificationEntitySet = new HashSet<>();
        componentEntity.getSpecifications().stream().forEach(item -> {
            try {
                specificationEntitySet.add(specificationService.getSpecificationById(item.getId(), contextInfo));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "specification";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the specification does not exists"));
            }
        });
        componentEntity.setSpecifications(specificationEntitySet);
    }

    //validate update
    private static void validateUpdateComponent(List<ValidationResultInfo> errors,
                                                ComponentEntity componentEntity,
                                                ComponentEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(componentEntity.getId(), "id", errors);
        //check the meta required
        if (componentEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!componentEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the Component since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        validateNotUpdatable(errors, componentEntity, originalEntity);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             ComponentEntity componentEntity,
                                             ComponentEntity originalEntity) {
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
                                "The id supplied to the create already exists"));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                // This is ok because created id should be unique
            }
        }
    }

    //Validate Required
    private static void validateCommonRequired(ComponentEntity componentEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(componentEntity.getName(), "name", errors);
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
                && StringUtils.isEmpty(componentEntity.getName())) {
            ComponentSearchFilter searchFilter = new ComponentSearchFilter();
            searchFilter.setName(componentEntity.getName());
            Page<ComponentEntity> componentEntities = componentService
                    .searchComponents(
                            null,
                            searchFilter,
                            Constant.TWO_VALUE_PAGE,
                            contextInfo);

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
                                "Given Component with same name already exists."));
            }
        }
    }

    //Validation For :Id
    private static void validateComponentEntityId(ComponentEntity componentEntity,
                                                  List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(componentEntity.getId(), "id", errors);
    }

    //Validation For :Name
    private static void validateComponentEntityName(ComponentEntity componentEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(componentEntity.getName(),
                "name",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(componentEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //Validation For :Order
    private static void validateComponentEntityOrder(ComponentEntity componentEntity,
                                                     List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(componentEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For :Order
    private static void validateComponentEntity(ComponentEntity componentEntity,
                                                List<ValidationResultInfo> errors) {
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
