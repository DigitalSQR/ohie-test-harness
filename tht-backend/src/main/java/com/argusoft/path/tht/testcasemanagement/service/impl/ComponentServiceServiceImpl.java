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
import com.argusoft.path.tht.testcasemanagement.filter.ComponentSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.repository.ComponentRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.validator.ComponentValidator;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * This ComponentServiceServiceImpl contains implementation for Component service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "ComponentServiceServiceImpl")
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
    @Timed(name = "createComponent")
    public ComponentEntity createComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        ComponentValidator.validateComponent(Constant.CREATE_VALIDATION,
                this,
                componentEntity,
                contextInfo);

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
    @Timed(name = "updateComponent")
    public ComponentEntity updateComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        ComponentValidator.validateComponent(Constant.UPDATE_VALIDATION,
                this,
                componentEntity,
                contextInfo);

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
    @Timed(name = "searchComponents")
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
    @Timed(name = "getComponentById")
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
    @Timed(name = "getComponents")
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
    @Timed(name = "validateComponent")
    public List<ValidationResultInfo> validateComponent(
            String validationTypeKey,
            ComponentEntity componentEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {

        List<ValidationResultInfo> errors = ComponentValidator.validateCreateUpdateComponent(validationTypeKey, componentEntity, this, specificationService, contextInfo);

        return errors;
    }

}
