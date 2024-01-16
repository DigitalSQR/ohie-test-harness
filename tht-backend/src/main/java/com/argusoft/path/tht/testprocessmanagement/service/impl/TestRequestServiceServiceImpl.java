/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestcaseExecutioner;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.testprocessmanagement.validator.TestRequestValidator;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
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
        TestRequestValidator.validateTestRequestReinitializeProcess(
                testRequestId,
                Constant.REINITIALIZE_AUTOMATION_PROCESS_VALIDATION,
                this,
                testcaseResultService,
                contextInfo);
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

        TestRequestValidator.validateCreateUpdateTestRequest(Constant.CREATE_VALIDATION,
                testRequestEntity,
                this,
                userService,
                componentService,
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

        TestRequestValidator.validateCreateUpdateTestRequest(Constant.UPDATE_VALIDATION,
                testRequestEntity,
                this,
                userService,
                componentService,
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
            throws OperationFailedException, DoesNotExistException {

        UserEntity principalUser = userService.getPrincipalUser(contextInfo);
        if(principalUser.getRoles().stream().anyMatch(roleEntity -> UserServiceConstants.ROLE_ID_ASSESSEE.equals(roleEntity.getId()))){
            testRequestSearchFilter.setAssesseeId(principalUser.getId());
        }

        if(testRequestSearchFilter.isEmpty()){
            return this.searchTestRequestsById(ids, pageable);
        }
        else {
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
        List<ValidationResultInfo> errors = TestRequestValidator.validateTestRequest(validationTypeKey, testRequestEntity, this, userService, componentService, contextInfo);
        return errors;
    }

    @Override
    public TestRequestEntity changeState(String testRequestId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException {

        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        if (!TestRequestServiceConstants.TEST_REQUEST_STATUS.contains(stateKey)) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("state");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("provided state is not valid ");
            errors.add(validationResultInfo);
            throw new DataValidationErrorException("Validation Failed due to errors ", errors);
        }
        TestRequestEntity testRequestEntity = this.getTestRequestById(testRequestId, contextInfo);

        testRequestEntity.setState(stateKey);
        testRequestRepository.save(testRequestEntity);

        return testRequestEntity;
    }
}
