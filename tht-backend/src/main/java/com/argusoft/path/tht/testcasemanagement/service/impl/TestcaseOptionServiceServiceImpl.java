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
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseOptionRepository;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.TestcaseOptionValidator;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Multimap;
import io.astefanutti.metrics.aspectj.Metrics;
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

import java.io.IOException;
import java.util.*;

/**
 * This TestcaseOptionServiceServiceImpl contains implementation for TestcaseOption service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "TestcaseOptionServiceServiceImpl")
public class TestcaseOptionServiceServiceImpl implements TestcaseOptionService {

    @Autowired
    TestcaseOptionRepository testcaseOptionRepository;

    @Autowired
    TestcaseService testcaseService;

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "createTestcaseOption")
    // @Caching(evict = {
    //         @CacheEvict(value = "searchTestcaseOptions", allEntries = true),
    //         @CacheEvict(value = "searchTestcaseOptionsList", allEntries = true),
    //         @CacheEvict(value = "getTestcaseOptions", allEntries = true)
    // })
    public TestcaseOptionEntity createTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseOptionValidator.validateCreateUpdateTestcaseOption(Constant.CREATE_VALIDATION,
                this,
                testcaseService,
                testcaseOptionEntity,
                contextInfo);

        if (StringUtils.isEmpty(testcaseOptionEntity.getId())) {
            testcaseOptionEntity.setId(UUID.randomUUID().toString());
        }
        testcaseOptionEntity.setState(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_DRAFT);
        testcaseOptionEntity = testcaseOptionRepository.saveAndFlush(testcaseOptionEntity);

        return testcaseOptionEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "updateTestcaseOption")
    // @Caching(
    //         evict = {
    //                 @CacheEvict(value = "searchTestcaseOptions", allEntries = true),
    //                 @CacheEvict(value = "searchTestcaseOptionsList", allEntries = true),
    //                 @CacheEvict(value = "getTestcaseOptions", allEntries = true)
    //         }, put = {
    //         @CachePut(value = "getTestcaseOptionById",
    //                 key = "#testcaseOptionEntity.getId()")
    // })
    public TestcaseOptionEntity updateTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseOptionValidator.validateCreateUpdateTestcaseOption(Constant.UPDATE_VALIDATION,
                this,
                testcaseService,
                testcaseOptionEntity,
                contextInfo);

        testcaseOptionEntity = testcaseOptionRepository.save(testcaseOptionEntity);
        return testcaseOptionEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchTestcaseOptions")
    // @Cacheable(value = "searchTestcaseOptions", key = "{ #testcaseOptionCriteriaSearchFilter, #pageable }")
    public Page<TestcaseOptionEntity> searchTestcaseOptions(
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseOptionEntity> testcaseOptionEntitySpecification = testcaseOptionCriteriaSearchFilter.buildSpecification(contextInfo);
        return testcaseOptionRepository.findAll(testcaseOptionEntitySpecification, pageable);
    }


    @Override
    @Timed(name = "searchTestcaseOptionsList")
    // @Cacheable(value = "searchTestcaseOptionsList", key = "#testcaseOptionCriteriaSearchFilter")
    public List<TestcaseOptionEntity> searchTestcaseOptions(
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseOptionEntity> testcaseOptionEntitySpecification = testcaseOptionCriteriaSearchFilter.buildSpecification(contextInfo);
        return testcaseOptionRepository.findAll(testcaseOptionEntitySpecification);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getTestcaseOptionById")
    // @Cacheable(value = "getTestcaseOptionById", key = "#testcaseOptionId")
    public TestcaseOptionEntity getTestcaseOptionById(String testcaseOptionId,
                                                      ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(testcaseOptionId)) {
            throw new InvalidParameterException("TestcaseOptionId is missing");
        }
        TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter = new TestcaseOptionCriteriaSearchFilter(testcaseOptionId);
        List<TestcaseOptionEntity> testcaseOptionEntities = this.searchTestcaseOptions(testcaseOptionCriteriaSearchFilter, contextInfo);
        return testcaseOptionEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("Testcase does not found with id : " + testcaseOptionId));
    }

    /**
     * {@inheritdoc}
     */
    @Override
    @Timed(name = "validateTestcaseOption")
    public List<ValidationResultInfo> validateTestcaseOption(
            String validationTypeKey,
            TestcaseOptionEntity testcaseOptionEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        List<ValidationResultInfo> errors = TestcaseOptionValidator.validateTestcaseOption(validationTypeKey, testcaseOptionEntity, this, testcaseService, contextInfo);
        return errors;
    }

    @Override
    @Timed(name = "changeState")
    // @Caching(
    //         evict = {
    //                 @CacheEvict(value = "searchTestcaseOptions", allEntries = true),
    //                 @CacheEvict(value = "searchTestcaseOptionsList", allEntries = true),
    //                 @CacheEvict(value = "getTestcaseOptions", allEntries = true)
    //         }, put = {
    //         @CachePut(value = "getTestcaseOptionById",
    //                 key = "#testcaseOptionId")
    // })
    public TestcaseOptionEntity changeState(String testcaseOptionId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        ValidationUtils.statusPresent(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS, stateKey, errors);

        TestcaseOptionEntity testcaseOptionEntity = this.getTestcaseOptionById(testcaseOptionId, contextInfo);

        String currentState = testcaseOptionEntity.getState();

        //validate transition
        ValidationUtils.transitionValid(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_MAP, currentState, stateKey, errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    errors);
        }

        testcaseOptionEntity.setState(stateKey);
        testcaseOptionEntity = testcaseOptionRepository.saveAndFlush(testcaseOptionEntity);

        return testcaseOptionEntity;
    }
}
