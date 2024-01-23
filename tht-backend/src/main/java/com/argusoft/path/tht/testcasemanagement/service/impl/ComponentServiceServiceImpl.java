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
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.repository.ComponentRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.validator.ComponentValidator;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
    ComponentRepository componentRepository;

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

        ComponentValidator.validateCreateUpdateComponent(Constant.CREATE_VALIDATION,
                this,
                specificationService,
                componentEntity,
                contextInfo);

        if (StringUtils.isEmpty(componentEntity.getId())) {
            componentEntity.setId(UUID.randomUUID().toString());
        }
        componentEntity.setState("COMPONENT_STATUS_DRAFT");
        componentEntity = componentRepository.save(componentEntity);
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

        ComponentValidator.validateCreateUpdateComponent(Constant.UPDATE_VALIDATION,
                this,
                specificationService,
                componentEntity,
                contextInfo);

        componentEntity = componentRepository.save(componentEntity);
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
            ComponentCriteriaSearchFilter componentCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<ComponentEntity> componentEntitySpecification = componentCriteriaSearchFilter.buildSpecification(contextInfo);
        return this.componentRepository.findAll(componentEntitySpecification, pageable);
    }

    @Override
    @Timed(name = "searchComponents")
    public List<ComponentEntity> searchComponents(
            ComponentCriteriaSearchFilter componentCriteriaSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<ComponentEntity> componentEntitySpecification = componentCriteriaSearchFilter.buildSpecification(contextInfo);
        return this.componentRepository.findAll(componentEntitySpecification);
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
        ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter(componentId);
        List<ComponentEntity> componentEntities = this.searchComponents(componentCriteriaSearchFilter, contextInfo);
        return componentEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("component does not found with id : " + componentId));
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
        Page<ComponentEntity> components = componentRepository.findComponents(pageable);
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

        List<ValidationResultInfo> errors = ComponentValidator.validateComponent(validationTypeKey, componentEntity, this, specificationService, contextInfo);

        return errors;
    }

    @Override
    public ComponentEntity changeState(String componentID, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        ValidationUtils.statusPresent(ComponentServiceConstants.COMPONENT_STATUS,stateKey,errors);

        ComponentEntity componentEntity = this.getComponentById(componentID, contextInfo);

        String currentState = componentEntity.getState();

        //validate transition
        ValidationUtils.transitionValid(ComponentServiceConstants.COMPONENT_STATUS_MAP,currentState,stateKey,errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    errors);
        }

        componentEntity.setState(stateKey);
        componentEntity = componentRepository.save(componentEntity);

        return componentEntity;
    }


}


