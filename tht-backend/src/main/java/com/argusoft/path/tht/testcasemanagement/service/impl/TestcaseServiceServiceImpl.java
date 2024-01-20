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
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseRepository;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.validator.TestcaseValidator;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * This TestcaseServiceServiceImpl contains implementation for Testcase service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "TestcaseServiceServiceImpl")
public class TestcaseServiceServiceImpl implements TestcaseService {

    @Autowired
    TestcaseRepository testcaseRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpecificationService specificationService;


    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "createTestcase")
    public TestcaseEntity createTestcase(TestcaseEntity testcaseEntity,
                                         ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseValidator.validateCreateUpdateTestCase(Constant.CREATE_VALIDATION,
                testcaseEntity,
                this,
                specificationService,
                applicationContext,
                contextInfo);

        if (StringUtils.isEmpty(testcaseEntity.getId())) {
            testcaseEntity.setId(UUID.randomUUID().toString());
        }
        testcaseEntity = testcaseRepository.save(testcaseEntity);
        return testcaseEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "updateTestcase")
    public TestcaseEntity updateTestcase(TestcaseEntity testcaseEntity,
                                         ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseValidator.validateCreateUpdateTestCase(Constant.UPDATE_VALIDATION,
                testcaseEntity,
                this,
                specificationService,
                applicationContext,
                contextInfo);

        Optional<TestcaseEntity> testcaseOptional
                = testcaseRepository.findById(testcaseEntity.getId());
        testcaseEntity = testcaseRepository.save(testcaseEntity);
        return testcaseEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchTestcases")
    public Page<TestcaseEntity> searchTestcases(
            TestcaseCriteriaSearchFilter testcaseSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseEntity> testcaseEntitySpecification = testcaseSearchFilter.buildSpecification();
        return this.testcaseRepository.findAll(testcaseEntitySpecification,pageable);
    }


    @Override
    @Timed(name = "searchTestcases")
    public List<TestcaseEntity> searchTestcases(
            TestcaseCriteriaSearchFilter testcaseSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseEntity> testcaseEntitySpecification = testcaseSearchFilter.buildSpecification();
        return this.testcaseRepository.findAll(testcaseEntitySpecification);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getTestcaseById")
    public TestcaseEntity getTestcaseById(String testcaseId,
                                          ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(testcaseId)) {
            throw new InvalidParameterException("TestcaseId is missing");
        }
        Optional<TestcaseEntity> TestcaseOptional
                = testcaseRepository.findById(testcaseId);
        if (!TestcaseOptional.isPresent()) {
            throw new DoesNotExistException("Testcase by id :"
                    + testcaseId
                    + Constant.NOT_FOUND);
        }
        return TestcaseOptional.get();
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getTestcases")
    public Page<TestcaseEntity> getTestcases(Pageable pageable,
                                             ContextInfo contextInfo)
            throws InvalidParameterException {
        if (pageable == null) {
            throw new InvalidParameterException("pageble is missing");
        }
        Page<TestcaseEntity> testcases = testcaseRepository.findTestcases(pageable);
        return testcases;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    @Timed(name = "validateTestcase")
    public List<ValidationResultInfo> validateTestcase(
            String validationTypeKey,
            TestcaseEntity testcaseEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {

        List<ValidationResultInfo> errors = TestcaseValidator.validateTestCase(validationTypeKey, testcaseEntity, this, specificationService, applicationContext, contextInfo);
        return errors;
    }

}
