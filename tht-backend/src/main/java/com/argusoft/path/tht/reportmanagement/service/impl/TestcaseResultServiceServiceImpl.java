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
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
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
 * This TestcaseResultServiceServiceImpl contains implementation for TestcaseResult service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "TestcaseResultServiceServiceImpl")
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
    @Timed(name = "createTestcaseResult")
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

    protected void updateParentTestcaseResultByTestcaseResultState(TestcaseResultEntity testcaseResultEntity, ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException, DoesNotExistException, DataValidationErrorException, VersionMismatchException {
        if (testcaseResultEntity.getParentTestcaseResult() == null
                || TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING.equals(testcaseResultEntity.getState())
                || TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED.equals(testcaseResultEntity.getParentTestcaseResult())) {
            //If result is pending then do not change anything.
            //If result don't have parent then skip.
            return;
        }
        TestcaseResultEntity parenttestcaseResultEntity = testcaseResultEntity.getParentTestcaseResult();
        if (TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS.equals(testcaseResultEntity.getState())) {
            parenttestcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS);
            parenttestcaseResultEntity = this.updateTestcaseResult(parenttestcaseResultEntity, contextInfo);
            updateParentTestcaseResultByTestcaseResultState(parenttestcaseResultEntity, contextInfo);
        } else {
            TestcaseResultSearchFilter searchFilter = new TestcaseResultSearchFilter(
                    null, SearchType.CONTAINING,
                    TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, SearchType.EXACTLY,
                    null,
                    null,
                    null,
                    null,
                    null,
                    parenttestcaseResultEntity.getId()
            );
            List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(new ArrayList<>(), searchFilter, Constant.FULL_PAGE, contextInfo).getContent();
            if (testcaseResultEntities.stream().allMatch(testcaseResultEntity1 -> TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED.equals(testcaseResultEntity1.getState()))) {
                parenttestcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED);
                parenttestcaseResultEntity.setTester(testcaseResultEntity.getTester());
                parenttestcaseResultEntity.setSuccess(testcaseResultEntities.stream().allMatch(testcaseResultEntity1 -> testcaseResultEntity1.getSuccess()));
                parenttestcaseResultEntity = this.updateTestcaseResult(parenttestcaseResultEntity, contextInfo);
                updateParentTestcaseResultByTestcaseResultState(parenttestcaseResultEntity, contextInfo);
            }
        }
    }

    protected void updateTestRequestByTestcaseResultState(String testRequestId, String testcaseResultState, ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException, DoesNotExistException, DataValidationErrorException, VersionMismatchException {
        if (TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING.equals(testcaseResultState)) {
            //If result is pending then do not change anything.
            return;
        }
        TestRequestEntity testRequestEntity = testRequestService.getTestRequestById(testRequestId, contextInfo);
        //If result is in progress then make testRequest inProgress.
        if (TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS.equals(testcaseResultState)) {
            if (!TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS.equals(testRequestEntity.getState())) {
                testRequestEntity.setState(TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS);
                testRequestService.updateTestRequest(testRequestEntity, contextInfo);
            }
            return;
        }
        //If all result are finished and testRequest is not finished then make it finished.
        if (!TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED.equals(testRequestEntity.getState())) {
            TestcaseResultSearchFilter searchFilter = new TestcaseResultSearchFilter(
                    null, SearchType.CONTAINING,
                    null, SearchType.CONTAINING,
                    null, TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI, testRequestId, testRequestId, null, null
            );
            List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(new ArrayList<>(), searchFilter, Constant.FULL_PAGE, contextInfo).getContent();
            if (testcaseResultEntities.size() == 2
                    && TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED.equals(testcaseResultEntities.get(0).getState())
                    && TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED.equals(testcaseResultEntities.get(0).getState())) {
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
    @Timed(name = "updateTestcaseResult")
    @Override
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
        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);
        updateTestRequestByTestcaseResultState(testcaseResultEntity.getTestRequestId(), testcaseResultEntity.getState(), contextInfo);
        return testcaseResultEntity;
    }


    @Timed(name = "submitTestcaseResult")
    @Override
    public TestcaseResultEntity submitTestcaseResult(String testcaseResultId, String selectedTestcaseOptionId, ContextInfo contextInfo) throws OperationFailedException, VersionMismatchException, DataValidationErrorException, InvalidParameterException, DoesNotExistException {
        List<ValidationResultInfo> validationResultEntitys
                = this.validateTestcaseResultSubmit(
                testcaseResultId,
                selectedTestcaseOptionId,
                Constant.SUBMIT_VALIDATION,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
        //Submit testcaseResult.
        TestcaseResultEntity testcaseResultEntity
                = testcaseResultRepository.findById(testcaseResultId).get();

        TestcaseOptionEntity testcaseOptionEntity
                = testcaseOptionService.getTestcaseOptionById(selectedTestcaseOptionId, contextInfo);

        testcaseResultEntity.setTestcaseOption(testcaseOptionEntity);

        UserEntity userEntity =
                userService.getPrincipalUser(contextInfo);
        testcaseResultEntity.setTester(userEntity);

        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED);
        testcaseResultEntity.setSuccess(testcaseOptionEntity.getSuccess());

        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);

        //Update parents
        updateParentTestcaseResultByTestcaseResultState(testcaseResultEntity, contextInfo);

        //Update TestRequest
        updateTestRequestByTestcaseResultState(testcaseResultEntity.getTestRequestId(), testcaseResultEntity.getState(), contextInfo);

        return testcaseResultEntity;
    }

    public List<ValidationResultInfo> validateTestcaseResultSubmit(
            String testcaseResultId,
            String selectedTestcaseOptionId,
            String validationTypeKey,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestcaseResultEntity originalEntity;
        try {
            originalEntity = this
                    .getTestcaseResultById(testcaseResultId,
                            contextInfo);
        } catch (DoesNotExistException | InvalidParameterException ex) {
            String fieldName = "testcaseResultId";
            errors.add(
                    new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            "The testcaseResultId supplied for the submit does not "
                                    + "exists"));
            return errors;
        }
        try {
            TestcaseOptionEntity testcaseOption = testcaseOptionService
                    .getTestcaseOptionById(selectedTestcaseOptionId,
                            contextInfo);

            if (!originalEntity.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI)
                    && !testcaseOption.getTestcase().getId().equals(originalEntity.getRefId())
            ) {
                String fieldName = "selectedTestcaseOptionId";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The selectedTestcaseOptionId supplied for the submit is invalid for the testcaseResult."));
            }
        } catch (DoesNotExistException | InvalidParameterException ex) {
            String fieldName = "selectedTestcaseOptionId";
            errors.add(
                    new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            "The selectedTestcaseOptionId supplied for the submit does not "
                                    + "exists"));
        }
        return errors;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchTestcaseResults")
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
    @Timed(name = "getTestcaseResultById")
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
    @Timed(name = "getTestcaseResults")
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
    @Timed(name = "validateTestcaseResult")
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
        // For :IsSuccess
        validateTestcaseResultEntityIsSuccess(testcaseResultEntity,
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
        //validate TestcaseResult parentTestcaseResult.
        if (testcaseResultEntity.getParentTestcaseResult() != null) {
            try {
                testcaseResultEntity.setParentTestcaseResult(
                        this.getTestcaseResultById(testcaseResultEntity.getParentTestcaseResult().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "parentTestcaseResult";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the parentTestcaseResult does not exists"));
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
        if (TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED.equals(testcaseResultEntity.getState())
                && TestcaseServiceConstants.TESTCASE_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())
                && Objects.equals(Boolean.TRUE, testcaseResultEntity.getManual())
                && Objects.equals(Boolean.FALSE, testcaseResultEntity.getHasSystemError())) {
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
        ValidationUtils.validateLength(testcaseResultEntity.getName(),
                "name",
                3,
                1000,
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

    //Validation For :isSuccess
    protected void validateTestcaseResultEntityIsSuccess(TestcaseResultEntity testcaseResultEntity,
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
