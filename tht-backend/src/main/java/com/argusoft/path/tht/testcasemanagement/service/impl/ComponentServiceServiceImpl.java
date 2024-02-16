package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.fileservice.constant.DocumentServiceConstants;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.repository.ComponentRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.validator.ComponentValidator;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Multimap;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.CachePut;
// import org.springframework.cache.annotation.Cacheable;
// import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * This ComponentServiceServiceImpl contains implementation for Component service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "ComponentServiceServiceImpl")
public class ComponentServiceServiceImpl implements ComponentService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentServiceServiceImpl.class);

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
    // @Caching(evict = {
    //         @CacheEvict(value = "searchComponents", allEntries = true),
    //         @CacheEvict(value = "searchComponentsList", allEntries = true),
    //         @CacheEvict(value = "getComponents", allEntries = true)
    // })
    public ComponentEntity createComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (componentEntity == null) {
            LOGGER.error("caught InvalidParameterException in ComponentServiceServiceImpl");
            throw new InvalidParameterException("componentEntity is missing");
        }

        defaultValueCreateComponent(componentEntity, contextInfo);

        ComponentValidator.validateCreateUpdateComponent(Constant.CREATE_VALIDATION,
                this,
                specificationService,
                componentEntity,
                contextInfo);

        componentEntity = componentRepository.saveAndFlush(componentEntity);
        return componentEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "updateComponent")
    // @Caching(
    //         evict = {
    //                 @CacheEvict(value = "searchComponents", allEntries = true),
    //                 @CacheEvict(value = "searchComponentsList", allEntries = true),
    //                 @CacheEvict(value = "getComponents", allEntries = true)
    //         }, put = {
    //         @CachePut(value = "getComponentById",
    //                 key = "#componentEntity.getId()")
    // })
    public ComponentEntity updateComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (componentEntity == null) {
            LOGGER.error("caught InvalidParameterException in ComponentServiceServiceImpl");
            throw new InvalidParameterException("componentEntity is missing");
        }

        ComponentValidator.validateCreateUpdateComponent(Constant.UPDATE_VALIDATION,
                this,
                specificationService,
                componentEntity,
                contextInfo);

        componentEntity = componentRepository.saveAndFlush(componentEntity);
        return componentEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchComponents")
    // @Cacheable(value = "searchComponents", key = "{ #componentCriteriaSearchFilter, #pageable }")
    public Page<ComponentEntity> searchComponents(
            ComponentCriteriaSearchFilter componentCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<ComponentEntity> componentEntitySpecification = componentCriteriaSearchFilter.buildSpecification(contextInfo);
        return this.componentRepository.findAll(componentEntitySpecification, pageable);
    }

    @Override
    @Timed(name = "searchComponentsList")
    // @Cacheable(value = "searchComponentsList", key = "#componentCriteriaSearchFilter")
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
    // @Cacheable(value = "getComponentById", key = "#componentId")
    public ComponentEntity getComponentById(String componentId,
                                            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (!StringUtils.hasLength(componentId)) {
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
     */
    @Override
    @Timed(name = "validateComponent")
    public List<ValidationResultInfo> validateComponent(
            String validationTypeKey,
            ComponentEntity componentEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {

        if (componentEntity == null) {
            LOGGER.error("caught InvalidParameterException in ComponentServiceServiceImpl");
            throw new InvalidParameterException("componentEntity is missing");
        }

        List<ValidationResultInfo> errors = ComponentValidator.validateComponent(validationTypeKey, componentEntity, this, specificationService, contextInfo);

        return errors;
    }

    @Override
    // @Timed(name = "changeState")
    // @Caching(
    //         evict = {
    //                 @CacheEvict(value = "searchComponents", allEntries = true),
    //                 @CacheEvict(value = "searchComponentsList", allEntries = true),
    //                 @CacheEvict(value = "getComponents", allEntries = true)
    //         }, put = {
    //         @CachePut(value = "getComponentById",
    //                 key = "#componentID")
    // })
    public ComponentEntity changeState(String componentID, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        ValidationUtils.statusPresent(ComponentServiceConstants.COMPONENT_STATUS, stateKey, errors);

        ComponentEntity componentEntity = this.getComponentById(componentID, contextInfo);

        //validate transition
        ValidationUtils.transitionValid(ComponentServiceConstants.COMPONENT_STATUS_MAP, componentEntity.getState(), stateKey, errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    errors);
        }

        componentEntity.setState(stateKey);
        componentEntity = componentRepository.saveAndFlush(componentEntity);

        return componentEntity;
    }

    private void defaultValueCreateComponent(ComponentEntity componentEntity, ContextInfo contextInfo) throws InvalidParameterException {
        if (!StringUtils.hasLength(componentEntity.getId())) {
            componentEntity.setId(UUID.randomUUID().toString());
        }
        componentEntity.setState(ComponentServiceConstants.COMPONENT_STATUS_DRAFT);

        ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
        componentEntity.setRank(1);
        List<ComponentEntity> components = this.searchComponents(componentCriteriaSearchFilter, Constant.SINGLE_PAGE_SORT_BY_RANK, contextInfo).getContent();
        if(!components.isEmpty()){
            componentEntity.setRank(components.get(0).getRank() + 1);
        }
    }
}

