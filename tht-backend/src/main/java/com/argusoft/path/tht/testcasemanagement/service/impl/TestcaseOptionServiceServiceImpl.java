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
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseOptionRepository;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.TestcaseOptionValidator;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        testcaseOptionEntity = testcaseOptionRepository.save(testcaseOptionEntity);
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

        TestcaseOptionValidator.validateCreateUpdateTestcaseOption(Constant.UPDATE_VALIDATION,
                this,
                testcaseService,
                testcaseOptionEntity,
                contextInfo);

        Optional<TestcaseOptionEntity> testcaseOptionOptional
                = testcaseOptionRepository.findById(testcaseOptionEntity.getId());
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
    public Page<TestcaseOptionEntity> searchTestcaseOptions(
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseOptionEntity> testcaseOptionEntitySpecification = testcaseOptionCriteriaSearchFilter.buildSpecification();
        return testcaseOptionRepository.findAll(testcaseOptionEntitySpecification, pageable);
    }


    @Override
    @Timed(name = "searchTestcaseOptions")
    public List<TestcaseOptionEntity> searchTestcaseOptions(
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseOptionEntity> testcaseOptionEntitySpecification = testcaseOptionCriteriaSearchFilter.buildSpecification();
        return testcaseOptionRepository.findAll(testcaseOptionEntitySpecification);
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
                = testcaseOptionRepository.findById(testcaseOptionId);
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
        Page<TestcaseOptionEntity> testcaseOptions = testcaseOptionRepository.findTestcaseOptions(pageable);
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
        List<ValidationResultInfo> errors = TestcaseOptionValidator.validateTestcaseOption(validationTypeKey, testcaseOptionEntity, this, testcaseService, contextInfo);
        return errors;
    }


}
