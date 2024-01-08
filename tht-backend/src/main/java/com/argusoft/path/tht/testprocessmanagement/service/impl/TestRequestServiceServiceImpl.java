/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestcaseExecutioner;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.testprocessmanagement.validator.TestRequestValidator;
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
import java.util.Objects;
import java.util.Optional;

/**
 * This TestRequestServiceServiceImpl contains implementation for TestRequest service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "TestRequestServiceServiceImpl")
public class TestRequestServiceServiceImpl implements TestRequestService {

    @Autowired
    private TestRequestRepository testRequestRepository;

    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestcaseExecutioner testcaseExecutioner;

    @Override
    @Timed(name = "startAutomationTestingProcess")
    public void startAutomationTestingProcess(String testRequestId, ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException, DataValidationErrorException {
        TestRequestValidator.validateTestRequestProcess(
                testRequestId,
                Constant.START_AUTOMATION_PROCESS_VALIDATION,
                this,
                testcaseResultService,
                contextInfo);

        testcaseExecutioner.executeAutomationTestingByTestRequest(testRequestId, contextInfo);
    }

    @Override
    @Timed(name = "reinitializeAutomationTestingProcess")
    public void reinitializeAutomationTestingProcess(String testRequestId, ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException, DataValidationErrorException {
        if (StringUtils.isEmpty(testRequestId)) {
            throw new InvalidParameterException("testRequestId is missing");
        }
        List<ValidationResultInfo> validationResultEntities
                = this.validateTestRequestReinitializeProcess(
                testRequestId,
                Constant.START_AUTOMATION_PROCESS_VALIDATION,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
        testcaseExecutioner.reinitializeAutomationTestingByTestRequest(testRequestId, contextInfo);
    }

    @Override
    @Timed(name = "startManualTestingProcess")
    public void startManualTestingProcess(String testRequestId, ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException,
            DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        TestRequestValidator.validateTestRequestProcess(
                testRequestId,
                Constant.START_MANUAL_PROCESS_VALIDATION,
                this,
                testcaseResultService,
                contextInfo);

        testcaseExecutioner.executeManualTestingByTestRequest(testRequestId, contextInfo);
    }

    public List<ValidationResultInfo> validateTestRequestReinitializeProcess(
            String testRequestId,
            String validationTypeKey,
            ContextInfo contextInfo)
            throws OperationFailedException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        try {
            TestRequestEntity originalEntity = this
                    .getTestRequestById(testRequestId,
                            contextInfo);
            if (!Constant.START_MANUAL_PROCESS_VALIDATION.equals(validationTypeKey)
                    && Objects.equals(originalEntity.getState(), TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS)) {
                TestcaseResultSearchFilter searchFilter = new TestcaseResultSearchFilter(
                        null, SearchType.CONTAINING,
                        TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,
                        null,
                        TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                        testRequestId,
                        testRequestId,
                        Boolean.FALSE,
                        null
                );
                List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(new ArrayList<>(), searchFilter, Constant.FULL_PAGE, contextInfo).getContent();
                if (testcaseResultEntities.isEmpty()) {
                    String fieldName = "testRequestId";
                    errors.add(
                            new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    "Automation process for The supplied testRequestId hasn't been finished yet."));
                }
            }
        } catch (DoesNotExistException | InvalidParameterException ex) {
            String fieldName = "testRequestId";
            errors.add(
                    new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            "The testRequestId supplied for the start process does not "
                                    + "exists"));
        }
        return errors;
    }

    public List<ValidationResultInfo> validateTestRequestStartProcess(
            String testRequestId,
            String validationTypeKey,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        try {
            TestRequestEntity originalEntity = this
                    .getTestRequestById(testRequestId,
                            contextInfo);
            if (!Objects.equals(originalEntity.getState(), TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED)) {
                TestcaseResultSearchFilter searchFilter = new TestcaseResultSearchFilter(
                        null, SearchType.CONTAINING,
                        null,
                        null,
                        TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                        testRequestId,
                        testRequestId,
                        Constant.START_MANUAL_PROCESS_VALIDATION.equals(validationTypeKey) ? Boolean.TRUE : Boolean.FALSE, null
                );
                List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(new ArrayList<>(), searchFilter, Constant.FULL_PAGE, contextInfo).getContent();
                if (!testcaseResultEntities.isEmpty()
                        && testcaseResultEntities.stream().anyMatch(testcaseResultEntity ->
                        !TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING.equals(testcaseResultEntity.getState())
                )) {
                    String fieldName = "testRequestId";
                    errors.add(
                            new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    "Process for The supplied testRequestId has been already started."));
                }
            }
        } catch (DoesNotExistException | InvalidParameterException ex) {
            String fieldName = "testRequestId";
            errors.add(
                    new ValidationResultInfo(fieldName,
                            ErrorLevel.ERROR,
                            "The testRequestId supplied for the start process does not "
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
    @Timed(name = "createTestRequest")
    public TestRequestEntity createTestRequest(TestRequestEntity testRequestEntity,
                                               ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestRequestValidator.validateTestRequest(Constant.CREATE_VALIDATION,
                this,
                testRequestEntity,
                contextInfo);

        //TODO: New request will have state TEST_REQUEST_STATUS_PENDING or DRAFT by default
        //Create state change API to make this as Accepted or Rejected
        testRequestEntity = testRequestRepository.save(testRequestEntity);
        return testRequestEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "updateTestRequest")
    public TestRequestEntity updateTestRequest(TestRequestEntity testRequestEntity,
                                               ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestRequestValidator.validateTestRequest(Constant.UPDATE_VALIDATION,
                this,
                testRequestEntity,
                contextInfo);

        Optional<TestRequestEntity> testRequestOptional
                = testRequestRepository.findById(testRequestEntity.getId());
        testRequestEntity = testRequestRepository.save(testRequestEntity);
        return testRequestEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchTestRequests")
    public Page<TestRequestEntity> searchTestRequests(
            List<String> ids,
            TestRequestSearchFilter testRequestSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException {

        if (!CollectionUtils.isEmpty(ids)) {
            return this.searchTestRequestsById(ids, pageable);
        } else {
            return this.searchTestRequests(testRequestSearchFilter, pageable);
        }
    }

    private Page<TestRequestEntity> searchTestRequests(
            TestRequestSearchFilter testRequestSearchFilter,
            Pageable pageable)
            throws OperationFailedException {

        Page<TestRequestEntity> testRequests = testRequestRepository.advanceTestRequestSearch(
                testRequestSearchFilter,
                pageable);
        return testRequests;
    }

    private Page<TestRequestEntity> searchTestRequestsById(
            List<String> ids,
            Pageable pageable) {

        List<TestRequestEntity> testRequests
                = testRequestRepository.findTestRequestsByIds(ids);
        return new PageImpl<>(testRequests,
                pageable,
                testRequests.size());
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getTestRequestById")
    public TestRequestEntity getTestRequestById(String testRequestId,
                                                ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(testRequestId)) {
            throw new InvalidParameterException("TestRequestId is missing");
        }
        Optional<TestRequestEntity> testRequestOptional
                = testRequestRepository.findById(testRequestId);
        if (!testRequestOptional.isPresent()) {
            throw new DoesNotExistException("TestRequest by id :"
                    + testRequestId
                    + Constant.NOT_FOUND);
        }
        return testRequestOptional.get();
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getTestRequests")
    public Page<TestRequestEntity> getTestRequests(Pageable pageable,
                                                   ContextInfo contextInfo)
            throws InvalidParameterException {
        if (pageable == null) {
            throw new InvalidParameterException("pageble is missing");
        }
        Page<TestRequestEntity> testRequests = testRequestRepository.findTestRequests(pageable);
        return testRequests;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    @Timed(name = "validateTestRequest")
    public List<ValidationResultInfo> validateTestRequest(
            String validationTypeKey,
            TestRequestEntity testRequestEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        List<ValidationResultInfo> errors = TestRequestValidator.validateCreateUpdateTestCase(validationTypeKey, testRequestEntity, this, userService, componentService, contextInfo);
        return errors;
    }

    @Override
    public TestRequestEntity changeState(String testRequestId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException {

        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        if(!TestRequestServiceConstants.TEST_REQUEST_STATUS.contains(stateKey)) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("state");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("provided state is not valid ");
            errors.add(validationResultInfo);
            throw new DataValidationErrorException("Validation Failed due to errors ",errors);
        }
        TestRequestEntity testRequestEntity = this.getTestRequestById(testRequestId, contextInfo);

        testRequestEntity.setState(stateKey);
        testRequestRepository.save(testRequestEntity);

        return testRequestEntity;
    }
}
