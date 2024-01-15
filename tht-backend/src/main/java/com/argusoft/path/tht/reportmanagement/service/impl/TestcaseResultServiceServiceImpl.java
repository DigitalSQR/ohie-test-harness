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
import com.argusoft.path.tht.reportmanagement.validator.TestcaseResultValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        TestcaseResultValidator.validateCreateUpdateTestCaseResult(Constant.CREATE_VALIDATION,
                this,
                userService,
                testcaseOptionService,
                testcaseResultEntity,
                contextInfo);

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
                    null,
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
        //If result is inactive then do nothing.
        if (TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INACTIVE.equals(testcaseResultState)) {
            return;
        }
        TestRequestEntity testRequestEntity = testRequestService.getTestRequestById(testRequestId, contextInfo);
        //If result is in progress/pending then make testRequest inProgress.
        if (TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS.equals(testcaseResultState)
                || TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING.equals(testcaseResultState)) {
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
                    null,
                    null, TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI, testRequestId, testRequestId, null, null
            );
            List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(new ArrayList<>(), searchFilter, Constant.FULL_PAGE, contextInfo).getContent();
            if (testcaseResultEntities.size() == 2
                    && TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED.equals(testcaseResultEntities.get(0).getState())
                    && TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED.equals(testcaseResultEntities.get(1).getState())) {

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

        TestcaseResultValidator.validateCreateUpdateTestCaseResult(Constant.UPDATE_VALIDATION,
                this,
                userService,
                testcaseOptionService,
                testcaseResultEntity,
                contextInfo);

        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);
        updateTestRequestByTestcaseResultState(testcaseResultEntity.getTestRequestId(), testcaseResultEntity.getState(), contextInfo);
        return testcaseResultEntity;
    }


    @Timed(name = "submitTestcaseResult")
    @Override
    public TestcaseResultEntity submitTestcaseResult(String testcaseResultId, String selectedTestcaseOptionId, ContextInfo contextInfo) throws OperationFailedException, VersionMismatchException, DataValidationErrorException, InvalidParameterException, DoesNotExistException {

        TestcaseResultValidator.validateSubmitTestcaseResult(
                testcaseResultId,
                selectedTestcaseOptionId,
                Constant.SUBMIT_VALIDATION,
                this,
                testcaseOptionService,
                contextInfo);

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
        List<ValidationResultInfo> errors = TestcaseResultValidator.validateTestCaseResult(validationTypeKey, testcaseResultEntity, userService, this, testcaseOptionService, contextInfo);
        return errors;
    }

}
