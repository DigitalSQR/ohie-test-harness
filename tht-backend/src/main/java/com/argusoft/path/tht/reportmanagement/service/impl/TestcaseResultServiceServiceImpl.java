/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultRepository;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.reportmanagement.validator.TestcaseResultValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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

        TestcaseResultValidator.validateCreateUpdateTestCaseResult(Constant.CREATE_VALIDATION,
                this,
                userService,
                testcaseOptionService,
                testRequestService,
                testcaseResultEntity,
                contextInfo);

        if (StringUtils.isEmpty(testcaseResultEntity.getId())) {
            testcaseResultEntity.setId(UUID.randomUUID().toString());
        }
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT);
        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);
        return testcaseResultEntity;
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
                testRequestService,
                testcaseResultEntity,
                contextInfo);

        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);
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
        testcaseResultEntity.setSuccess(testcaseOptionEntity.getSuccess());

        UserEntity userEntity =
                userService.getPrincipalUser(contextInfo);
        testcaseResultEntity.setTester(userEntity);

        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);

        changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);

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
            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<TestcaseResultEntity> testcaseEntitySpecification = testcaseResultCriteriaSearchFilter.buildSpecification();
        return testcaseResultRepository.findAll(testcaseEntitySpecification, pageable);
    }

    @Override
    @Timed(name = "searchTestcaseResults")
    public List<TestcaseResultEntity> searchTestcaseResults(
            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseResultEntity> testcaseResultEntitySpecification = testcaseResultCriteriaSearchFilter.buildSpecification();
        return testcaseResultRepository.findAll(testcaseResultEntitySpecification);
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
        List<ValidationResultInfo> errors = TestcaseResultValidator.validateTestCaseResult(validationTypeKey, testcaseResultEntity, userService, this, testcaseOptionService, testRequestService, contextInfo);
        return errors;
    }

    @Override
    public TestcaseResultEntity changeState(String testcaseResultId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {

        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        if (!TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS.contains(stateKey)) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("state");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("provided state is not valid ");
            errors.add(validationResultInfo);
            throw new DataValidationErrorException("Validation Failed due to errors ", errors);
        }
        TestcaseResultEntity testcaseResultEntity = this.getTestcaseResultById(testcaseResultId, contextInfo);
        if (!stateKey.equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED)) {
            testcaseResultEntity.setMessage(null);
            testcaseResultEntity.setHasSystemError(Boolean.FALSE);
            testcaseResultEntity.setSuccess(Boolean.FALSE);
            testcaseResultEntity.setSuccess(Boolean.FALSE);
        }
        testcaseResultEntity.setState(stateKey);
        testcaseResultEntity = testcaseResultRepository.save(testcaseResultEntity);

        if (!testcaseResultEntity.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI)) {
            updateChildTestcaseResult(testcaseResultEntity, contextInfo);
        }

        if (testcaseResultEntity.getParentTestcaseResult() != null) {
            updateParentTestcaseResult(testcaseResultEntity.getParentTestcaseResult(), contextInfo);
        } else {
            updateTestRequestState(testcaseResultEntity.getTestRequest(), contextInfo);
        }
        return testcaseResultEntity;
    }

    private void updateTestRequestState(
            TestRequestEntity testRequestEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DoesNotExistException,
            DataValidationErrorException,
            VersionMismatchException {

        TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
        searchFilter.setRefObjUri(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI);
        searchFilter.setRefId(testRequestEntity.getId());

        List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(searchFilter, contextInfo);

        if (testcaseResultEntities.stream()
                .allMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP))) {
            if (!testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_SKIPPED)) {
                testRequestService.changeState(testRequestEntity.getId(), TestRequestServiceConstants.TEST_REQUEST_STATUS_SKIPPED, contextInfo);
            }
        } else if (testcaseResultEntities.stream()
                .allMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED)
                        || tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP))) {
            if (!testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED)) {
                testRequestService.changeState(testRequestEntity.getId(), TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED, contextInfo);
            }
        } else if (testcaseResultEntities.stream()
                .anyMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT))) {
            if (!testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED)) {
                testRequestService.changeState(testRequestEntity.getId(), TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED, contextInfo);
            }
        } else if (!testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS)) {
            testRequestService.changeState(testRequestEntity.getId(), TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS, contextInfo);
        }
    }

    private void updateChildTestcaseResult(
            TestcaseResultEntity testcaseResultEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DoesNotExistException,
            DataValidationErrorException,
            VersionMismatchException {
        if (!testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP)) {
            return;
        }

        TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
        searchFilter.setParentTestcaseResultId(testcaseResultEntity.getId());


        List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(searchFilter, contextInfo);
        for (TestcaseResultEntity testcaseResult : testcaseResultEntities) {
            if (!testcaseResult.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP)) {
                changeState(testcaseResult.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP, contextInfo);
            }
        }
    }

    private void updateParentTestcaseResult(
            TestcaseResultEntity testcaseResultEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DoesNotExistException,
            DataValidationErrorException,
            VersionMismatchException {
        TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
        searchFilter.setParentTestcaseResultId(testcaseResultEntity.getId());

        List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(searchFilter, contextInfo);
        if (testcaseResultEntities.stream()
                .allMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP))) {
            if (!testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP)) {
                testcaseResultEntity.setSuccess(Boolean.TRUE);
                updateTestcaseResult(testcaseResultEntity, contextInfo);
                changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP, contextInfo);
            }
        } else if (testcaseResultEntities.stream()
                .allMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED)
                        || tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP))) {
            if (!testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED)) {
                Long duration = testcaseResultEntities.stream()
                        .filter(tcr -> tcr.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED))
                        .map(TestcaseResultEntity::getDuration).reduce(0L, Long::sum);
                if (testcaseResultEntities.stream()
                        .anyMatch(tre -> {
                            return tre.getRequired() && !Objects.equals(tre.getSuccess(), Boolean.TRUE);
                        })) {
                    testcaseResultEntity.setSuccess(Boolean.FALSE);
                } else {
                    testcaseResultEntity.setSuccess(Boolean.TRUE);
                }
                testcaseResultEntity.setDuration(duration);
                updateTestcaseResult(testcaseResultEntity, contextInfo);
                changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);
            }
        } else if (testcaseResultEntities.stream()
                .anyMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS))) {
            if (!testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS)) {
                changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS, contextInfo);
            }
        } else if (testcaseResultEntities.stream()
                .anyMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING))) {
            if (!testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING)) {
                changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING, contextInfo);
            }
        } else {
            if (!testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT)) {
                changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT, contextInfo);
            }
        }
    }
}
