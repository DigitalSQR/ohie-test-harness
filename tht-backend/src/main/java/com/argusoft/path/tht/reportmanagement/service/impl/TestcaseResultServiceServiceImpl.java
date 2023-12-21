/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultRepository;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.usermanagement.service.UserService;
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
 * This TestcaseResultServiceServiceImpl contains implementation for TestcaseResult service.
 *
 * @author Dhruv
 */
@Service
public class TestcaseResultServiceServiceImpl implements TestcaseResultService {

    @Autowired
    private TestcaseResultRepository testcaseResultRepository;

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private TestcaseOptionService testcaseOptionService;

    @Autowired
    private TestRequestService testRequestService;

    @Autowired
    private UserService userService;

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public TestcaseResultEntity createTestcaseResult(TestcaseResultEntity testcaseResultEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, VersionMismatchException {

        List<ValidationResultInfo> validationResultEntities
                = this.validateTestcaseResult(Constant.CREATE_VALIDATION,
                testcaseResultEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
        if (StringUtils.isEmpty(testcaseResultEntity.getId())) {
            testcaseResultEntity.setId(UUID.randomUUID().toString());
        }
        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);
        updateTestRequestByTestcaseResultState(testcaseResultEntity.getTestRequestId(), testcaseResultEntity.getState(), contextInfo);
        return testcaseResultEntity;
    }

    public void updateTestRequestByTestcaseResultState(String testRequestId, String testcaseResultState, ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException, DoesNotExistException, DataValidationErrorException, VersionMismatchException {
        if(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING.equals(testcaseResultState)) {
            //If result is pending then do not change anything.
            return;
        }
        TestRequestEntity testRequestEntity = testRequestService.getTestRequestById(testRequestId, contextInfo);
        //If result is in progress then make testRequest inProgress.
        if(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS.equals(testcaseResultState)) {
            if(!TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS.equals(testRequestEntity.getState())) {
                testRequestEntity.setState(TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS);
                testRequestService.updateTestRequest(testRequestEntity, contextInfo);
            }
            return;
        }
        //If all result are finished and testRequest is not finished then make it finished.
        if(!TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED.equals(testRequestEntity.getState())) {
            TestcaseResultSearchFilter searchFilter = new TestcaseResultSearchFilter(
                    null, SearchType.CONTAINING,
                    null, SearchType.CONTAINING,
                    null, null, null, testRequestId, null
            );
            List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(new ArrayList<>(), searchFilter, Constant.FULL_PAGE, contextInfo).getContent();
            Boolean testingFinished = testcaseResultEntities.stream().allMatch(testcaseResultEntity ->
                    TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PASSED.equals(testcaseResultEntity.getState())
                            || TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FAILED.equals(testcaseResultEntity.getState()));
            if(testingFinished) {
                testRequestEntity.setState(TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED);
                testRequestService.updateTestRequest(testRequestEntity, contextInfo);
            }
        }
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public TestcaseResultEntity updateTestcaseResult(TestcaseResultEntity testcaseResultEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, VersionMismatchException {

        List<ValidationResultInfo> validationResultEntitys
                = this.validateTestcaseResult(Constant.UPDATE_VALIDATION,
                testcaseResultEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
        Optional<TestcaseResultEntity> testcaseResultOptional
                = testcaseResultRepository.findById(testcaseResultEntity.getId());
        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);
        updateTestRequestByTestcaseResultState(testcaseResultEntity.getTestRequestId(), testcaseResultEntity.getState(), contextInfo);
        return testcaseResultEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<TestcaseResultEntity> searchTestcaseResults(
            List<String> ids,
            TestcaseResultSearchFilter testcaseResultSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException {

        if (!CollectionUtils.isEmpty(ids)) {
            return this.searchTestcaseResultsById(ids, pageable);
        } else {
            return this.searchTestcaseResults(testcaseResultSearchFilter, pageable);
        }
    }

    private Page<TestcaseResultEntity> searchTestcaseResults(
            TestcaseResultSearchFilter testcaseResultSearchFilter,
            Pageable pageable)
            throws OperationFailedException {

        Page<TestcaseResultEntity> testcaseResults = testcaseResultRepository.advanceTestcaseResultSearch(
                testcaseResultSearchFilter,
                pageable);
        return testcaseResults;
    }

    private Page<TestcaseResultEntity> searchTestcaseResultsById(
            List<String> ids,
            Pageable pageable) {

        List<TestcaseResultEntity> testcaseResults
                = testcaseResultRepository.findTestcaseResultsByIds(ids);
        return new PageImpl<>(testcaseResults,
                pageable,
                testcaseResults.size());
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseResultEntity getTestcaseResultById(String testcaseResultId,
                                                      ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(testcaseResultId)) {
            throw new InvalidParameterException("TestcaseResultId is missing");
        }
        Optional<TestcaseResultEntity> testcaseResultOptional
                = testcaseResultRepository.findById(testcaseResultId);
        if (!testcaseResultOptional.isPresent()) {
            throw new DoesNotExistException("TestcaseResult by id :"
                    + testcaseResultId
                    + Constant.NOT_FOUND);
        }
        return testcaseResultOptional.get();
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<TestcaseResultEntity> getTestcaseResults(Pageable pageable,
                                                         ContextInfo contextInfo)
            throws InvalidParameterException {
        if (pageable == null) {
            throw new InvalidParameterException("pageble is missing");
        }
        Page<TestcaseResultEntity> testcaseResults = testcaseResultRepository.findTestcaseResults(pageable);
        return testcaseResults;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<ValidationResultInfo> validateTestcaseResult(
            String validationTypeKey,
            TestcaseResultEntity testcaseResultEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (testcaseResultEntity == null) {
            throw new InvalidParameterException("TestcaseResultEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestcaseResultEntity originalEntity = null;
        trimTestcaseResult(testcaseResultEntity);

        // check Common Required
        this.validateCommonRequired(testcaseResultEntity, errors);

        // check Common ForeignKey
        this.validateCommonForeignKey(testcaseResultEntity, errors, contextInfo);

        // check Common Unique
        this.validateCommonUnique(testcaseResultEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testcaseResultEntity.getId() != null) {
                    try {
                        originalEntity = this
                                .getTestcaseResultById(testcaseResultEntity.getId(),
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

                this.validateUpdateTestcaseResult(errors,
                        testcaseResultEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                this.validateCreateTestcaseResult(errors, testcaseResultEntity, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateTestcaseResultEntityId(testcaseResultEntity,
                errors);
        // For :Name
        validateTestcaseResultEntityName(testcaseResultEntity,
                errors);
        // For :Order
        validateTestcaseResultEntityOrder(testcaseResultEntity,
                errors);
        // For :TestcaseOption
        validateTestcaseResultEntityTestcaseOption(testcaseResultEntity,
                errors);
        return errors;
    }

    protected void validateCommonForeignKey(TestcaseResultEntity testcaseResultEntity,
                                            List<ValidationResultInfo> errors,
                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        //validate TestcaseResult foreignKey.
        if (testcaseResultEntity.getTester() != null) {
            try {
                testcaseResultEntity.setTester(
                        userService.getUserById(testcaseResultEntity.getTester().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "tester";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the tester does not exists"));
            }
        }
        //validate TestcaseOption foreignKey.
        if (testcaseResultEntity.getTestcaseOption() != null) {
            try {
                testcaseResultEntity.setTestcaseOption(
                        testcaseOptionService.getTestcaseOptionById(testcaseResultEntity.getTestcaseOption().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "testcaseOption";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the testcaseOption does not exists"));
            }
        }
    }

    //validate update
    protected void validateUpdateTestcaseResult(List<ValidationResultInfo> errors,
                                                TestcaseResultEntity testcaseResultEntity,
                                                TestcaseResultEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(testcaseResultEntity.getId(), "id", errors);
        //check the meta required
        if (testcaseResultEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!testcaseResultEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the TestcaseResult since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        this.validateNotUpdatable(errors, testcaseResultEntity, originalEntity);
    }

    //validate not update
    protected void validateNotUpdatable(List<ValidationResultInfo> errors,
                                        TestcaseResultEntity testcaseResultEntity,
                                        TestcaseResultEntity originalEntity) {
    }

    //validate create
    protected void validateCreateTestcaseResult(
            List<ValidationResultInfo> errors,
            TestcaseResultEntity testcaseResultEntity,
            ContextInfo contextInfo) {
        if (testcaseResultEntity.getId() != null) {
            try {
                this.getTestcaseResultById(testcaseResultEntity.getId(),
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
    protected void validateCommonRequired(TestcaseResultEntity testcaseResultEntity,
                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testcaseResultEntity.getName(), "name", errors);
        ValidationUtils.validateRequired(testcaseResultEntity.getRank(), "rank", errors);
        if(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PASSED.equals(testcaseResultEntity.getState())
                || TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FAILED.equals(testcaseResultEntity.getState())) {
            ValidationUtils.validateRequired(testcaseResultEntity.getTestcaseOption(), "testcaseOption", errors);
        }
    }

    //Validate Common Unique
    protected void validateCommonUnique(TestcaseResultEntity testcaseResultEntity,
                                        String validationTypeKey,
                                        List<ValidationResultInfo> errors,
                                        ContextInfo contextInfo)
            throws OperationFailedException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || testcaseResultEntity.getId() != null)) {
            TestcaseResultSearchFilter searchFilter = new TestcaseResultSearchFilter();
            searchFilter.setTestRequestId(testcaseResultEntity.getTestRequestId());
            searchFilter.setRefId(testcaseResultEntity.getRefId());
            searchFilter.setRefObjUri(testcaseResultEntity.getRefObjUri());
            searchFilter.setIsManual(Objects.equals(Boolean.TRUE, testcaseResultEntity.getManual()));
            Page<TestcaseResultEntity> testcaseResultEntities = this
                    .searchTestcaseResults(
                            null,
                            searchFilter,
                            Constant.TWO_VALUE_PAGE,
                            contextInfo);

            // if info found with same testRequestId, refObjUri, refId than and not current id
            boolean flag
                    = testcaseResultEntities.stream().anyMatch(c -> (validationTypeKey.equals(Constant.CREATE_VALIDATION)
                    || !c.getId().equals(testcaseResultEntity.getId()))
            );
            if (flag) {
                String fieldName = "testRequestId, refObjUri and refId";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Given TestcaseResult with same testRequestId, refObjUri and refId already exists."));
            }
        }
    }

    //Validation For :Id
    protected void validateTestcaseResultEntityId(TestcaseResultEntity testcaseResultEntity,
                                                  List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(testcaseResultEntity.getId(), "id", errors);
    }

    //Validation For :Name
    protected void validateTestcaseResultEntityName(TestcaseResultEntity testcaseResultEntity,
                                                    List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(testcaseResultEntity.getName(),
                "name",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(testcaseResultEntity.getName(),
                "name",
                3,
                255,
                errors);
    }

    //Validation For :Order
    protected void validateTestcaseResultEntityOrder(TestcaseResultEntity testcaseResultEntity,
                                                     List<ValidationResultInfo> errors) {
        ValidationUtils.validateIntegerRange(testcaseResultEntity.getRank(),
                "rank",
                1,
                null,
                errors);
    }

    //Validation For :TestcaseOption
    protected void validateTestcaseResultEntityTestcaseOption(TestcaseResultEntity testcaseResultEntity,
                                                     List<ValidationResultInfo> errors) {
    }

    //trim all TestcaseResult field
    protected void trimTestcaseResult(TestcaseResultEntity testcaseResultEntity) {
        if (testcaseResultEntity.getId() != null) {
            testcaseResultEntity.setId(testcaseResultEntity.getId().trim());
        }
        if (testcaseResultEntity.getName() != null) {
            testcaseResultEntity.setName(testcaseResultEntity.getName().trim());
        }
        if (testcaseResultEntity.getDescription() != null) {
            testcaseResultEntity.setDescription(testcaseResultEntity.getDescription().trim());
        }
        if (testcaseResultEntity.getMessage() != null) {
            testcaseResultEntity.setMessage(testcaseResultEntity.getMessage().trim());
        }
        if (testcaseResultEntity.getRefObjUri() != null) {
            testcaseResultEntity.setRefObjUri(testcaseResultEntity.getRefObjUri().trim());
        }
        if (testcaseResultEntity.getRefId() != null) {
            testcaseResultEntity.setRefId(testcaseResultEntity.getRefId().trim());
        }
    }
}
