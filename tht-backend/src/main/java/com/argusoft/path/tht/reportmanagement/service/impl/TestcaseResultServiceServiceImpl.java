package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.Audit.Service.AuditService;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.evaluator.GradeEvaluator;
import com.argusoft.path.tht.reportmanagement.filter.TestResultRelationCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapper;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultRepository;
import com.argusoft.path.tht.reportmanagement.service.TestResultRelationService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.reportmanagement.validator.TestcaseResultValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateNameMetaInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.models.entity.IdMetaEntity;
import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This TestcaseResultServiceServiceImpl contains implementation for TestcaseResult service.
 *
 * @author Dhruv
 */
@Service
public class TestcaseResultServiceServiceImpl implements TestcaseResultService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseResultServiceServiceImpl.class);

    @Autowired
    private TestcaseResultRepository testcaseResultRepository;

    @Autowired
    TestcaseResultMapper testcaseResultMapper;

    @Autowired
    private TestcaseOptionService testcaseOptionService;

    @Autowired
    private TestRequestService testRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private GradeEvaluator gradeEvaluator;

    @Autowired
    private AuditService auditService;

    @Autowired
    private TestResultRelationService testResultRelationService;

    @Autowired
    SimpMessagingTemplate msgTemplate;

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseResultEntity createTestcaseResult(TestcaseResultEntity testcaseResultEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {

        if (testcaseResultEntity == null) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION+ TestcaseResultServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("TestcaseResultEntity is missing");
        }

        defaultValueCreateTestCaseResult(testcaseResultEntity, contextInfo);

        TestcaseResultValidator.validateCreateUpdateTestCaseResult(Constant.CREATE_VALIDATION,
                this,
                userService,
                testcaseOptionService,
                testRequestService,
                testcaseResultEntity,
                contextInfo);

        if(!testcaseResultEntity.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI))
            testcaseResultEntity.setMessage(null);
        testcaseResultEntity = testcaseResultRepository.saveAndFlush(testcaseResultEntity);

        return testcaseResultEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */

    @Override
    public TestcaseResultEntity updateTestcaseResult(TestcaseResultEntity testcaseResultEntity,
                                                     ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (testcaseResultEntity == null) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION+ TestcaseResultServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("TestcaseResultEntity is missing");
        }

        TestcaseResultValidator.validateCreateUpdateTestCaseResult(Constant.UPDATE_VALIDATION,
                this,
                userService,
                testcaseOptionService,
                testRequestService,
                testcaseResultEntity,
                contextInfo);

        if(!testcaseResultEntity.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI))
            testcaseResultEntity.setMessage(null);
        testcaseResultEntity = testcaseResultRepository.saveAndFlush(testcaseResultEntity);


        return testcaseResultEntity;
    }


    @Override
    public TestcaseResultEntity submitTestcaseResult(String testcaseResultId, Set<String> selectedTestcaseOptionIds, ContextInfo contextInfo) throws OperationFailedException, VersionMismatchException, DataValidationErrorException, InvalidParameterException, DoesNotExistException {

        TestcaseResultValidator.validateSubmitTestcaseResult(
                testcaseResultId,
                selectedTestcaseOptionIds,
                Constant.SUBMIT_VALIDATION,
                this,
                testcaseOptionService,
                testResultRelationService,
                auditService,
                contextInfo);

        TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter = new TestResultRelationCriteriaSearchFilter();
        testResultRelationCriteriaSearchFilter.setTestcaseResultId(testcaseResultId);
        testResultRelationCriteriaSearchFilter.setRefObjUri(TestcaseOptionServiceConstants.TESTCASE_OPTION_REF_OBJ_URI);

        List<TestResultRelationEntity> testResultRelationEntities = testResultRelationService.searchTestResultRelation(testResultRelationCriteriaSearchFilter, contextInfo);

        List<String> resultRelationIds = testResultRelationEntities.stream().map(IdMetaEntity::getId).collect(Collectors.toList());

        List<TestcaseOptionEntity> testcaseOptionEntitiesFromAudit =
                testResultRelationService.getTestResultRelationEntitiesFromAuditMapping(resultRelationIds, contextInfo)
                        .stream()
                        .map(TestcaseOptionEntity.class::cast)
                        .toList();


        for (TestResultRelationEntity testResultRelationEntity : testResultRelationEntities) {
            testResultRelationEntity.setSelected(selectedTestcaseOptionIds.contains(testResultRelationEntity.getRefId()));
            testResultRelationService.updateTestcaseResult(testResultRelationEntity,contextInfo);
        }


        boolean isSuccess = isSuccessFromTestResultRelationAndSelectedOption(testcaseResultId , testResultRelationEntities, testcaseOptionEntitiesFromAudit, contextInfo);


        TestcaseResultEntity testcaseResultEntity
                = this.getTestcaseResultById(testcaseResultId, contextInfo);

        testcaseResultEntity.setSuccess(isSuccess);

        UserEntity userEntity =
                userService.getPrincipalUser(contextInfo);
        testcaseResultEntity.setTester(userEntity);

        testcaseResultEntity = testcaseResultRepository.saveAndFlush(testcaseResultEntity);

        changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);

        return testcaseResultEntity;
    }


    private boolean isSuccessFromTestResultRelationAndSelectedOption(String testcaseResultId, List<TestResultRelationEntity> testResultRelationEntities, List<TestcaseOptionEntity> testcaseOptionEntitiesFromAudit, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {


        TestcaseEntity auditTestcaseEntity = getTestcaseEntityFromAuditMapping(testcaseResultId, contextInfo);


        boolean isSingleSelectQuestion = TestcaseServiceConstants.QuestionType.SINGLE_SELECT.name().equals(auditTestcaseEntity.getQuestionType());


        boolean isTestSuccessful = !isSingleSelectQuestion;

        for (TestResultRelationEntity testResultRelationEntity : testResultRelationEntities) {
            // Check if the testResultRelationEntity is marked as success or failure
            boolean isRelationSuccess = isTestResultRelationSuccess(testResultRelationEntity, testcaseOptionEntitiesFromAudit);

            if(isSingleSelectQuestion){

                if (testResultRelationEntity.getSelected() == isRelationSuccess) {
                    isTestSuccessful = true;
                    break;
                }
            } else {

                if (testResultRelationEntity.getSelected() != isRelationSuccess) {
                    isTestSuccessful = false;
                    break;
                }
            }
        }

        return isTestSuccessful;
    }

    private TestcaseEntity getTestcaseEntityFromAuditMapping(String testcaseResultId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException, InvalidParameterException, DataValidationErrorException {
        List<Object> auditTestcaseEntities = testResultRelationService.getTestResultRelationEntitiesFromAuditMapping(testcaseResultId, TestcaseServiceConstants.TESTCASE_REF_OBJ_URI, contextInfo);
        return auditTestcaseEntities.stream().findFirst().map(TestcaseEntity.class::cast).get();
    }


    private boolean isTestResultRelationSuccess(TestResultRelationEntity testResultRelationEntity, List<TestcaseOptionEntity> testcaseOptionEntitiesFromAudit) {
        return testcaseOptionEntitiesFromAudit.stream()
                .filter(option -> option.getId().equals(testResultRelationEntity.getRefId()))
                .anyMatch(TestcaseOptionEntity::getSuccess);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<TestcaseResultEntity> searchTestcaseResults(
            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<TestcaseResultEntity> testcaseEntitySpecification = testcaseResultCriteriaSearchFilter.buildSpecification(contextInfo);
        return testcaseResultRepository.findAll(testcaseEntitySpecification, pageable);
    }

    @Override
    public List<TestcaseResultEntity> searchTestcaseResults(
            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseResultEntity> testcaseResultEntitySpecification = testcaseResultCriteriaSearchFilter.buildSpecification(contextInfo);
        return testcaseResultRepository.findAll(testcaseResultEntitySpecification);
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
        if (!StringUtils.hasLength(testcaseResultId)) {
            throw new InvalidParameterException("TestcaseResultId is missing");
        }
        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter(testcaseResultId);
        List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(testcaseResultCriteriaSearchFilter, contextInfo);
        return testcaseResultEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("TestcaseResult does not found with id : " + testcaseResultId));
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
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION+ TestcaseResultServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("TestcaseResultEntity is missing");
        }
        List<ValidationResultInfo> errors = TestcaseResultValidator.validateTestCaseResult(validationTypeKey, testcaseResultEntity, userService, this, testcaseOptionService, testRequestService, contextInfo);
        return errors;
    }

    @Override
    public TestcaseResultEntity changeState(String testcaseResultId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {

        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        ValidationUtils.statusPresent(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS, stateKey, errors);

        TestcaseResultEntity testcaseResultEntity = this.getTestcaseResultById(testcaseResultId, contextInfo);

        //validate transition
        ValidationUtils.transitionValid(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_MAP, testcaseResultEntity.getState(), stateKey, errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    errors);
        }

        if (!stateKey.equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED)) {
            testcaseResultEntity.setMessage(null);
            testcaseResultEntity.setHasSystemError(Boolean.FALSE);
            testcaseResultEntity.setSuccess(Boolean.FALSE);
            testcaseResultEntity.setSuccess(Boolean.FALSE);
        }

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    errors);
        }

        testcaseResultEntity.setState(stateKey);

        if(!testcaseResultEntity.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI))
            testcaseResultEntity.setMessage(null);
        testcaseResultEntity = testcaseResultRepository.saveAndFlush(testcaseResultEntity);

        // Notify client if the state is changed to finished
        if (TestcaseServiceConstants.TESTCASE_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())) {
            notifyTestCaseFinished(
                    testcaseResultEntity.getId(),
                    testcaseResultEntity.getManual().equals(Boolean.TRUE) ? null : Boolean.TRUE,
                    testcaseResultEntity.getManual().equals(Boolean.TRUE) ? Boolean.TRUE : null,
                    contextInfo
            );
        }

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
        searchFilter.setTestRequestId(testRequestEntity.getId());

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
                .allMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT))) {
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
                Long duration = 0L;
                for (TestcaseResultEntity tcr : testcaseResultEntities) {
                    if (tcr.getDuration() != null) {
                        duration = duration + tcr.getDuration();
                    }
                }
                testcaseResultEntity.setSuccess(
                        testcaseResultEntities.stream()
                                .allMatch(tre -> {
                                    return !Objects.equals(tre.getRequired(), Boolean.TRUE)
                                            || Objects.equals(tre.getSuccess(), Boolean.TRUE);
                                }));
                testcaseResultEntity.setDuration(duration);
                updateTestcaseResult(testcaseResultEntity, contextInfo);
                changeState(testcaseResultEntity.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);
                updateTestcaseResult(testcaseResultEntity, contextInfo);
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

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseResultEntity getTestcaseResultStatus(
            String testcaseResultId,
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException {
        if (!StringUtils.hasLength(testcaseResultId)) {
            throw new InvalidParameterException("TestcaseResultId is missing");
        }

        TestcaseResultEntity testcaseResultEntity = this.getTestcaseResultById(testcaseResultId, contextInfo);

        return fetchTestcaseResultStatusByInputs(
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                testcaseResultEntity,
                contextInfo);
    }

    @Override
    public List<String> getSubClassesNameForTestCase() {
        Reflections reflections = new Reflections(TestcaseServiceConstants.PACKAGE_NAME);
        Set<Class<? extends TestCase>> subTypes = reflections.getSubTypesOf(TestCase.class);
        List<String> classNames = new ArrayList<>();
        for (Class<?> subType : subTypes) {
            classNames.add(subType.getSimpleName());
        }
        return classNames;
    }

    private TestcaseResultEntity fetchTestcaseResultStatusByInputs(
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            TestcaseResultEntity testcaseResultEntity,
            ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException{


        testcaseResultEntity = new TestcaseResultEntity(testcaseResultEntity);


        if (testcaseResultEntity.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI)) {
            if(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS.equals(testcaseResultEntity.getState())) {
                testcaseResultEntity.setDuration(System.currentTimeMillis() - testcaseResultEntity.getUpdatedAt().getTime());
            }
            return testcaseResultEntity;
        }

        List<TestcaseResultEntity> filteredTestcaseResults = new ArrayList<>();

        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
        testcaseResultCriteriaSearchFilter.setTestRequestId(testcaseResultEntity.getTestRequest().getId());
        testcaseResultCriteriaSearchFilter.setManual(isManual);
        testcaseResultCriteriaSearchFilter.setAutomated(isAutomated);
        testcaseResultCriteriaSearchFilter.setRequired(isRequired);
        testcaseResultCriteriaSearchFilter.setRecommended(isRecommended);
        testcaseResultCriteriaSearchFilter.setFunctional(isFunctional);
        testcaseResultCriteriaSearchFilter.setWorkflow(isWorkflow);

        List<TestcaseResultEntity> testcaseResultEntities = this.searchTestcaseResults(testcaseResultCriteriaSearchFilter, Constant.FULL_PAGE, contextInfo).getContent();

        recalculateTestcaseResultEntity(testcaseResultEntity, testcaseResultEntities);

        if (Boolean.TRUE.equals(isRecommended) && testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED)) {
            if (ComponentServiceConstants.COMPONENT_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())) {
                List<TestcaseResultEntity> specificationsFromComponent = getChildTestcaseResultFromParentTestcaseResult(testcaseResultEntity, testcaseResultEntities);
                for (TestcaseResultEntity specificationTestcase : specificationsFromComponent) {
                    recalculateTestcaseResultEntity(specificationTestcase, testcaseResultEntities);
                }
                testcaseResultEntity.setGrade(gradeEvaluator.evaluate(specificationsFromComponent, contextInfo));
            } else if (TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI.equals(testcaseResultEntity.getRefObjUri())) {
                List<TestcaseResultEntity> componentFromTestRequest = getChildTestcaseResultFromParentTestcaseResult(testcaseResultEntity, testcaseResultEntities);

                for (TestcaseResultEntity componentTestRequest : componentFromTestRequest) {
                    List<TestcaseResultEntity> specificationsFromComponent = getChildTestcaseResultFromParentTestcaseResult(componentTestRequest, testcaseResultEntities);
                    for (TestcaseResultEntity specificationTestcase : specificationsFromComponent) {
                        recalculateTestcaseResultEntity(specificationTestcase, testcaseResultEntities);
                    }
                    componentTestRequest.setSuccess(specificationsFromComponent.stream().allMatch(entity -> Boolean.TRUE.equals(entity.getSuccess())));
                }
                testcaseResultEntity.setGrade(gradeEvaluator.evaluate(componentFromTestRequest, contextInfo));
            }
        }
        return testcaseResultEntity;
    }

    private void recalculateTestcaseResultEntity(TestcaseResultEntity testcaseResultEntity, List<TestcaseResultEntity> testcaseResultEntities) {
        List<TestcaseResultEntity> filteredTestcaseResults;
        if (testcaseResultEntity.getRefObjUri().equals(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI)) {
            filteredTestcaseResults = getFilteredChileTestcaseResultsForTestResult(testcaseResultEntity, testcaseResultEntities);

                boolean allTestcasesFinished = true;
                for (TestcaseResultEntity testcaseResult : filteredTestcaseResults){
                    if(!testcaseResult.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED) && !testcaseResult.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP)){
                        allTestcasesFinished = false;
                        break;
                    }
                }
                if(allTestcasesFinished) {
                    List<String> failedTestcaseResultName = filteredTestcaseResults.stream().filter(tcre -> tcre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED) && !tcre.getSuccess()).map(IdStateNameMetaEntity::getName).toList();
                    String message;
                    if(failedTestcaseResultName.isEmpty()){
                        message = "Passed";
                    }
                    else{
                        message = getMessage("Specification <b>", testcaseResultEntity, "</b> has been failed due to failing following testcase failure:", failedTestcaseResultName);
                    }
                    testcaseResultEntity.setMessage(message);
                }


        } else if (testcaseResultEntity.getRefObjUri().equals(ComponentServiceConstants.COMPONENT_REF_OBJ_URI)) {
            //Change to info
            filteredTestcaseResults = getFilteredChileTestcaseResultsForTestResult(testcaseResultEntity, testcaseResultEntities);

            for(TestcaseResultEntity tcr: filteredTestcaseResults) {
                this.recalculateTestcaseResultEntity(tcr, testcaseResultEntities);
            }
            boolean allTestcasesFinished = true;
            for (TestcaseResultEntity specificationResultEntity : filteredTestcaseResults){
                if(!specificationResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED) && !specificationResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP)){
                    allTestcasesFinished = false;
                    break;
                }
            }
            if(allTestcasesFinished) {
                List<String> failedSpecificationTestcaseResultName = filteredTestcaseResults.stream().filter(entity -> entity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED) && !entity.getSuccess()).map(IdStateNameMetaEntity::getName)
                        .toList();
                String message;
                if(failedSpecificationTestcaseResultName.isEmpty()) {
                    message = "Passed";
                } else {
                    message = getMessage("Component <b>", testcaseResultEntity, "</b> has been failed due to failing following specification failure:", failedSpecificationTestcaseResultName);
                }
                testcaseResultEntity.setMessage(message);
            }
        } else {
            filteredTestcaseResults
                    = testcaseResultEntities.stream()
                    .filter(tcre -> {
                        return tcre.getRefObjUri().equals(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI);
                    })
                    .collect(Collectors.toList());
        }

        if (filteredTestcaseResults.stream()
                .allMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP))) {
            testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP);
        } else if (filteredTestcaseResults.stream()
                .allMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED)
                        || tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP))) {
            //set duration
            Long duration = 0L;
            for (TestcaseResultEntity tcr : filteredTestcaseResults) {
                if (tcr.getDuration() != null) {
                    duration = duration + tcr.getDuration();
                }
            }
            testcaseResultEntity.setDuration(duration);

            testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED);
        } else if (filteredTestcaseResults.stream()
                .anyMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS))) {
            //set duration
            Long duration = System.currentTimeMillis() - testcaseResultEntity.getUpdatedAt().getTime();
            for (TestcaseResultEntity tcr : testcaseResultEntities) {
                if (tcr.getDuration() != null) {
                    duration = duration + tcr.getDuration();
                }
            }
            testcaseResultEntity.setDuration(duration);


            testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS);
        } else if (filteredTestcaseResults.stream()
                .anyMatch(tre -> tre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING))) {
            testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING);
        } else {
            testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT);
        }

        //set success
        testcaseResultEntity.setSuccess(filteredTestcaseResults.stream().allMatch(testcaseResult -> Boolean.TRUE.equals(testcaseResult.getSuccess())));
    }

    private static String getMessage(String x, TestcaseResultEntity testcaseResultEntity, String x1, List<String> failedSpecificationTestcaseResultName) {
        String message = x + testcaseResultEntity.getName() + x1;
        for (int i = 0; i < (failedSpecificationTestcaseResultName.size() - 1   ); i++) {
            message = message + " <b>" + failedSpecificationTestcaseResultName.get(i) + "<b>,";
        }
        if(failedSpecificationTestcaseResultName.size() > 1) {
            message = message + " and";
        }

        message = message + "<b>" + failedSpecificationTestcaseResultName.get(failedSpecificationTestcaseResultName.size() - 1) + "<b>";

        return message;
    }

    private static List<TestcaseResultEntity> getChildTestcaseResultFromParentTestcaseResult(TestcaseResultEntity testcaseResultEntity, List<TestcaseResultEntity> testcaseResultEntities) {
        List<TestcaseResultEntity> specificationTestcaseResultIds = testcaseResultEntities.stream()
                .filter(tcre -> {
                    return tcre.getParentTestcaseResult() != null
                            && tcre.getParentTestcaseResult().getId().equals(testcaseResultEntity.getId());
                }).collect(Collectors.toList());
        return specificationTestcaseResultIds;
    }

    private static List<TestcaseResultEntity> getFilteredChileTestcaseResultsForTestResult(TestcaseResultEntity testcaseResultEntity, List<TestcaseResultEntity> testcaseResultEntities) {
        List<TestcaseResultEntity> filteredTestcaseResults;
        filteredTestcaseResults
                = testcaseResultEntities.stream()
                .filter(tcre -> {
                    return tcre.getParentTestcaseResult() != null
                            && tcre.getParentTestcaseResult().getId().equals(testcaseResultEntity.getId());
                }).collect(Collectors.toList());
        return filteredTestcaseResults;
    }


    private void defaultValueCreateTestCaseResult(TestcaseResultEntity testcaseResultEntity, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, OperationFailedException {
        if (!StringUtils.hasLength(testcaseResultEntity.getId())) {
            testcaseResultEntity.setId(UUID.randomUUID().toString());
        }
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT);
        testcaseResultEntity.setDuration(null);
    }

    private void defaultValueSubmitTestCaseResult(TestcaseResultEntity testcaseResultEntity, String selectedTestcaseOptionId, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, OperationFailedException {
        testcaseResultEntity.setTester(userService.getPrincipalUser(contextInfo));

        TestcaseOptionEntity testcaseOptionEntity
            = testcaseOptionService.getTestcaseOptionById(selectedTestcaseOptionId, contextInfo);

        testcaseResultEntity.setSuccess(testcaseOptionEntity.getSuccess());
    }

    public void notifyTestCaseFinished(String testcaseResultEntityId, Boolean isAutomated, Boolean isManual, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DoesNotExistException {
        TestcaseResultEntity testcaseResultEntity = null;
        testcaseResultEntity = this.getTestcaseResultById(testcaseResultEntityId, contextInfo);
        String destination = "/testcase-result/" + testcaseResultEntity.getId();
        testcaseResultEntity = fetchTestcaseResultStatusByInputs(
                isManual,
                isAutomated,
                null,
                null,
                null,
                null,
                testcaseResultEntity,
                contextInfo
        );
        System.out.println("*******" + destination);
        msgTemplate.convertAndSend(destination, testcaseResultMapper.modelToDto(testcaseResultEntity));
        if (testcaseResultEntity.getParentTestcaseResult() != null) {
            notifyTestCaseFinished(testcaseResultEntity.getParentTestcaseResult().getId(), isAutomated, isManual, contextInfo);
        }
    }
}
