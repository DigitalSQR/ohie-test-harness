/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.service.impl;

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
import com.argusoft.path.tht.testcasemanagement.repository.ComponentRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * This ComponentServiceServiceImpl contains implementation for Component service.
 *
 * @author Dhruv
 */
@Service
public class ComponentServiceServiceImpl implements ComponentService {

    @Autowired
    ComponentRepository ComponentRepository;

    @Autowired
    private SpecificationService specificationService;

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public ComponentEntity createComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        List<ValidationResultInfo> validationResultEntities
                = this.validateComponent(Constant.CREATE_VALIDATION,
                componentEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
        if (StringUtils.isEmpty(componentEntity.getId())) {
            componentEntity.setId(UUID.randomUUID().toString());
        }
        componentEntity = ComponentRepository.save(componentEntity);
        return componentEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public ComponentEntity updateComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        List<ValidationResultInfo> validationResultEntitys
                = this.validateComponent(Constant.UPDATE_VALIDATION,
                componentEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
        Optional<ComponentEntity> componentOptional
                = ComponentRepository.findById(componentEntity.getId());
        componentEntity = ComponentRepository.save(componentEntity);
        return componentEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<ComponentEntity> searchComponents(
            List<String> ids,
            ComponentSearchFilter componentSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException {

        if (!CollectionUtils.isEmpty(ids)) {
            return this.searchComponentsById(ids, pageable);
        } else {
            return this.searchComponents(componentSearchFilter, pageable);
        }
    }

    private Page<ComponentEntity> searchComponents(
            ComponentSearchFilter componentSearchFilter,
            Pageable pageable)
            throws OperationFailedException {

        Page<ComponentEntity> Components = ComponentRepository.advanceComponentSearch(
                componentSearchFilter,
                pageable);
        return Components;
    }

    private Page<ComponentEntity> searchComponentsById(
            List<String> ids,
            Pageable pageable) {

        List<ComponentEntity> components
                = ComponentRepository.findComponentsByIds(ids);
        return new PageImpl<>(components,
                pageable,
                components.size());
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public ComponentEntity getComponentById(String componentId,
                                            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(componentId)) {
            throw new InvalidParameterException("ComponentId is missing");
        }
        Optional<ComponentEntity> ComponentOptional
                = ComponentRepository.findById(componentId);
        if (!ComponentOptional.isPresent()) {
            throw new DoesNotExistException("Component by id :"
                    + componentId
                    + Constant.NOT_FOUND);
        }
        return ComponentOptional.get();
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<ComponentEntity> getComponents(Pageable pageable,
                                               ContextInfo contextInfo)
            throws InvalidParameterException {
        if (pageable == null) {
            throw new InvalidParameterException("pageble is missing");
        }
        Page<ComponentEntity> components = ComponentRepository.findComponents(pageable);
        return components;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<ValidationResultInfo> validateComponent(
            String validationTypeKey,
            ComponentEntity componentEntity,
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
        this.validateCommonRequired(componentEntity, errors);

        // check Common ForeignKey
        this.validateCommonForeignKey(componentEntity, errors, contextInfo);

        // check Common Unique
        this.validateCommonUnique(componentEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (componentEntity.getId() != null) {
                    try {
                        originalEntity = this
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

                this.validateUpdateComponent(errors,
                        componentEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                this.validateCreateComponent(errors, componentEntity, contextInfo);
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

    protected void validateCommonForeignKey(ComponentEntity componentEntity,
                                            List<ValidationResultInfo> errors,
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
    protected void validateUpdateComponent(List<ValidationResultInfo> errors,
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
        this.validateNotUpdatable(errors, componentEntity, originalEntity);
    }

    //validate not update
    protected void validateNotUpdatable(List<ValidationResultInfo> errors,
                                        ComponentEntity componentEntity,
                                        ComponentEntity originalEntity) {
    }

    //validate create
    protected void validateCreateComponent(
            List<ValidationResultInfo> errors,
            ComponentEntity componentEntity,
            ContextInfo contextInfo) {
        if (componentEntity.getId() != null) {
            try {
                this.getComponentById(componentEntity.getId(),
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
    protected void validateCommonRequired(ComponentEntity componentEntity,
                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(componentEntity.getName(), "name", errors);
    }

    //Validate Common Unique
    protected void validateCommonUnique(ComponentEntity componentEntity,
                                        String validationTypeKey,
                                        List<ValidationResultInfo> errors,
                                        ContextInfo contextInfo)
            throws OperationFailedException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || componentEntity.getId() != null)
                && StringUtils.isEmpty(componentEntity.getName())) {
            ComponentSearchFilter searchFilter = new ComponentSearchFilter();
            searchFilter.setName(componentEntity.getName());
            Page<ComponentEntity> componentEntities = this
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
    protected void validateComponentEntityId(ComponentEntity componentEntity,
                                             List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(componentEntity.getId(), "id", errors);
    }

    //Validation For :Name
    protected void validateComponentEntityName(ComponentEntity componentEntity,
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
    protected void validateComponentEntityOrder(ComponentEntity componentEntity,
                                                List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(componentEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For :Order
    protected void validateComponentEntity(ComponentEntity componentEntity,
                                           List<ValidationResultInfo> errors) {
    }

    //trim all Component field
    protected void trimComponent(ComponentEntity ComponentEntity) {
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
