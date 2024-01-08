/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationSearchFilter;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    @Timed(name = "createSpecification")
    public SpecificationEntity createSpecification(SpecificationEntity specificationEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        SpecificationValidator.validateSpecification(Constant.CREATE_VALIDATION,
                this,
                specificationEntity,
                contextInfo);

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
    @Timed(name = "updateSpecification")
    public SpecificationEntity updateSpecification(SpecificationEntity specificationEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {


        SpecificationValidator.validateSpecification(Constant.UPDATE_VALIDATION,
                this,
                specificationEntity,
                contextInfo);

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
    @Timed(name = "searchSpecifications")
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
    @Timed(name = "getSpecificationById")
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
    @Timed(name = "getSpecifications")
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
    @Timed(name = "validateSpecification")
    public List<ValidationResultInfo> validateSpecification(
            String validationTypeKey,
            SpecificationEntity specificationEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        List<ValidationResultInfo> errors = SpecificationValidator.validateCreateUpdateSpecification(validationTypeKey, specificationEntity, this, testcaseService, componentService, contextInfo);
        return errors;
    }
}
