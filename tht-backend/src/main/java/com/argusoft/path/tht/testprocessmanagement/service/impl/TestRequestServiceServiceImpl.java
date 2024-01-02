/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.service.impl;

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
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
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
            OperationFailedException,
            DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntities
                = this.validateTestRequestStartProcess(
                testRequestId,
                Constant.START_AUTOMATION_PROCESS_VALIDATION,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
        testcaseExecutioner.executeAutomationTestingByTestRequest(testRequestId, contextInfo);
    }

    @Override
    @Timed(name = "startManualTestingProcess")
    public void startManualTestingProcess(String testRequestId, ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException,
            DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        List<ValidationResultInfo> validationResultEntities
                = this.validateTestRequestStartProcess(
                testRequestId,
                Constant.START_MANUAL_PROCESS_VALIDATION,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
        testcaseExecutioner.executeManualTestingByTestRequest(testRequestId, contextInfo);
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
                        null, SearchType.CONTAINING,
                        null,
                        TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                        testRequestId,
                        testRequestId,
                        Constant.START_MANUAL_PROCESS_VALIDATION.equals(validationTypeKey) ? Boolean.TRUE : Boolean.FALSE, null
                );
                List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(new ArrayList<>(), searchFilter, Constant.FULL_PAGE, contextInfo).getContent();
                if (!testcaseResultEntities.isEmpty()) {
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

        List<ValidationResultInfo> validationResultEntities
                = this.validateTestRequest(Constant.CREATE_VALIDATION,
                testRequestEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntities);
        }
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

        List<ValidationResultInfo> validationResultEntitys
                = this.validateTestRequest(Constant.UPDATE_VALIDATION,
                testRequestEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
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
        if (testRequestEntity == null) {
            throw new InvalidParameterException("TestRequestEntity is missing");
        }
        if (StringUtils.isEmpty(validationTypeKey)) {
            throw new InvalidParameterException("validationTypeKey is missing");
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        TestRequestEntity originalEntity = null;
        trimTestRequest(testRequestEntity);

        // check Common Required
        this.validateCommonRequired(testRequestEntity, errors);

        // check Common ForeignKey
        this.validateCommonForeignKey(testRequestEntity, errors, contextInfo);

        // check Common Unique
        this.validateCommonUnique(testRequestEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (testRequestEntity.getId() != null) {
                    try {
                        originalEntity = this
                                .getTestRequestById(testRequestEntity.getId(),
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

                this.validateUpdateTestRequest(errors,
                        testRequestEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                this.validateCreateTestRequest(errors, testRequestEntity, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateTestRequestEntityId(testRequestEntity,
                errors);
        // For :Name
        validateTestRequestEntityName(testRequestEntity,
                errors);
        return errors;
    }

    protected void validateCommonForeignKey(TestRequestEntity testRequestEntity,
                                            List<ValidationResultInfo> errors,
                                            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        //validate TestRequest foreignKey.
        if (testRequestEntity.getApprover() != null) {
            try {
                testRequestEntity.setApprover(
                        userService.getUserById(testRequestEntity.getApprover().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "approver";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the approver does not exists"));
            }
        }
        if (testRequestEntity.getAssessee() != null) {
            try {
                testRequestEntity.setAssessee(
                        userService.getUserById(testRequestEntity.getAssessee().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                String fieldName = "assessee";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the assessee does not exists"));
            }
        }
        if (!testRequestEntity.getTestRequestUrls().isEmpty()) {
            for (TestRequestUrlEntity testRequestUrlEntity : testRequestEntity.getTestRequestUrls()) {
                try {
                    if (testRequestUrlEntity.getComponent() != null) {
                        testRequestUrlEntity.setComponent(componentService.getComponentById(testRequestUrlEntity.getComponent().getId(), contextInfo));
                    }
                } catch (DoesNotExistException | InvalidParameterException ex) {
                    String fieldName = "testRequestUrls.component";
                    errors.add(
                            new ValidationResultInfo(fieldName,
                                    ErrorLevel.ERROR,
                                    "The id supplied for the component does not exists"));
                }
            }
        }
    }

    //validate update
    protected void validateUpdateTestRequest(List<ValidationResultInfo> errors,
                                             TestRequestEntity testRequestEntity,
                                             TestRequestEntity originalEntity)
            throws OperationFailedException,
            InvalidParameterException {
        // required validation
        ValidationUtils.validateRequired(testRequestEntity.getId(), "id", errors);
        //check the meta required
        if (testRequestEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!testRequestEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the TestRequest since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        this.validateNotUpdatable(errors, testRequestEntity, originalEntity);
    }

    //validate not update
    protected void validateNotUpdatable(List<ValidationResultInfo> errors,
                                        TestRequestEntity testRequestEntity,
                                        TestRequestEntity originalEntity) {
    }

    //validate create
    protected void validateCreateTestRequest(
            List<ValidationResultInfo> errors,
            TestRequestEntity testRequestEntity,
            ContextInfo contextInfo) {
        if (testRequestEntity.getId() != null) {
            try {
                this.getTestRequestById(testRequestEntity.getId(),
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
    protected void validateCommonRequired(TestRequestEntity testRequestEntity,
                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(testRequestEntity.getName(), "name", errors);
    }

    //Validate Common Unique
    protected void validateCommonUnique(TestRequestEntity testRequestEntity,
                                        String validationTypeKey,
                                        List<ValidationResultInfo> errors,
                                        ContextInfo contextInfo)
            throws OperationFailedException {
        // check unique field
    }

    //Validation For :Id
    protected void validateTestRequestEntityId(TestRequestEntity testRequestEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(testRequestEntity.getId(), "id", errors);
    }

    //Validation For :Name
    protected void validateTestRequestEntityName(TestRequestEntity testRequestEntity,
                                                 List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(testRequestEntity.getName(),
                "name",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(testRequestEntity.getName(),
                "name",
                3,
                1000,
                errors);
    }

    //trim all TestRequest field
    protected void trimTestRequest(TestRequestEntity testRequestEntity) {
        if (testRequestEntity.getId() != null) {
            testRequestEntity.setId(testRequestEntity.getId().trim());
        }
        if (testRequestEntity.getName() != null) {
            testRequestEntity.setName(testRequestEntity.getName().trim());
        }
        if (testRequestEntity.getDescription() != null) {
            testRequestEntity.setDescription(testRequestEntity.getDescription().trim());
        }
        if (testRequestEntity.getEvaluationVersionId() != null) {
            testRequestEntity.setEvaluationVersionId(testRequestEntity.getEvaluationVersionId().trim());
        }
        if (testRequestEntity.getFhirVersion() != null) {
            testRequestEntity.setFhirVersion(testRequestEntity.getFhirVersion().trim());
        }
        testRequestEntity.getTestRequestUrls().stream().forEach(testRequestUrlEntity -> {
            if (testRequestUrlEntity.getBaseUrl() != null) {
                testRequestUrlEntity.setBaseUrl(testRequestUrlEntity.getBaseUrl().trim());
            }
            if (testRequestUrlEntity.getUsername() != null) {
                testRequestUrlEntity.setUsername(testRequestUrlEntity.getUsername().trim());
            }
            if (testRequestUrlEntity.getPassword() != null) {
                testRequestUrlEntity.setPassword(testRequestUrlEntity.getPassword().trim());
            }
        });
    }
}
