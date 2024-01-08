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
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseOptionRepository;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.TestcaseOptionValidator;
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
 * This TestcaseOptionServiceServiceImpl contains implementation for TestcaseOption service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "TestcaseOptionServiceServiceImpl")
public class TestcaseOptionServiceServiceImpl implements TestcaseOptionService {

    @Autowired
    TestcaseOptionRepository TestcaseOptionRepository;

    @Autowired
    TestcaseService testcaseService;

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "createTestcaseOption")
    public TestcaseOptionEntity createTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseOptionValidator.validateTestcaseOption(Constant.CREATE_VALIDATION,
                this,
                testcaseOptionEntity,
                contextInfo);

        if (StringUtils.isEmpty(testcaseOptionEntity.getId())) {
            testcaseOptionEntity.setId(UUID.randomUUID().toString());
        }
        testcaseOptionEntity = TestcaseOptionRepository.save(testcaseOptionEntity);
        return testcaseOptionEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "updateTestcaseOption")
    public TestcaseOptionEntity updateTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseOptionValidator.validateTestcaseOption(Constant.UPDATE_VALIDATION,
                this,
                testcaseOptionEntity,
                contextInfo);

        Optional<TestcaseOptionEntity> testcaseOptionOptional
                = TestcaseOptionRepository.findById(testcaseOptionEntity.getId());
        testcaseOptionEntity = TestcaseOptionRepository.save(testcaseOptionEntity);
        return testcaseOptionEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchTestcaseOptions")
    public Page<TestcaseOptionEntity> searchTestcaseOptions(
            List<String> ids,
            TestcaseOptionSearchFilter testcaseOptionSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException {

        if (!CollectionUtils.isEmpty(ids)) {
            return this.searchTestcaseOptionsById(ids, pageable);
        } else {
            return this.searchTestcaseOptions(testcaseOptionSearchFilter, pageable);
        }
    }

    private Page<TestcaseOptionEntity> searchTestcaseOptions(
            TestcaseOptionSearchFilter testcaseOptionSearchFilter,
            Pageable pageable)
            throws OperationFailedException {

        Page<TestcaseOptionEntity> TestcaseOptions = TestcaseOptionRepository.advanceTestcaseOptionSearch(
                testcaseOptionSearchFilter,
                pageable);
        return TestcaseOptions;
    }

    private Page<TestcaseOptionEntity> searchTestcaseOptionsById(
            List<String> ids,
            Pageable pageable) {

        List<TestcaseOptionEntity> testcaseOptions
                = TestcaseOptionRepository.findTestcaseOptionsByIds(ids);
        return new PageImpl<>(testcaseOptions,
                pageable,
                testcaseOptions.size());
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getTestcaseOptionById")
    public TestcaseOptionEntity getTestcaseOptionById(String testcaseOptionId,
                                                      ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(testcaseOptionId)) {
            throw new InvalidParameterException("TestcaseOptionId is missing");
        }
        Optional<TestcaseOptionEntity> TestcaseOptionOptional
                = TestcaseOptionRepository.findById(testcaseOptionId);
        if (!TestcaseOptionOptional.isPresent()) {
            throw new DoesNotExistException("TestcaseOption by id :"
                    + testcaseOptionId
                    + Constant.NOT_FOUND);
        }
        return TestcaseOptionOptional.get();
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getTestcaseOptions")
    public Page<TestcaseOptionEntity> getTestcaseOptions(Pageable pageable,
                                                         ContextInfo contextInfo)
            throws InvalidParameterException {
        if (pageable == null) {
            throw new InvalidParameterException("pageble is missing");
        }
        Page<TestcaseOptionEntity> testcaseOptions = TestcaseOptionRepository.findTestcaseOptions(pageable);
        return testcaseOptions;
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
        List<ValidationResultInfo> errors = TestcaseOptionValidator.validateCreateUpdateTestcaseOption(validationTypeKey, testcaseOptionEntity, this, testcaseService, contextInfo);
        return errors;
    }


}
