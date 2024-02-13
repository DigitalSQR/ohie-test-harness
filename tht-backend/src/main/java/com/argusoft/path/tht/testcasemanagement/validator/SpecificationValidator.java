package com.argusoft.path.tht.testcasemanagement.validator;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpecificationValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(SpecificationValidator.class);

    public static void validateCreateUpdateSpecification(String validationTypeKey, SpecificationService specificationService, TestcaseService testcaseService, ComponentService componentService, SpecificationEntity specificationEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = validateSpecification(validationTypeKey,
                specificationEntity,
                specificationService,
                testcaseService,
                componentService,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error("caught DataValidationErrorException in SpecificationValidator ");
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }

    }

    public static List<ValidationResultInfo> validateSpecification(String validationTypeKey, SpecificationEntity specificationEntity, SpecificationService specificationService, TestcaseService testcaseService, ComponentService componentService, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        if (specificationEntity == null) {
            LOGGER.error("caught InvalidParameterException in SpecificationValidator ");
            throw new InvalidParameterException("specificationEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            LOGGER.error("caught InvalidParameterException in SpecificationValidator ");
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        SpecificationEntity originalEntity = null;
        trimSpecification(specificationEntity);

        // check Common Required
        validateCommonRequired(specificationEntity, errors);

        // check Common ForeignKey
        validateCommonForeignKey(specificationEntity, errors, testcaseService, componentService, contextInfo);

        // check Common Unique
        validateCommonUnique(specificationEntity,
                validationTypeKey,
                specificationService,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (specificationEntity.getId() != null) {
                    try {
                        originalEntity = specificationService
                                .getSpecificationById(specificationEntity.getId(),
                                        contextInfo);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error("caught DoesNotExistException in SpecificationValidator ", ex);
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

                validateUpdateSpecification(errors,
                        specificationEntity,
                        specificationService,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateSpecification(errors, specificationEntity, specificationService, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateSpecificationEntityId(specificationEntity,
                errors);
        // For :Name
        validateSpecificationEntityName(specificationEntity,
                errors);
        // For :Order
        validateSpecificationEntityOrder(specificationEntity,
                errors);
        // For :IsFunctional
        validateSpecificationEntityIsFunctional(specificationEntity,
                errors);
        // For :IsFunctional
        validateSpecificationEntityDesc(specificationEntity,
                errors);
        return errors;
    }


    private static void validateCommonForeignKey(SpecificationEntity specificationEntity,
                                                 List<ValidationResultInfo> errors,
                                                 TestcaseService testcaseService,
                                                 ComponentService componentService,
                                                 ContextInfo contextInfo) {
        Set<TestcaseEntity> testcaseEntitySet = new HashSet<>();
        specificationEntity.getTestcases().stream().forEach(item -> {
            try {
                testcaseEntitySet.add(testcaseService.getTestcaseById(item.getId(), contextInfo));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error("caught DoesNotExistException in SpecificationValidator ", ex);
                String fieldName = "testcase";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the testcase does not exists"));
            }
        });
        specificationEntity.setTestcases(testcaseEntitySet);

        if (specificationEntity.getComponent() != null) {
            try {
                specificationEntity.setComponent(
                        componentService.getComponentById(specificationEntity.getComponent().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error("caught DoesNotExistException in SpecificationValidator ", ex);
                String fieldName = "component";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the component does not exists"));
            }
        }
    }

    //validate update
    private static void validateUpdateSpecification(List<ValidationResultInfo> errors,
                                                    SpecificationEntity specificationEntity,
                                                    SpecificationService specificationService,
                                                    SpecificationEntity originalEntity) {
        // required validation
        ValidationUtils.validateRequired(specificationEntity.getId(), "id", errors);
        //check the meta required
        if (specificationEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!specificationEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the Specification since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        validateNotUpdatable(errors, specificationEntity, specificationService, originalEntity);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             SpecificationEntity specificationEntity,
                                             SpecificationService specificationService,
                                             SpecificationEntity originalEntity) {
        // state can't be updated
        ValidationUtils.validateNotUpdatable(specificationEntity.getState(), originalEntity.getState(), "state", errors);
    }

    //validate create
    private static void validateCreateSpecification(
            List<ValidationResultInfo> errors,
            SpecificationEntity specificationEntity,
            SpecificationService specificationService,
            ContextInfo contextInfo) {
        if (specificationEntity.getId() != null) {
            try {
                specificationService.getSpecificationById(specificationEntity.getId(),
                        contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied to the create already exists"));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error("caught DoesNotExistException in SpecificationValidator ", ex);
                // This is ok because created id should be unique
            }
        }
    }

    //Validate Required
    private static void validateCommonRequired(SpecificationEntity specificationEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(specificationEntity.getName(), "name", errors);
        ValidationUtils.validateRequired(specificationEntity.getComponent(), "component", errors);
    }

    //Validate Common Unique
    private static void validateCommonUnique(SpecificationEntity specificationEntity,
                                             String validationTypeKey,
                                             SpecificationService specificationService,
                                             List<ValidationResultInfo> errors,
                                             ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || specificationEntity.getId() != null)
                && StringUtils.isEmpty(specificationEntity.getName())) {

            SpecificationCriteriaSearchFilter specificationCriteriaSearchFilter = new SpecificationCriteriaSearchFilter();
            specificationCriteriaSearchFilter.setName(specificationEntity.getName());
            specificationCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<SpecificationEntity> specificationEntities = specificationService.searchSpecifications(specificationCriteriaSearchFilter, contextInfo);

            // if info found with same name than and not current id
            boolean flag
                    = specificationEntities.stream().anyMatch(c -> (validationTypeKey.equals(Constant.CREATE_VALIDATION)
                    || !c.getId().equals(specificationEntity.getId()))
            );
            if (flag) {
                String fieldName = "name";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Given Specification with same name already exists."));
            }
        }
    }

    //Validation For :Id
    private static void validateSpecificationEntityId(SpecificationEntity specificationEntity,
                                                      List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(specificationEntity.getId(), "id", errors);
        ValidationUtils.validateLength(specificationEntity.getId(),
                "id",
                0,
                255,
                errors);
    }

    //Validation For :Name
    private static void validateSpecificationEntityName(SpecificationEntity specificationEntity,
                                                        List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(specificationEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //Validation For :Order
    private static void validateSpecificationEntityOrder(SpecificationEntity specificationEntity,
                                                         List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(specificationEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For :IsFunctional
    private static void validateSpecificationEntityIsFunctional(SpecificationEntity specificationEntity,
                                                                List<ValidationResultInfo> errors) {
    }

    //Validation For :ComponentId
    private static void validateSpecificationEntityComponentId(SpecificationEntity specificationEntity,
                                                               List<ValidationResultInfo> errors) {

    }

    //Validation for desc
    private static void validateSpecificationEntityDesc(SpecificationEntity specificationEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(specificationEntity.getDescription(),
                "description",
                0,
                1000,
                errors);
    }

    //trim all Specification field
    private static void trimSpecification(SpecificationEntity SpecificationEntity) {
        if (SpecificationEntity.getId() != null) {
            SpecificationEntity.setId(SpecificationEntity.getId().trim());
        }
        if (SpecificationEntity.getName() != null) {
            SpecificationEntity.setName(SpecificationEntity.getName().trim());
        }
        if (SpecificationEntity.getDescription() != null) {
            SpecificationEntity.setDescription(SpecificationEntity.getDescription().trim());
        }
    }


}

