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
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.repository.SpecificationRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
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
 * This SpecificationServiceServiceImpl contains implementation for Specification service.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Service
public class SpecificationServiceServiceImpl implements SpecificationService {

    @Autowired
    SpecificationRepository SpecificationRepository;

    @Autowired
    TestcaseService testcaseService;

    @Autowired
    ComponentService componentService;

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public SpecificationEntity createSpecification(SpecificationEntity specificationEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        List<ValidationResultInfo> validationResultEntities
                = this.validateSpecification(Constant.CREATE_VALIDATION,
                specificationEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
        if (StringUtils.isEmpty(specificationEntity.getId())) {
            specificationEntity.setId(UUID.randomUUID().toString());
        }
        specificationEntity = SpecificationRepository.save(specificationEntity);
        return specificationEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public SpecificationEntity updateSpecification(SpecificationEntity specificationEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        List<ValidationResultInfo> validationResultEntitys
                = this.validateSpecification(Constant.UPDATE_VALIDATION,
                specificationEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
        Optional<SpecificationEntity> specificationOptional
                = SpecificationRepository.findById(specificationEntity.getId());
        specificationEntity = SpecificationRepository.save(specificationEntity);
        return specificationEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<SpecificationEntity> searchSpecifications(
            List<String> ids,
            SpecificationSearchFilter specificationSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException {

        if (!CollectionUtils.isEmpty(ids)) {
            return this.searchSpecificationsById(ids, pageable);
        } else {
            return this.searchSpecifications(specificationSearchFilter, pageable);
        }
    }

    private Page<SpecificationEntity> searchSpecifications(
            SpecificationSearchFilter specificationSearchFilter,
            Pageable pageable)
            throws OperationFailedException {

        Page<SpecificationEntity> Specifications = SpecificationRepository.advanceSpecificationSearch(
                specificationSearchFilter,
                pageable);
        return Specifications;
    }

    private Page<SpecificationEntity> searchSpecificationsById(
            List<String> ids,
            Pageable pageable) {

        List<SpecificationEntity> specifications
                = SpecificationRepository.findSpecificationsByIds(ids);
        return new PageImpl<>(specifications,
                pageable,
                specifications.size());
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public SpecificationEntity getSpecificationById(String specificationId,
                                                    ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(specificationId)) {
            throw new InvalidParameterException("SpecificationId is missing");
        }
        Optional<SpecificationEntity> SpecificationOptional
                = SpecificationRepository.findById(specificationId);
        if (!SpecificationOptional.isPresent()) {
            throw new DoesNotExistException("Specification by id :"
                    + specificationId
                    + Constant.NOT_FOUND);
        }
        return SpecificationOptional.get();
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<SpecificationEntity> getSpecifications(Pageable pageable,
                                                       ContextInfo contextInfo)
            throws InvalidParameterException {
        if (pageable == null) {
            throw new InvalidParameterException("pageble is missing");
        }
        Page<SpecificationEntity> specifications = SpecificationRepository.findSpecifications(pageable);
        return specifications;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<ValidationResultInfo> validateSpecification(
            String validationTypeKey,
            SpecificationEntity specificationEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (specificationEntity == null) {
            throw new InvalidParameterException("specificationEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        SpecificationEntity originalEntity = null;
        trimSpecification(specificationEntity);

        // check Common Required
        this.validateCommonRequired(specificationEntity, errors);

        // check Common ForeignKey
        this.validateCommonForeignKey(specificationEntity, errors, contextInfo);

        // check Common Unique
        this.validateCommonUnique(specificationEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (specificationEntity.getId() != null) {
                    try {
                        originalEntity = this
                                .getSpecificationById(specificationEntity.getId(),
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

                this.validateUpdateSpecification(errors,
                        specificationEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                this.validateCreateSpecification(errors, specificationEntity, contextInfo);
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
        return errors;
    }

    protected void validateCommonForeignKey(SpecificationEntity specificationEntity,
                                            List<ValidationResultInfo> errors,
                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        Set<TestcaseEntity> testcaseEntitySet = new HashSet<>();
        specificationEntity.getTestcases().stream().forEach(item -> {
            try {
                testcaseEntitySet.add(testcaseService.getTestcaseById(item.getId(), contextInfo));
            } catch (DoesNotExistException | InvalidParameterException ex) {
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
                String fieldName = "component";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the component does not exists"));
            }
        }
    }

    //validate update
    protected void validateUpdateSpecification(List<ValidationResultInfo> errors,
                                               SpecificationEntity specificationEntity,
                                               SpecificationEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
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
        this.validateNotUpdatable(errors, specificationEntity, originalEntity);
    }

    //validate not update
    protected void validateNotUpdatable(List<ValidationResultInfo> errors,
                                        SpecificationEntity specificationEntity,
                                        SpecificationEntity originalEntity) {
    }

    //validate create
    protected void validateCreateSpecification(
            List<ValidationResultInfo> errors,
            SpecificationEntity specificationEntity,
            ContextInfo contextInfo) {
        if (specificationEntity.getId() != null) {
            try {
                this.getSpecificationById(specificationEntity.getId(),
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
    protected void validateCommonRequired(SpecificationEntity specificationEntity,
                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(specificationEntity.getName(), "name", errors);
        ValidationUtils.validateRequired(specificationEntity.getComponent(), "component", errors);
        ValidationUtils.validateRequired(specificationEntity.getOrder(), "order", errors);
    }

    //Validate Common Unique
    protected void validateCommonUnique(SpecificationEntity specificationEntity,
                                        String validationTypeKey,
                                        List<ValidationResultInfo> errors,
                                        ContextInfo contextInfo)
            throws OperationFailedException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || specificationEntity.getId() != null)
                && StringUtils.isEmpty(specificationEntity.getName())) {
            SpecificationSearchFilter searchFilter = new SpecificationSearchFilter();
            searchFilter.setName(specificationEntity.getName());
            Page<SpecificationEntity> specificationEntities = this
                    .searchSpecifications(
                            null,
                            searchFilter,
                            Constant.TWO_VALUE_PAGE,
                            contextInfo);

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
    protected void validateSpecificationEntityId(SpecificationEntity specificationEntity,
                                                 List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(specificationEntity.getId(), "id", errors);
    }

    //Validation For :Name
    protected void validateSpecificationEntityName(SpecificationEntity specificationEntity,
                                                   List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(specificationEntity.getName(),
                "name",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(specificationEntity.getName(),
                "name",
                3,
                255,
                errors);
    }

    //Validation For :Order
    protected void validateSpecificationEntityOrder(SpecificationEntity specificationEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(specificationEntity.getOrder(),
                "order",
                1,
                null,
                errors);
    }

    //Validation For :IsFunctional
    protected void validateSpecificationEntityIsFunctional(SpecificationEntity specificationEntity,
                                                           List<ValidationResultInfo> errors) {
    }

    //Validation For :ComponentId
    protected void validateSpecificationEntityComponentId(SpecificationEntity specificationEntity,
                                                          List<ValidationResultInfo> errors) {
    }

    //trim all Specification field
    protected void trimSpecification(SpecificationEntity SpecificationEntity) {
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
