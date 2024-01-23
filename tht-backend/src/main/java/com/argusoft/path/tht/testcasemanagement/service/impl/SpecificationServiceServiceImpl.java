/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.fileservice.constant.DocumentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.repository.SpecificationRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.SpecificationValidator;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This SpecificationServiceServiceImpl contains implementation for Specification service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "SpecificationServiceServiceImpl")
public class SpecificationServiceServiceImpl implements SpecificationService {

    @Autowired
    SpecificationRepository specificationRepository;

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
    @Timed(name = "createSpecification")
    public SpecificationEntity createSpecification(SpecificationEntity specificationEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        SpecificationValidator.validateCreateUpdateSpecification(Constant.CREATE_VALIDATION,
                this,
                testcaseService,
                componentService,
                specificationEntity,
                contextInfo);

        if (StringUtils.isEmpty(specificationEntity.getId())) {
            specificationEntity.setId(UUID.randomUUID().toString());
        }
        specificationEntity = specificationRepository.save(specificationEntity);
        return specificationEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "updateSpecification")
    public SpecificationEntity updateSpecification(SpecificationEntity specificationEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {


        SpecificationValidator.validateCreateUpdateSpecification(Constant.UPDATE_VALIDATION,
                this,
                testcaseService,
                componentService,
                specificationEntity,
                contextInfo);

        specificationEntity = specificationRepository.save(specificationEntity);
        return specificationEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchSpecifications")
    public Page<SpecificationEntity> searchSpecifications(
            SpecificationCriteriaSearchFilter specificationSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<SpecificationEntity> specificationEntitySpecification = specificationSearchFilter.buildSpecification(contextInfo);
        return specificationRepository.findAll(specificationEntitySpecification, pageable);
    }


    @Override
    @Timed(name = "searchSpecifications")
    public List<SpecificationEntity> searchSpecifications(
            SpecificationCriteriaSearchFilter specificationSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<SpecificationEntity> specificationEntitySpecification = specificationSearchFilter.buildSpecification(contextInfo);
        return specificationRepository.findAll(specificationEntitySpecification);
    }


    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getSpecificationById")
    public SpecificationEntity getSpecificationById(String specificationId,
                                                    ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(specificationId)) {
            throw new InvalidParameterException("SpecificationId is missing");
        }
        SpecificationCriteriaSearchFilter specificationCriteriaSearchFilter = new SpecificationCriteriaSearchFilter(specificationId);
        List<SpecificationEntity> specificationEntities = this.searchSpecifications(specificationCriteriaSearchFilter, contextInfo);
        return specificationEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("Specification does not found with id : " + specificationId));
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getSpecifications")
    public Page<SpecificationEntity> getSpecifications(Pageable pageable,
                                                       ContextInfo contextInfo)
            throws InvalidParameterException {
        if (pageable == null) {
            throw new InvalidParameterException("pageble is missing");
        }
        Page<SpecificationEntity> specifications = specificationRepository.findSpecifications(pageable);
        return specifications;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    @Timed(name = "validateSpecification")
    public List<ValidationResultInfo> validateSpecification(
            String validationTypeKey,
            SpecificationEntity specificationEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        List<ValidationResultInfo> errors = SpecificationValidator.validateSpecification(validationTypeKey, specificationEntity, this, testcaseService, componentService, contextInfo);
        return errors;
    }

    @Override
    public SpecificationEntity changeState(String specificationId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        ValidationUtils.statusPresent(SpecificationServiceConstants.SPECIFICATION_STATUS,stateKey,errors);

        SpecificationEntity specificationEntity = this.getSpecificationById(specificationId, contextInfo);

        String currentState = specificationEntity.getState();

        //validate transition
        ValidationUtils.transitionValid(SpecificationServiceConstants.SPECIFICATION_STATUS_MAP,currentState,stateKey,errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    errors);
        }

        specificationEntity.setState(stateKey);
        specificationEntity = specificationRepository.save(specificationEntity);

        return specificationEntity;
    }
}
