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
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseOptionRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * This TestcaseOptionServiceServiceImpl contains implementation for TestcaseOption service.
 *
 * @author Dhruv
 */
@Service
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
    @Transactional
    public TestcaseOptionEntity createTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        List<ValidationResultInfo> validationResultEntities
                = this.validateTestcaseOption(Constant.CREATE_VALIDATION,
                testcaseOptionEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
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
    @Transactional
    public TestcaseOptionEntity updateTestcaseOption(TestcaseOptionEntity testcaseOptionEntity,
                                                   ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        List<ValidationResultInfo> validationResultEntitys
                = this.validateTestcaseOption(Constant.UPDATE_VALIDATION,
                testcaseOptionEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
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
    public List<ValidationResultInfo> validateTestcaseOption(
            String validationTypeKey,
            TestcaseOptionEntity testcaseOptionEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (testcaseOptionEntity == null) {
            throw new InvalidParameterException("testcaseOptionEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestcaseOptionEntity originalEntity = null;
        trimTestcaseOption(testcaseOptionEntity);

        // check Common Required
        this.validateCommonRequired(testcaseOptionEntity, errors);

        // check Common ForeignKey
        this.validateCommonForeignKey(testcaseOptionEntity, errors, contextInfo);

        // check Common Unique
        this.validateCommonUnique(testcaseOptionEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testcaseOptionEntity.getId() != null) {
                    try {
                        originalEntity = this
                                .getTestcaseOptionById(testcaseOptionEntity.getId(),
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

                this.validateUpdateTestcaseOption(errors,
                        testcaseOptionEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                this.validateCreateTestcaseOption(errors, testcaseOptionEntity, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateTestcaseOptionEntityId(testcaseOptionEntity,
                errors);
        // For :Name
        validateTestcaseOptionEntityName(testcaseOptionEntity,
                errors);
        // For :Order
        validateTestcaseOptionEntityOrder(testcaseOptionEntity,
                errors);
        // For :IsFunctional
        validateTestcaseOptionEntityIsSuccess(testcaseOptionEntity,
                errors);
        return errors;
    }

    protected void validateCommonForeignKey(TestcaseOptionEntity testcaseOptionEntity,
                                            List<ValidationResultInfo> errors,
                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        Set<TestcaseEntity> testcaseEntitySet = new HashSet<>();

        if (testcaseOptionEntity.getTestcase() != null) {
            try {
                testcaseOptionEntity.setTestcase(
                        testcaseService.getTestcaseById(testcaseOptionEntity.getTestcase().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "testcase";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the testcase does not exists"));
            }
        }
    }

    //validate update
    protected void validateUpdateTestcaseOption(List<ValidationResultInfo> errors,
                                               TestcaseOptionEntity testcaseOptionEntity,
                                               TestcaseOptionEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(testcaseOptionEntity.getId(), "id", errors);
        //check the meta required
        if (testcaseOptionEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!testcaseOptionEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the TestcaseOption since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        this.validateNotUpdatable(errors, testcaseOptionEntity, originalEntity);
    }

    //validate not update
    protected void validateNotUpdatable(List<ValidationResultInfo> errors,
                                        TestcaseOptionEntity testcaseOptionEntity,
                                        TestcaseOptionEntity originalEntity) {
    }

    //validate create
    protected void validateCreateTestcaseOption(
            List<ValidationResultInfo> errors,
            TestcaseOptionEntity testcaseOptionEntity,
            ContextInfo contextInfo) {
        if (testcaseOptionEntity.getId() != null) {
            try {
                this.getTestcaseOptionById(testcaseOptionEntity.getId(),
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
    protected void validateCommonRequired(TestcaseOptionEntity testcaseOptionEntity,
                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testcaseOptionEntity.getName(), "name", errors);
        ValidationUtils.validateRequired(testcaseOptionEntity.getTestcase(), "component", errors);
    }

    //Validate Common Unique
    protected void validateCommonUnique(TestcaseOptionEntity testcaseOptionEntity,
                                        String validationTypeKey,
                                        List<ValidationResultInfo> errors,
                                        ContextInfo contextInfo)
            throws OperationFailedException {
        // check unique field
    }

    //Validation For :Id
    protected void validateTestcaseOptionEntityId(TestcaseOptionEntity testcaseOptionEntity,
                                                 List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(testcaseOptionEntity.getId(), "id", errors);
    }

    //Validation For :Name
    protected void validateTestcaseOptionEntityName(TestcaseOptionEntity testcaseOptionEntity,
                                                   List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(testcaseOptionEntity.getName(),
                "name",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(testcaseOptionEntity.getName(),
                "name",
                3,
                255,
                errors);
    }

    //Validation For :Order
    protected void validateTestcaseOptionEntityOrder(TestcaseOptionEntity testcaseOptionEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(testcaseOptionEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For :IsFunctional
    protected void validateTestcaseOptionEntityIsSuccess(TestcaseOptionEntity testcaseOptionEntity,
                                                           List<ValidationResultInfo> errors) {
    }

    //Validation For :ComponentId
    protected void validateTestcaseOptionEntityComponentId(TestcaseOptionEntity testcaseOptionEntity,
                                                          List<ValidationResultInfo> errors) {
    }

    //trim all TestcaseOption field
    protected void trimTestcaseOption(TestcaseOptionEntity TestcaseOptionEntity) {
        if (TestcaseOptionEntity.getId() != null) {
            TestcaseOptionEntity.setId(TestcaseOptionEntity.getId().trim());
        }
        if (TestcaseOptionEntity.getName() != null) {
            TestcaseOptionEntity.setName(TestcaseOptionEntity.getName().trim());
        }
        if (TestcaseOptionEntity.getDescription() != null) {
            TestcaseOptionEntity.setDescription(TestcaseOptionEntity.getDescription().trim());
        }
    }
}
