package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.utils.CommonStateChangeValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.repository.SpecificationRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.SpecificationValidator;
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
 * This SpecificationServiceServiceImpl contains implementation for
 * Specification service.
 *
 * @author Dhruv
 */
@Service
public class SpecificationServiceServiceImpl implements SpecificationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SpecificationServiceServiceImpl.class);

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
    public SpecificationEntity createSpecification(SpecificationEntity specificationEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (specificationEntity == null) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + SpecificationServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("specificationEntity is missing");
        }

        defaultValueCreateSpecification(specificationEntity, contextInfo);

        SpecificationValidator.validateCreateUpdateSpecification(Constant.CREATE_VALIDATION,
                this,
                testcaseService,
                componentService,
                specificationEntity,
                contextInfo);

        specificationEntity = specificationRepository.saveAndFlush(specificationEntity);
        return specificationEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public SpecificationEntity updateSpecification(SpecificationEntity specificationEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (specificationEntity == null) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + SpecificationServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("specificationEntity is missing");
        }

        SpecificationValidator.validateCreateUpdateSpecification(Constant.UPDATE_VALIDATION,
                this,
                testcaseService,
                componentService,
                specificationEntity,
                contextInfo);

        specificationEntity = specificationRepository.saveAndFlush(specificationEntity);
        return specificationEntity;
    }


    @Override
    public SpecificationEntity changeRank(String specificationId, Integer rank, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        SpecificationEntity specificationEntity = this.getSpecificationById(specificationId, contextInfo);
        Integer oldRank = specificationEntity.getRank();

        specificationEntity.setRank(rank);
        ValidationUtils.validateRequired(rank, "rank", errors);
        SpecificationValidator.validateSpecificationEntityOrder(specificationEntity,errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    errors);
        }

        specificationRepository.saveAndFlush(specificationEntity);

        SpecificationCriteriaSearchFilter specificationCriteriaSearchFilter = new SpecificationCriteriaSearchFilter();
        specificationCriteriaSearchFilter.setComponentId(specificationEntity.getComponent().getId());
        specificationCriteriaSearchFilter.setMinRank(Integer.min(oldRank, rank));
        specificationCriteriaSearchFilter.setMaxRank(Integer.max(oldRank, rank));
        List<SpecificationEntity> specifications = this.searchSpecifications(specificationCriteriaSearchFilter, contextInfo);

        for(SpecificationEntity currentSpecification : specifications){
            int specificationRank = currentSpecification.getRank();
            if(!currentSpecification.getId().equals(specificationEntity.getId())) {
                if (oldRank > specificationRank && specificationRank >= rank) {
                    currentSpecification.setRank(specificationRank + 1);
                } else {
                    if (rank >= specificationRank && specificationRank > oldRank) {
                        currentSpecification.setRank(specificationRank - 1);
                    }
                }
                specificationRepository.saveAndFlush(currentSpecification);
            }
        }
        return specificationEntity;
    }


    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<SpecificationEntity> searchSpecifications(
            SpecificationCriteriaSearchFilter specificationSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<SpecificationEntity> specificationEntitySpecification = specificationSearchFilter.buildSpecification(contextInfo);
        return specificationRepository.findAll(specificationEntitySpecification, pageable);
    }

    @Override
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
    public SpecificationEntity getSpecificationById(String specificationId,
            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (!StringUtils.hasLength(specificationId)) {
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
     */
    @Override
    public List<ValidationResultInfo> validateSpecification(
            String validationTypeKey,
            SpecificationEntity specificationEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (specificationEntity == null) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + SpecificationServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("specificationEntity is missing");
        }
        List<ValidationResultInfo> errors = SpecificationValidator.validateSpecification(validationTypeKey, specificationEntity, this, testcaseService, componentService, contextInfo);
        return errors;
    }

    @Override
    public SpecificationEntity changeState(String specificationId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {

        List<ValidationResultInfo> errors = new ArrayList<>();

        SpecificationEntity specificationEntity = this.getSpecificationById(specificationId, contextInfo);

        CommonStateChangeValidator.validateStateChange(SpecificationServiceConstants.SPECIFICATION_STATUS, SpecificationServiceConstants.SPECIFICATION_STATUS_MAP, specificationEntity.getState(), stateKey, errors);

        specificationEntity.setState(stateKey);
        specificationEntity = specificationRepository.saveAndFlush(specificationEntity);

        return specificationEntity;
    }

    private void defaultValueCreateSpecification(SpecificationEntity specificationEntity, ContextInfo contextInfo) throws InvalidParameterException {
        if (!StringUtils.hasLength(specificationEntity.getId())) {
            specificationEntity.setId(UUID.randomUUID().toString());
        }
        specificationEntity.setState(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE);
        SpecificationCriteriaSearchFilter searchFilter = new SpecificationCriteriaSearchFilter();

        specificationEntity.setRank(1);
        if (specificationEntity.getComponent() != null) {
            searchFilter.setComponentId(specificationEntity.getComponent().getId());
            List<SpecificationEntity> specifications = this.searchSpecifications(searchFilter, Constant.SINGLE_PAGE_SORT_BY_RANK, contextInfo).getContent();
            if (!specifications.isEmpty()) {
                specificationEntity.setRank(specifications.get(0).getRank() + 1);
            }
        }
    }
}
