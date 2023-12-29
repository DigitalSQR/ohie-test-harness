/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseRepository;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This TestcaseServiceServiceImpl contains implementation for Testcase service.
 *
 * @author Dhruv
 */
@Service
public class TestcaseServiceServiceImpl implements TestcaseService {

    @Autowired
    TestcaseRepository TestcaseRepository;

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
    @Transactional
    public TestcaseEntity createTestcase(TestcaseEntity testcaseEntity,
                                         ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        List<ValidationResultInfo> validationResultEntities
                = this.validateTestcase(Constant.CREATE_VALIDATION,
                testcaseEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
        if (StringUtils.isEmpty(testcaseEntity.getId())) {
            testcaseEntity.setId(UUID.randomUUID().toString());
        }
        testcaseEntity = TestcaseRepository.save(testcaseEntity);
        return testcaseEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public TestcaseEntity updateTestcase(TestcaseEntity testcaseEntity,
                                         ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        List<ValidationResultInfo> validationResultEntitys
                = this.validateTestcase(Constant.UPDATE_VALIDATION,
                testcaseEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
        Optional<TestcaseEntity> testcaseOptional
                = TestcaseRepository.findById(testcaseEntity.getId());
        testcaseEntity = TestcaseRepository.save(testcaseEntity);
        return testcaseEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<TestcaseEntity> searchTestcases(
            List<String> ids,
            TestcaseSearchFilter testcaseSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException {

        if (!CollectionUtils.isEmpty(ids)) {
            return this.searchTestcasesById(ids, pageable);
        } else {
            return this.searchTestcases(testcaseSearchFilter, pageable);
        }
    }

    private Page<TestcaseEntity> searchTestcases(
            TestcaseSearchFilter testcaseSearchFilter,
            Pageable pageable)
            throws OperationFailedException {

        Page<TestcaseEntity> Testcases = TestcaseRepository.advanceTestcaseSearch(
                testcaseSearchFilter,
                pageable);
        return Testcases;
    }

    private Page<TestcaseEntity> searchTestcasesById(
            List<String> ids,
            Pageable pageable) {

        List<TestcaseEntity> testcases
                = TestcaseRepository.findTestcasesByIds(ids);
        return new PageImpl<>(testcases,
                pageable,
                testcases.size());
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseEntity getTestcaseById(String testcaseId,
                                          ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(testcaseId)) {
            throw new InvalidParameterException("TestcaseId is missing");
        }
        Optional<TestcaseEntity> TestcaseOptional
                = TestcaseRepository.findById(testcaseId);
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
    public Page<TestcaseEntity> getTestcases(Pageable pageable,
                                             ContextInfo contextInfo)
            throws InvalidParameterException {
        if (pageable == null) {
            throw new InvalidParameterException("pageble is missing");
        }
        Page<TestcaseEntity> testcases = TestcaseRepository.findTestcases(pageable);
        return testcases;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<ValidationResultInfo> validateTestcase(
            String validationTypeKey,
            TestcaseEntity testcaseEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (testcaseEntity == null) {
            throw new InvalidParameterException("testcaseEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestcaseEntity originalEntity = null;
        trimTestcase(testcaseEntity);

        // check Common Required
        this.validateCommonRequired(testcaseEntity, errors);

        // check Common ForeignKey
        this.validateCommonForeignKey(testcaseEntity, errors, contextInfo);

        // check Common Unique
        this.validateCommonUnique(testcaseEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testcaseEntity.getId() != null) {
                    try {
                        originalEntity = this
                                .getTestcaseById(testcaseEntity.getId(),
                                        contextInfo);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR,
                                        "The id supplied to the update does not "
                                                + "exists"));
                    }
                }

                if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
                    return errors;
                }

                this.validateUpdateTestcase(errors,
                        testcaseEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                this.validateCreateTestcase(errors, testcaseEntity, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateTestcaseEntityId(testcaseEntity,
                errors);
        // For :Name
        validateTestcaseEntityName(testcaseEntity,
                errors);
        // For :ClassName
        validateTestcaseEntityClassName(testcaseEntity,
                errors);
        // For :Order
        validateTestcaseEntityOrder(testcaseEntity,
                errors);
        // For :IsManual
        validateTestcaseEntityIsManual(testcaseEntity,
                errors);
        // For :IsRequired
        validateTestcaseEntityIsRequired(testcaseEntity,
                errors);
        return errors;
    }

    protected void validateCommonForeignKey(TestcaseEntity testcaseEntity,
                                            List<ValidationResultInfo> errors,
                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        if (testcaseEntity.getSpecification() != null) {
            try {
                testcaseEntity.setSpecification(
                        specificationService.getSpecificationById(testcaseEntity.getSpecification().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "specification";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the specification does not exists"));
            }
        }
    }

    //validate update
    protected void validateUpdateTestcase(List<ValidationResultInfo> errors,
                                          TestcaseEntity testcaseEntity,
                                          TestcaseEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(testcaseEntity.getId(), "id", errors);
        //check the meta required
        if (testcaseEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!testcaseEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the Testcase since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        this.validateNotUpdatable(errors, testcaseEntity, originalEntity);
    }

    //validate not update
    protected void validateNotUpdatable(List<ValidationResultInfo> errors,
                                        TestcaseEntity testcaseEntity,
                                        TestcaseEntity originalEntity) {
        //className can't be updatable
        ValidationUtils.validateNotUpdatable(testcaseEntity.getBeanName(), originalEntity.getBeanName(), "beanName", errors);
    }

    //validate create
    protected void validateCreateTestcase(
            List<ValidationResultInfo> errors,
            TestcaseEntity testcaseEntity,
            ContextInfo contextInfo) {
        if (testcaseEntity.getId() != null) {
            try {
                this.getTestcaseById(testcaseEntity.getId(),
                        contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied to the create already exists"));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                // This is ok because created id should be unique
            }
        }
    }

    //Validate Required
    protected void validateCommonRequired(TestcaseEntity testcaseEntity,
                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testcaseEntity.getSpecification(), "specification", errors);
    }

    //Validate Common Unique
    protected void validateCommonUnique(TestcaseEntity testcaseEntity,
                                        String validationTypeKey,
                                        List<ValidationResultInfo> errors,
                                        ContextInfo contextInfo)
            throws OperationFailedException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || testcaseEntity.getId() != null)
                && StringUtils.isEmpty(testcaseEntity.getName())) {
            TestcaseSearchFilter searchFilter = new TestcaseSearchFilter();
            searchFilter.setName(testcaseEntity.getName());
            Page<TestcaseEntity> testcaseEntities = this
                    .searchTestcases(
                            null,
                            searchFilter,
                            Constant.TWO_VALUE_PAGE,
                            contextInfo);

            // if info found with same name than and not current id
            boolean flag
                    = testcaseEntities.stream().anyMatch(c -> (validationTypeKey.equals(Constant.CREATE_VALIDATION)
                    || !c.getId().equals(testcaseEntity.getId()))
            );
            if (flag) {
                String fieldName = "name";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Given Testcase with same name already exists."));
            }
        }
    }

    //Validation For :Id
    protected void validateTestcaseEntityId(TestcaseEntity testcaseEntity,
                                            List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(testcaseEntity.getId(), "id", errors);
    }

    //Validation For :Name
    protected void validateTestcaseEntityName(TestcaseEntity testcaseEntity,
                                              List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(testcaseEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //Validation For :ClassName
    protected void validateTestcaseEntityClassName(TestcaseEntity testcaseEntity,
                                                   List<ValidationResultInfo> errors) {
        try {
            TestCase cRTestCases = (TestCase) applicationContext.getBean(testcaseEntity.getBeanName());
        } catch (Exception e) {
            errors
                    .add(new ValidationResultInfo("beanName",
                            ErrorLevel.ERROR,
                            "beanName doesn't exist with " + testcaseEntity.getBeanName()));
        }
    }

    //Validation For :Order
    protected void validateTestcaseEntityOrder(TestcaseEntity testcaseEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(testcaseEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For :IsManual
    protected void validateTestcaseEntityIsManual(TestcaseEntity testcaseEntity,
                                                  List<ValidationResultInfo> errors) {
    }

    //Validation For :IsRequired
    protected void validateTestcaseEntityIsRequired(TestcaseEntity testcaseEntity,
                                                    List<ValidationResultInfo> errors) {
    }

    //trim all Testcase field
    protected void trimTestcase(TestcaseEntity TestcaseEntity) {
        if (TestcaseEntity.getId() != null) {
            TestcaseEntity.setId(TestcaseEntity.getId().trim());
        }
        if (TestcaseEntity.getName() != null) {
            TestcaseEntity.setName(TestcaseEntity.getName().trim());
        }
        if (TestcaseEntity.getDescription() != null) {
            TestcaseEntity.setDescription(TestcaseEntity.getDescription().trim());
        }
        if (TestcaseEntity.getBeanName() != null) {
            TestcaseEntity.setBeanName(TestcaseEntity.getBeanName().trim());
        }
    }
}
