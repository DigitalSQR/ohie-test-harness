package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.utils.CommonStateChangeValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.repository.ComponentRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.ComponentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This ComponentServiceServiceImpl contains implementation for Component
 * service.
 *
 * @author Dhruv
 */
@Service
public class ComponentServiceServiceImpl implements ComponentService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentServiceServiceImpl.class);

    ComponentRepository componentRepository;
    private SpecificationService specificationService;
    private TestcaseService testcaseService;
    private TestcaseOptionService testcaseOptionService;

    @Autowired
    public void setComponentRepository(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    @Autowired
    public void setSpecificationService(SpecificationService specificationService) {
        this.specificationService = specificationService;
    }

    @Autowired
    public void setTestcaseService(TestcaseService testcaseService) {
        this.testcaseService = testcaseService;
    }

    @Autowired
    public void setTestcaseOptionService(TestcaseOptionService testcaseOptionService) {
        this.testcaseOptionService = testcaseOptionService;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public ComponentEntity createComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (componentEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, ComponentServiceServiceImpl.class.getSimpleName());
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
    public ComponentEntity updateComponent(ComponentEntity componentEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (componentEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, ComponentServiceServiceImpl.class.getSimpleName());
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
    public Page<ComponentEntity> searchComponents(
            ComponentCriteriaSearchFilter componentCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<ComponentEntity> componentEntitySpecification = componentCriteriaSearchFilter.buildSpecification(contextInfo);
        return this.componentRepository.findAll(componentEntitySpecification, pageable);
    }

    @Override
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
    public List<ValidationResultInfo> validateComponent(
            String validationTypeKey,
            ComponentEntity componentEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {

        if (componentEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, ComponentServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("componentEntity is missing");
        }

        List<ValidationResultInfo> errors = ComponentValidator.validateComponent(validationTypeKey, componentEntity, this, specificationService, contextInfo);

        return errors;
    }

    @Override
    public ComponentEntity changeState(String componentID, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        ComponentEntity componentEntity = this.getComponentById(componentID, contextInfo);

        CommonStateChangeValidator.validateStateChange(ComponentServiceConstants.COMPONENT_STATUS, ComponentServiceConstants.COMPONENT_STATUS_MAP, componentEntity.getState(), stateKey, errors);

        componentEntity.setState(stateKey);
        componentEntity = componentRepository.saveAndFlush(componentEntity);

        return componentEntity;
    }

    @Override
    public ComponentEntity changeRank(String componentId, Integer rank, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        ComponentEntity componentEntity = this.getComponentById(componentId, contextInfo);
        Integer oldRank = componentEntity.getRank();

        componentEntity.setRank(rank);
        ComponentValidator.validateComponentEntityRank(componentEntity,errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    errors);
        }

        componentRepository.saveAndFlush(componentEntity);

        ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
        componentCriteriaSearchFilter.setMinRank(Integer.min(oldRank, rank));
        componentCriteriaSearchFilter.setMaxRank(Integer.max(oldRank, rank));
        List<ComponentEntity> components = this.searchComponents(componentCriteriaSearchFilter, contextInfo);


        for(ComponentEntity currentComponent : components){
            int componentRank = currentComponent.getRank();
            if(!currentComponent.getId().equals(componentEntity.getId())){
                if(oldRank > componentRank && componentRank >= rank){
                    currentComponent.setRank(componentRank + 1);
                }
                else {
                    if (rank >= componentRank && componentRank > oldRank) {
                        currentComponent.setRank(componentRank - 1);
                    }
                }
                componentRepository.saveAndFlush(currentComponent);
            }
        }
        return componentEntity;
    }

    @Override
    public List<TestcaseValidationResultInfo> validateTestCaseConfiguration(String refObjUri, String refId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        return ComponentValidator.validateTestCaseConfiguration(refObjUri, refId, this, specificationService, testcaseService, testcaseOptionService, contextInfo);
    }

    private void defaultValueCreateComponent(ComponentEntity componentEntity, ContextInfo contextInfo) throws InvalidParameterException {
        if (!StringUtils.hasLength(componentEntity.getId())) {
            componentEntity.setId(UUID.randomUUID().toString());
        }
        componentEntity.setState(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE);

        ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
        componentEntity.setRank(1);
        List<ComponentEntity> components = this.searchComponents(componentCriteriaSearchFilter, Constant.SINGLE_PAGE_SORT_BY_RANK, contextInfo).getContent();
        if (!components.isEmpty()) {
            componentEntity.setRank(components.get(0).getRank() + 1);
        }
    }
}
