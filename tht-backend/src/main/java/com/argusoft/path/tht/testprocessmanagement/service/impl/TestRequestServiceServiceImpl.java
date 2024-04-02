package com.argusoft.path.tht.testprocessmanagement.service.impl;

import com.argusoft.path.tht.fileservice.constant.DocumentServiceConstants;
import com.argusoft.path.tht.fileservice.filter.DocumentCriteriaSearchFilter;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.fileservice.service.DocumentService;
import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.notificationmanagement.service.NotificationService;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.evaluator.GradeEvaluator;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultViewInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultRepository;
import com.argusoft.path.tht.reportmanagement.service.TestResultRelationService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.email.service.EmailService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.MetaInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.models.entity.IdStateNameMetaEntity;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.CommonStateChangeValidator;
import com.argusoft.path.tht.systemconfiguration.utils.CommonUtil;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.util.TestcaseExecutioner;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.dto.*;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.testprocessmanagement.validator.TestRequestValidator;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.filter.UserSearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This TestRequestServiceServiceImpl contains implementation for TestRequest service.
 *
 * @author Dhruv
 */
@Service
public class TestRequestServiceServiceImpl implements TestRequestService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestRequestServiceServiceImpl.class);

    private TestcaseResultRepository testcaseResultRepository;

    private TestRequestRepository testRequestRepository;

    private TestcaseResultService testcaseResultService;

    private ComponentService componentService;

    private SpecificationService specificationService;

    private TestcaseService testcaseService;

    private UserService userService;

    private TestcaseExecutioner testcaseExecutioner;

    private TestResultRelationService testResultRelationService;

    private TestcaseOptionService testcaseOptionService;

    private DocumentService documentService;

    private EmailService emailService;

    private NotificationService notificationService;

    @Value("${message-configuration.test-request.create.mail}")
    private boolean testRequestCreateMail;

    @Value("${message-configuration.test-request.create.notification}")
    private boolean testRequestCreateNotification;

    @Value("${message-configuration.test-request.accept.mail}")
    private boolean testRequestAcceptMail;

    @Value("${message-configuration.test-request.accept.notification}")
    private boolean testRequestAcceptNotification;

    @Value("${message-configuration.test-request.reject.mail}")
    private boolean testRequestRejectMail;

    @Value("${message-configuration.test-request.reject.notification}")
    private boolean testRequestRejectNotification;

    @Value("${message-configuration.test-request.finish.mail}")
    private boolean testRequestFinishMail;

    @Value("${message-configuration.test-request.finish.notification}")
    private boolean testRequestFinishNotification;
    private GradeEvaluator gradeEvaluator;


    @Autowired
    public void setTestcaseResultRepository(TestcaseResultRepository testcaseResultRepository) {
        this.testcaseResultRepository = testcaseResultRepository;
    }

    @Autowired
    public void setTestRequestRepository(TestRequestRepository testRequestRepository) {
        this.testRequestRepository = testRequestRepository;
    }

    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }

    @Autowired
    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    @Autowired
    public void setSpecificationService(SpecificationService specificationService) {
        this.specificationService = specificationService;
    }

    @Autowired
    public void setTestcaseService(TestcaseService testcaseService) {
        this.testcaseService = testcaseService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTestcaseExecutioner(TestcaseExecutioner testcaseExecutioner) {
        this.testcaseExecutioner = testcaseExecutioner;
    }

    @Autowired
    public void setTestResultRelationService(TestResultRelationService testResultRelationService) {
        this.testResultRelationService = testResultRelationService;
    }

    @Autowired
    public void setTestcaseOptionService(TestcaseOptionService testcaseOptionService) {
        this.testcaseOptionService = testcaseOptionService;
    }

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) { this.notificationService = notificationService; }

    @Autowired
    public void setGradeEvaluator(GradeEvaluator gradeEvaluator) {
        this.gradeEvaluator = gradeEvaluator;
    }

    @Override
    public void stopTestingProcess(
            String testRequestId,
            String refObjUri,
            String refId,
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            Boolean changeOnlyIfNotSubmitted,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {
        TestRequestValidator.validateTestRequestStartReinitializeProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                Constant.STOP_PROCESS_VALIDATION,
                testcaseResultService,
                contextInfo);

        testcaseExecutioner.stopTestingProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                changeOnlyIfNotSubmitted,
                contextInfo);
    }

    @Override
    public void startTestingProcess(
            String testRequestId,
            String refObjUri,
            String refId,
            Boolean isManual,
            Boolean isAutomated,
            Boolean isRequired,
            Boolean isRecommended,
            Boolean isWorkflow,
            Boolean isFunctional,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException,
            DataValidationErrorException {
        TestRequestValidator.validateTestRequestStartReinitializeProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                Constant.START_PROCESS_VALIDATION,
                testcaseResultService,
                contextInfo);

        testcaseExecutioner.executeTestingProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestRequestEntity createTestRequest(TestRequestEntity testRequestEntity,
                                               ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {

        if (testRequestEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestRequestServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("TestRequestEntity is missing");
        }

        defaultValueCreateTestRequest(testRequestEntity, contextInfo);

        TestRequestValidator.validateCreateUpdateTestRequest(Constant.CREATE_VALIDATION,
                testRequestEntity,
                this,
                userService,
                componentService,
                contextInfo);

        messageAdminsIfTestRequestCreated(contextInfo);

        //Create state change API to make this as Accepted or Rejected
        testRequestEntity = testRequestRepository.saveAndFlush(testRequestEntity);
        return testRequestEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestRequestEntity updateTestRequest(TestRequestEntity testRequestEntity,
                                               ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (testRequestEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestRequestServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("TestRequestEntity is missing");
        }

        TestRequestValidator.validateCreateUpdateTestRequest(Constant.UPDATE_VALIDATION,
                testRequestEntity,
                this,
                userService,
                componentService,
                contextInfo);

        testRequestEntity = testRequestRepository.saveAndFlush(testRequestEntity);
        return testRequestEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<TestRequestEntity> searchTestRequests(
            TestRequestCriteriaSearchFilter testRequestSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<TestRequestEntity> testRequestEntitySpecification = testRequestSearchFilter.buildSpecification(pageable, contextInfo);
        return testRequestRepository.findAll(testRequestEntitySpecification, CommonUtil.getPageable(pageable));
    }


    @Override
    public List<TestRequestEntity> searchTestRequests(
            TestRequestCriteriaSearchFilter testRequestSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<TestRequestEntity> testRequestEntitySpecification = testRequestSearchFilter.buildSpecification(contextInfo);
        return testRequestRepository.findAll(testRequestEntitySpecification);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestRequestEntity getTestRequestById(String testRequestId,
                                                ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (!StringUtils.hasLength(testRequestId)) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestRequestServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("TestRequestId is missing");
        }
        TestRequestCriteriaSearchFilter testRequestCriteriaSearchFilter = new TestRequestCriteriaSearchFilter(testRequestId);
        List<TestRequestEntity> testRequestEntities = this.searchTestRequests(testRequestCriteriaSearchFilter, contextInfo);
        return testRequestEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("TestRequest does not found with id : " + testRequestId));
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<ValidationResultInfo> validateTestRequest(
            String validationTypeKey,
            TestRequestEntity testRequestEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (testRequestEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestRequestServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("TestRequestEntity is missing");
        }
        return TestRequestValidator.validateTestRequest(validationTypeKey, testRequestEntity, this, userService, componentService, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateChangeState(String testRequestId, String stateKey, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        try {
            TestRequestEntity testRequestEntity = this.getTestRequestById(testRequestId, contextInfo);
            TestRequestValidator.validateChangeState(testRequestEntity, stateKey, componentService, specificationService, testcaseService, testcaseOptionService, errors, contextInfo);
            CommonStateChangeValidator.validateStateChangeByMap(TestRequestServiceConstants.TEST_REQUEST_STATUS, TestRequestServiceConstants.TEST_REQUEST_STATUS_MAP, testRequestEntity.getState(), stateKey, errors);
        } catch (DoesNotExistException ex) {
            errors.add(
                    new ValidationResultInfo("id",
                            ErrorLevel.ERROR,
                            ValidateConstant.ID_SUPPLIED + "update" + ValidateConstant.DOES_NOT_EXIST));
        }
        return errors;
    }

    @Override
    public List<TestRequestViewInfo> getApplicationsStats(ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException, RuntimeException{

        //Fetch all test requests
        List<TestRequestEntity> testRequestEntities = testRequestRepository.findAll();

        List<TestRequestViewInfo> testRequestViewInfos = new ArrayList<>();

        //Add TestRequestViewInfo for every test request entity found
        testRequestEntities.forEach(testRequestEntity -> {

            //TestRequestInfo Model to DTO
            TestRequestViewInfo testRequestViewInfo = new TestRequestViewInfo();
            testRequestViewInfo.setId(testRequestEntity.getId());
            testRequestViewInfo.setState(testRequestEntity.getState());
            testRequestViewInfo.setName(testRequestEntity.getName());
            testRequestViewInfo.setAssesseeName(testRequestEntity.getAssessee().getName());
            testRequestViewInfo.setAssesseeEmail(testRequestEntity.getAssessee().getEmail());
            MetaInfo meta = new MetaInfo();
            meta.setCreatedAt(testRequestEntity.getCreatedAt());
            meta.setUpdatedAt(testRequestEntity.getUpdatedAt());
            meta.setCreatedBy(testRequestEntity.getCreatedBy());
            meta.setUpdatedBy(testRequestEntity.getUpdatedBy());
            meta.setVersion(testRequestEntity.getVersion());
            testRequestViewInfo.setMeta(meta);

            //Search for all testcases on this test request
            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
            testcaseResultCriteriaSearchFilter.setTestRequestId(testRequestEntity.getId());
            testcaseResultCriteriaSearchFilter.setRefObjUri(ComponentServiceConstants.COMPONENT_REF_OBJ_URI);
            List<TestcaseResultEntity> testcaseResultEntities;

            try {
                testcaseResultEntities = testcaseResultService.searchTestcaseResults(
                        testcaseResultCriteriaSearchFilter,
                        contextInfo);
            } catch (OperationFailedException | InvalidParameterException e) {
                throw new RuntimeException(e);
            }

            List<ComponentEntity> components = testRequestEntity.getTestRequestUrls().stream().map(TestRequestUrlEntity::getComponent).collect(Collectors.toList());



            List<TestcaseResultViewInfo> testResultOfComponents = new ArrayList<>();

            testcaseResultEntities
                    .forEach(componentResult -> {

//                        components = components.stream()
//                                                .filter(componentEntity -> !(componentEntity.getId().equals(componentResult.getId())))
//                                                .collect(Collectors.toList());

                        components.removeIf(componentEntity -> componentEntity.getId().equals(componentResult.getRefId()));

                        TestcaseResultViewInfo componentResultViewInfo = new TestcaseResultViewInfo();
                        componentResultViewInfo.setComponentId(componentResult.getId());
                        componentResultViewInfo.setComponentName(componentResult.getName());
                        String testRequestState = testRequestEntity.getState();
                        if(TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED.equals(testRequestState) || TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS.equals(testRequestState) || TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED.equals(testRequestState)){
                            componentResultViewInfo.setTestcaseResultState(componentResult.getState());
                        }
                        if(TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED.equals(testRequestState)){
                            componentResultViewInfo.setSuccess(componentResult.getSuccess());
                            componentResultViewInfo.setGrade(componentResult.getGrade());
                        }
                        testResultOfComponents.add(componentResultViewInfo);
                    });
            //add components to testResultOfComponents
            for(ComponentEntity component: components){
                TestcaseResultViewInfo inactiveComponent = new TestcaseResultViewInfo();
                inactiveComponent.setComponentId(component.getId());
                inactiveComponent.setComponentName(component.getName());


                testResultOfComponents.add(inactiveComponent);
            }

            testRequestViewInfo.setTestResultOfComponents(testResultOfComponents);

            //Search for all testcases on this test request
            testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
            testcaseResultCriteriaSearchFilter.setTestRequestId(testRequestEntity.getId());
            testcaseResultCriteriaSearchFilter.setRefObjUri(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI);
            List<TestcaseResultEntity> testRequestTestcaseResultEntity;

            try {
                testRequestTestcaseResultEntity = testcaseResultService.searchTestcaseResults(
                        testcaseResultCriteriaSearchFilter,
                        contextInfo);
            } catch (OperationFailedException | InvalidParameterException e) {
                throw new RuntimeException(e);
            }

            if(TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED.equals(testRequestEntity.getState())){
                TestcaseResultEntity testRequestTestcaseResult =testRequestTestcaseResultEntity.stream().filter(testcaseResultEntity -> testcaseResultEntity.getRefObjUri().equals(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI)).findFirst().get();
                testRequestViewInfo.setSuccess(testRequestTestcaseResult.getSuccess());
                testRequestViewInfo.setGrade(testRequestTestcaseResult.getGrade());
            }

            testRequestViewInfos.add(testRequestViewInfo);
        });

        return testRequestViewInfos;
    }

    @Override
    public TestRequestEntity changeState(String testRequestId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {

        List<ValidationResultInfo> errors = new ArrayList<>();

        TestRequestEntity testRequestEntity = this.getTestRequestById(testRequestId, contextInfo);

        defaultValueChangeState(testRequestEntity, stateKey, contextInfo);

        TestRequestValidator.validateChangeState(testRequestEntity, stateKey, componentService, specificationService, testcaseService, testcaseOptionService, errors, contextInfo);

        CommonStateChangeValidator.validateStateChange(TestRequestServiceConstants.TEST_REQUEST_STATUS, TestRequestServiceConstants.TEST_REQUEST_STATUS_MAP, testRequestEntity.getState(), stateKey, errors);

        String oldState = testRequestEntity.getState();

        testRequestEntity.setState(stateKey);
        testRequestEntity = testRequestRepository.saveAndFlush(testRequestEntity);

        UserEntity requestingUser = testRequestEntity.getAssessee();

        sendMailToTheUserOnChangeState(oldState, stateKey, requestingUser, testRequestEntity.getName(), contextInfo);

        changeStateCallback(testRequestEntity, contextInfo);
        return testRequestEntity;
    }

    private void sendMailToTheUserOnChangeState(String oldState, String newState, UserEntity requestingUser, String testRequestName, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        if (TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING.equals(oldState) && TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED.equals(newState)) {
            messageAssesseeIfTestRequestAccepted(requestingUser, testRequestName, contextInfo);
        } else if (TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING.equals(oldState) && TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED.equals(newState)) {
            messageAssesseeIfTestRequestRejected(requestingUser, testRequestName, contextInfo);
        } else if (TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS.equals(oldState) && TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED.equals(newState)) {
            messageAssesseeIfTestRequestFinished(requestingUser, testRequestName, contextInfo);
        }
    }

    private void changeStateCallback(TestRequestEntity testRequestEntity, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        if (testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED)) {
            createDraftTestcaseResultsByTestRequestAndProcessKey(testRequestEntity, contextInfo);
        } else if (testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED)) {
            updateIsSuccessAndGradeForReport(testRequestEntity, contextInfo);
        }
    }

    private void createDraftTestcaseResultsByTestRequestAndProcessKey(TestRequestEntity testRequestEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        List<ComponentEntity> activeComponents = fetchActiveComponents(contextInfo)
                .stream().filter(componentEntity ->
                        testRequestEntity.getTestRequestUrls().stream().anyMatch(testRequestUrlEntity -> testRequestUrlEntity.getComponent().getId().equals(componentEntity.getId()))
                                && componentEntity.getSpecifications().stream().anyMatch(
                                specificationEntity -> {
                                    return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                                            && specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                                        return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                                    });
                                })
                ).collect(Collectors.toList());

        Integer counter = 1;
        if (!activeComponents.isEmpty()) {
            Boolean isManual = activeComponents.stream().anyMatch(componentEntity -> {
                return componentEntity.getSpecifications().stream().anyMatch(specificationEntity -> {
                    return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                            && specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                        return testcaseEntity.getManual()
                                && testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                    });
                });
            });
            Boolean isAutomated = activeComponents.stream().anyMatch(componentEntity -> {
                return componentEntity.getSpecifications().stream().anyMatch(specificationEntity -> {
                    return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                            && specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                        return !testcaseEntity.getManual()
                                && testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                    });
                });
            });
            Boolean isRequired = activeComponents.stream().anyMatch(componentEntity -> {
                return componentEntity.getSpecifications().stream().anyMatch(specificationEntity -> {
                    return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                            && specificationEntity.getRequired();
                });
            });
            Boolean isRecommended = activeComponents.stream().anyMatch(componentEntity -> {
                return componentEntity.getSpecifications().stream().anyMatch(specificationEntity -> {
                    return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                            && !specificationEntity.getRequired();
                });
            });
            Boolean isFunctional = activeComponents.stream().anyMatch(componentEntity -> {
                return componentEntity.getSpecifications().stream().anyMatch(specificationEntity -> {
                    return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                            && specificationEntity.getFunctional();
                });
            });
            Boolean isWorkflow = activeComponents.stream().anyMatch(componentEntity -> {
                return componentEntity.getSpecifications().stream().anyMatch(specificationEntity -> {
                    return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                            && !specificationEntity.getFunctional();
                });
            });

            TestcaseResultEntity testRequestTestcaseResult = createDraftTestCaseResultIfNotExists(
                    TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                    testRequestEntity.getId(),
                    testRequestEntity.getId(),
                    testRequestEntity.getName(),
                    counter,
                    isManual,
                    isAutomated,
                    isRequired,
                    isRecommended,
                    isFunctional,
                    isWorkflow,
                    null,
                    contextInfo);
            counter++;

            for (ComponentEntity componentEntity : activeComponents) {
                List<SpecificationEntity> activeSpecifications = componentEntity.getSpecifications().stream().filter(specificationEntity -> {
                            return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                                    && specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                                return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                            });
                        })
                        .sorted(Comparator.comparing(SpecificationEntity::getRank))
                        .collect(Collectors.toList());

                if (!activeSpecifications.isEmpty()) {
                    isManual = activeSpecifications.stream().anyMatch(specificationEntity -> {
                        return specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                            return testcaseEntity.getManual() && testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                        });
                    });
                    isAutomated = activeSpecifications.stream().anyMatch(specificationEntity -> {
                        return specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                            return !testcaseEntity.getManual() && testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                        });
                    });
                    isRequired = activeSpecifications.stream().anyMatch(specificationEntity -> {
                        return specificationEntity.getRequired();
                    });
                    isRecommended = activeSpecifications.stream().anyMatch(specificationEntity -> {
                        return !specificationEntity.getRequired();
                    });
                    isFunctional = activeSpecifications.stream().anyMatch(specificationEntity -> {
                        return specificationEntity.getFunctional();
                    });
                    isWorkflow = activeSpecifications.stream().anyMatch(specificationEntity -> {
                        return !specificationEntity.getFunctional();
                    });

                    TestcaseResultEntity componentTestcaseResult = createDraftTestCaseResultIfNotExists(
                            ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                            componentEntity.getId(),
                            testRequestEntity.getId(),
                            componentEntity.getName(),
                            counter,
                            isManual,
                            isAutomated,
                            isRequired,
                            isRecommended,
                            isFunctional,
                            isWorkflow,
                            testRequestTestcaseResult.getId(),
                            contextInfo);
                    counter++;

                    for (SpecificationEntity specificationEntity : activeSpecifications) {
                        List<TestcaseEntity> filteredTestcases = specificationEntity.getTestcases().stream().filter(testcaseEntity -> {
                                    return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                                })
                                .sorted(Comparator.comparing(TestcaseEntity::getRank))
                                .collect(Collectors.toList());

                        if (!filteredTestcases.isEmpty()) {
                            isManual = specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                                return testcaseEntity.getManual() && testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                            });
                            isAutomated = specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                                return !testcaseEntity.getManual() && testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
                            });
                            isRequired = specificationEntity.getRequired();
                            isRecommended = !specificationEntity.getRequired();
                            isFunctional = specificationEntity.getFunctional();
                            isWorkflow = !specificationEntity.getFunctional();
                            TestcaseResultEntity specificationTestcaseResult = createDraftTestCaseResultIfNotExists(
                                    SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                                    specificationEntity.getId(),
                                    testRequestEntity.getId(),
                                    specificationEntity.getName(),
                                    counter,
                                    isManual,
                                    isAutomated,
                                    isRequired,
                                    isRecommended,
                                    isFunctional,
                                    isWorkflow,
                                    componentTestcaseResult.getId(),
                                    contextInfo);
                            counter++;
                            for (TestcaseEntity testcaseEntity : filteredTestcases) {
                                TestcaseResultEntity testcaseResult = createDraftTestCaseResultIfNotExists(
                                        TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                                        testcaseEntity.getId(),
                                        testRequestEntity.getId(),
                                        testcaseEntity.getName(),
                                        counter,
                                        testcaseEntity.getManual(),
                                        !testcaseEntity.getManual(),
                                        isRequired,
                                        isRecommended,
                                        isFunctional,
                                        isWorkflow,
                                        specificationTestcaseResult.getId(),
                                        contextInfo);


                                // create TestResultRelation For Manual
                                createTestResultRelationsForManualTestcase(testcaseEntity, testcaseResult, contextInfo);

                                counter++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateIsSuccessAndGradeForReport(TestRequestEntity testRequestEntity, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
        testcaseResultCriteriaSearchFilter.setTestRequestId(testRequestEntity.getId());

        List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(
                testcaseResultCriteriaSearchFilter,
                contextInfo);

        TestcaseResultEntity testcaseResult = testcaseResultEntities.stream().filter(testcaseResultEntity -> testcaseResultEntity.getRefObjUri().equals(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI)).findFirst().get();

        recalculateTestcaseResultEntity(testcaseResult, testcaseResultEntities, contextInfo);
    }   

    private void recalculateTestcaseResultEntity(TestcaseResultEntity testcaseResultEntity, List<TestcaseResultEntity> testcaseResultEntities, ContextInfo contextInfo) {
        if (testcaseResultEntity.getRefObjUri().equals(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI)) {
            //FOR SPECIFICATION
            //set success
            List<TestcaseResultEntity> filteredTestcaseResults = getFilteredChileTestcaseResultsForTestResult(testcaseResultEntity, testcaseResultEntities);
            testcaseResultEntity.setSuccess(filteredTestcaseResults.stream().allMatch(testcaseResultEntity1 ->
                    testcaseResultEntity1.getState()
                            .equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP)
                            || Boolean.TRUE.equals(testcaseResultEntity1.getSuccess())));
            //set duration
            Long duration = 0L;
            for (TestcaseResultEntity tcr : filteredTestcaseResults) {
                if (tcr.getDuration() != null) {
                    duration = duration + tcr.getDuration();
                }
            }
            testcaseResultEntity.setDuration(duration);
            //Set message
            List<String> failedTestcaseResultName = filteredTestcaseResults.stream().filter(tcre -> tcre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED) && !tcre.getSuccess()).map(IdStateNameMetaEntity::getName).toList();
            String message;
            if (failedTestcaseResultName.isEmpty()) {
                message = "Passed";
            } else {
                message = getMessage("Specification <b>", testcaseResultEntity, "</b> has been failed due to failing following testcase failure: ", failedTestcaseResultName);
            }
            testcaseResultEntity.setMessage(message);

            //Set compliance of specification
            int compliance = GradeEvaluator.getComplianceForSpecification(filteredTestcaseResults);
            testcaseResultEntity.setCompliant(compliance);
            testcaseResultEntity.setNonCompliant(filteredTestcaseResults.size()-compliance);

        } else if (testcaseResultEntity.getRefObjUri().equals(ComponentServiceConstants.COMPONENT_REF_OBJ_URI)) {
            //FOR COMPONENT
            //update specifications
            List<TestcaseResultEntity> filteredTestcaseResults = getFilteredChileTestcaseResultsForTestResult(testcaseResultEntity, testcaseResultEntities);
            for(TestcaseResultEntity testcaseResult: filteredTestcaseResults) {
                recalculateTestcaseResultEntity(testcaseResult, testcaseResultEntities, contextInfo);
            }
            //set success
            testcaseResultEntity.setSuccess(filteredTestcaseResults.stream().allMatch(testcaseResultEntity1 ->
                    !testcaseResultEntity1.getRequired() || Boolean.TRUE.equals(testcaseResultEntity1.getSuccess())));
            //set duration
            Long duration = 0L;
            for (TestcaseResultEntity tcr : filteredTestcaseResults) {
                if (tcr.getDuration() != null) {
                    duration = duration + tcr.getDuration();
                }
            }
            testcaseResultEntity.setDuration(duration);
            //set message
            List<String> failedTestcaseResultName = filteredTestcaseResults.stream().filter(tcre -> tcre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED) && !tcre.getSuccess()).map(IdStateNameMetaEntity::getName).toList();
            String message;
            if (failedTestcaseResultName.isEmpty()) {
                message = "Passed";
            } else {
                message = getMessage("Component <b>", testcaseResultEntity, "</b> has been failed due to failing following specification failure: ", failedTestcaseResultName);
            }
            testcaseResultEntity.setMessage(message);

            //Set Compliance of component
            testcaseResultEntity.setCompliant(GradeEvaluator.getCompliance(filteredTestcaseResults));
            testcaseResultEntity.setNonCompliant(GradeEvaluator.getNonCompliance(filteredTestcaseResults));


            //set grade
            testcaseResultEntity.setGrade(gradeEvaluator.evaluate(
                    filteredTestcaseResults.stream().filter(tcre -> !tcre.getRequired())
                            .collect(Collectors.toList()),
                    contextInfo)
            );
        } else if (testcaseResultEntity.getRefObjUri().equals(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI)) {
            //FOR TestRequest
            //update components
            List<TestcaseResultEntity> filteredTestcaseResults = getFilteredChileTestcaseResultsForTestResult(testcaseResultEntity, testcaseResultEntities);
            for(TestcaseResultEntity testcaseResult: filteredTestcaseResults) {
                recalculateTestcaseResultEntity(testcaseResult, testcaseResultEntities, contextInfo);
            }
            //set success
            testcaseResultEntity.setSuccess(filteredTestcaseResults.stream().allMatch(testcaseResultEntity1 ->
                    !testcaseResultEntity1.getRequired() || Boolean.TRUE.equals(testcaseResultEntity1.getSuccess())));
            //set duration
            Long duration = 0L;
            for (TestcaseResultEntity tcr : filteredTestcaseResults) {
                if (tcr.getDuration() != null) {
                    duration = duration + tcr.getDuration();
                }
            }
            testcaseResultEntity.setDuration(duration);
            //set message
            List<String> failedTestcaseResultName = filteredTestcaseResults.stream().filter(tcre -> tcre.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED) && !tcre.getSuccess()).map(IdStateNameMetaEntity::getName).toList();
            String message;
            if (failedTestcaseResultName.isEmpty()) {
                message = "Passed";
            } else {
                message = getMessage("Test Request <b>", testcaseResultEntity, "</b> has been failed due to failing following component failure: ", failedTestcaseResultName);
            }
            testcaseResultEntity.setMessage(message);

            //Set Compliance of Test Request
            if(testcaseResultEntity.getRefObjUri().equals(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI)){
                testcaseResultEntity.setCompliant(GradeEvaluator.getCompliance(filteredTestcaseResults));
                testcaseResultEntity.setNonCompliant(GradeEvaluator.getNonCompliance(filteredTestcaseResults));
            }

            //set grade
            testcaseResultEntity.setGrade(gradeEvaluator.evaluate(
                    testcaseResultEntities.stream().filter(tcre -> tcre.getRefObjUri().equals(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI) && !tcre.getRequired())
                            .collect(Collectors.toList()),
                    contextInfo)
            );
        }
    }

    private static List<TestcaseResultEntity> getFilteredChileTestcaseResultsForTestResult(TestcaseResultEntity testcaseResultEntity, List<TestcaseResultEntity> testcaseResultEntities) {
        return testcaseResultEntities.stream()
                .filter(tcre -> {
                    return tcre.getParentTestcaseResult() != null
                            && tcre.getParentTestcaseResult().getId().equals(testcaseResultEntity.getId());
                }).collect(Collectors.toList());
    }

    private void createTestResultRelationsForManualTestcase(TestcaseEntity testcaseEntity, TestcaseResultEntity testcaseResult, ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException {

        if (Boolean.TRUE.equals(testcaseEntity.getManual())) {

            // create for question
            TestResultRelationEntity testResultRelationEntity = createTestResultRelationEntity(
                    TestcaseServiceConstants.TESTCASE_REF_OBJ_URI,
                    testcaseEntity.getId(),
                    testcaseEntity.getVersion(),
                    testcaseResult
            );

            testResultRelationService.createTestcaseResult(testResultRelationEntity, contextInfo);


            // create for options
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter = new TestcaseOptionCriteriaSearchFilter();
            testcaseOptionCriteriaSearchFilter.setTestcaseId(testcaseEntity.getId());
            testcaseOptionCriteriaSearchFilter.setState(Collections.singletonList(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_ACTIVE));

            List<TestcaseOptionEntity> testcaseOptionEntities = testcaseOptionService.searchTestcaseOptions(testcaseOptionCriteriaSearchFilter, contextInfo);

            for (TestcaseOptionEntity testcaseOptionEntity : testcaseOptionEntities) {
                testResultRelationEntity = createTestResultRelationEntity(
                        TestcaseOptionServiceConstants.TESTCASE_OPTION_REF_OBJ_URI,
                        testcaseOptionEntity.getId(),
                        testcaseOptionEntity.getVersion(),
                        testcaseResult
                );

                testResultRelationService.createTestcaseResult(testResultRelationEntity, contextInfo);
            }

            // create for documents related to question
            DocumentCriteriaSearchFilter documentCriteriaSearchFilter = new DocumentCriteriaSearchFilter();
            documentCriteriaSearchFilter.setRefObjUri(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI);
            documentCriteriaSearchFilter.setRefId(testcaseEntity.getId());
            documentCriteriaSearchFilter.setState(Collections.singletonList(DocumentServiceConstants.DOCUMENT_STATUS_ACTIVE));

            List<DocumentEntity> documentEntities = documentService.searchDocument(documentCriteriaSearchFilter, contextInfo);

            for (DocumentEntity documentEntity : documentEntities) {
                testResultRelationEntity = createTestResultRelationEntity(
                        DocumentServiceConstants.DOCUMENT_REF_OBJ_URI,
                        documentEntity.getId(),
                        documentEntity.getVersion(),
                        testcaseResult
                );

                testResultRelationService.createTestcaseResult(testResultRelationEntity, contextInfo);
            }
        }
    }

    public TestResultRelationEntity createTestResultRelationEntity(String refObjUri, String refId, Long versionOfRefEntity, TestcaseResultEntity testcaseResult) {
        TestResultRelationEntity testResultRelationEntity = new TestResultRelationEntity();
        testResultRelationEntity.setRefObjUri(refObjUri);
        testResultRelationEntity.setRefId(refId);
        testResultRelationEntity.setVersionOfRefEntity(versionOfRefEntity);
        testResultRelationEntity.setTestcaseResultEntity(testcaseResult);

        return testResultRelationEntity;
    }


    private List<ComponentEntity> fetchActiveComponents(ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
        componentCriteriaSearchFilter.setState(Collections.singletonList(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE));
        return componentService.searchComponents(componentCriteriaSearchFilter, Constant.FULL_PAGE_SORT_BY_RANK, contextInfo).getContent();
    }

    private TestcaseResultEntity createDraftTestCaseResultIfNotExists(String refObjUri,
                                                                      String refId,
                                                                      String testRequestId,
                                                                      String name,
                                                                      Integer counter,
                                                                      Boolean isManual,
                                                                      Boolean isAutomated,
                                                                      Boolean isRequired,
                                                                      Boolean isRecommended,
                                                                      Boolean isFunctional,
                                                                      Boolean isWorkflow,
                                                                      String parentTestcaseResultId,
                                                                      ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
        searchFilter.setRefObjUri(refObjUri);
        searchFilter.setRefId(refId);
        searchFilter.setTestRequestId(testRequestId);
        searchFilter.setManual(isManual);

        List<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(
                searchFilter,
                Constant.FULL_PAGE,
                contextInfo).getContent();

        if (!testcaseResultEntities.isEmpty()) {
            return testcaseResultEntities.get(0);
        }

        TestcaseResultEntity testcaseResultEntity = new TestcaseResultEntity();
        testcaseResultEntity.setRefObjUri(refObjUri);
        testcaseResultEntity.setRefId(refId);
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT);

        TestRequestEntity testRequestEntity = new TestRequestEntity();
        testRequestEntity.setId(testRequestId);
        testcaseResultEntity.setTestRequest(testRequestEntity);

        testcaseResultEntity.setRank(counter);
        testcaseResultEntity.setName(name);
        testcaseResultEntity.setManual(isManual);
        testcaseResultEntity.setAutomated(isAutomated);
        testcaseResultEntity.setRequired(isRequired);
        testcaseResultEntity.setRecommended(isRecommended);
        testcaseResultEntity.setFunctional(isFunctional);
        testcaseResultEntity.setWorkflow(isWorkflow);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(contextInfo.getUsername());
        testcaseResultEntity.setTester(userEntity);
        if (StringUtils.hasLength(parentTestcaseResultId)) {
            TestcaseResultEntity parentTestcaseResult = new TestcaseResultEntity();
            parentTestcaseResult.setId(parentTestcaseResultId);
            testcaseResultEntity.setParentTestcaseResult(parentTestcaseResult);
        }
        return testcaseResultService.createTestcaseResult(testcaseResultEntity, contextInfo);
    }

    private void defaultValueCreateTestRequest(TestRequestEntity testRequestEntity, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, OperationFailedException {
        testRequestEntity.setState(TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING);
        UserEntity principalUser = userService.getPrincipalUser(contextInfo);
        if (principalUser.getRoles().stream().anyMatch(roleEntity -> UserServiceConstants.ROLE_ID_ASSESSEE.equals(roleEntity.getId()))) {
            testRequestEntity.setAssessee(principalUser);
        }
    }

    private void defaultValueChangeState(TestRequestEntity testRequestEntity, String stateKey, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, OperationFailedException {
        if (stateKey.equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED) || stateKey.equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED)) {
            testRequestEntity.setApprover(userService.getPrincipalUser(contextInfo));
        }
    }

    private void messageAdminsIfTestRequestCreated(ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        //Notify each admin that a test request is created
        List<UserEntity> admins = userService.getUsersByRole("role.admin", contextInfo);
        for (UserEntity admin : admins) {
            if (testRequestCreateMail) {
                emailService.testRequestCreatedMessage(admin.getEmail(), admin.getName(), contextInfo.getEmail());
            }
            if (testRequestCreateNotification) {
                NotificationEntity notificationEntity = new NotificationEntity("A new Test Request has been created by "+contextInfo.getEmail(), admin);
                notificationService.createNotification(notificationEntity, contextInfo);
            }
        }
    }

    private void messageAssesseeIfTestRequestAccepted(UserEntity requestingUser, String testRequestName, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        if(testRequestAcceptMail) {
            emailService.testRequestAcceptedMessage(requestingUser.getEmail(), requestingUser.getName(), testRequestName);
        }
        if(testRequestAcceptNotification) {
            NotificationEntity notificationEntity = new NotificationEntity("Your Test Request with name "+testRequestName+" has been accepted.",requestingUser);
            notificationService.createNotification(notificationEntity, contextInfo);
        }
    }

    private void messageAssesseeIfTestRequestRejected(UserEntity requestingUser, String testRequestName, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        if(testRequestRejectMail) {
            emailService.testRequestRejectedMessage(requestingUser.getEmail(), requestingUser.getName(), testRequestName);
        }
        if(testRequestRejectNotification) {
            NotificationEntity notificationEntity = new NotificationEntity("Your Test Request with name "+testRequestName+" has been rejected.",requestingUser);
            notificationService.createNotification(notificationEntity, contextInfo);
        }
    }

    private void messageAssesseeIfTestRequestFinished(UserEntity requestingUser, String testRequestName, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        if(testRequestFinishMail) {
            emailService.testRequestFinishedMessage(requestingUser.getEmail(), requestingUser.getName(), testRequestName);
        }
        if(testRequestFinishNotification) {
            NotificationEntity notificationEntity = new NotificationEntity("Your Test Request with name "+testRequestName+" has been finished.",requestingUser);
            notificationService.createNotification(notificationEntity, contextInfo);
        }
    }
    private static String getMessage(String x, TestcaseResultEntity testcaseResultEntity, String x1, List<String> failedSpecificationTestcaseResultName) {
        StringBuilder message = new StringBuilder(x + testcaseResultEntity.getName() + x1);
        for (int i = 0; i < (failedSpecificationTestcaseResultName.size() - 1); i++) {
            message.append(" <b>").append(failedSpecificationTestcaseResultName.get(i)).append("<b>,");
        }
        if (failedSpecificationTestcaseResultName.size() > 1) {
            message.append(" and ");
        }

        message.append("<b>").append(failedSpecificationTestcaseResultName.get(failedSpecificationTestcaseResultName.size() - 1)).append("<b>");

        return message.toString();
    }

    @Override
    public GraphInfo getDashboard(ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        GraphInfo graphInfo = new GraphInfo();


        // Search for all test Request Results
        TestcaseResultCriteriaSearchFilter searchFilterForAllTestRequests = new TestcaseResultCriteriaSearchFilter();
        searchFilterForAllTestRequests.setRefObjUri(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI);
        List<TestcaseResultEntity> testRequestResults = testcaseResultService.searchTestcaseResults(searchFilterForAllTestRequests, contextInfo);


        // Total Number of Applications


        graphInfo.setTotalApplications(testRequestResults.size());


        // Total number of assessees registered

        UserSearchCriteriaFilter searchCriteriaFilter = new UserSearchCriteriaFilter();
        graphInfo.setAssesseeRegistered(userService.searchUsers(searchCriteriaFilter, contextInfo).stream().filter(userEntity -> userEntity.getRoles().stream().anyMatch(roleEntity -> roleEntity.getId().equals(UserServiceConstants.ROLE_ID_ASSESSEE))).filter(userEntity -> userEntity.getState().equals(UserServiceConstants.USER_STATUS_ACTIVE)).toList().size());

        // Compliance Rate


        int complianceForComplianceRate = testRequestResults.stream().mapToInt(TestcaseResultEntity::getCompliant).sum();
        int nonComplianceForComplianceRate = testRequestResults.stream().mapToInt(TestcaseResultEntity::getNonCompliant).sum();

        if((complianceForComplianceRate+nonComplianceForComplianceRate)!=0){
            graphInfo.setComplianceRate(((float) complianceForComplianceRate /(complianceForComplianceRate + nonComplianceForComplianceRate))*100.0F);
        } else {
            graphInfo.setComplianceRate(0);
        }




        // Testing Rate



        if(!testRequestResults.isEmpty()){
            graphInfo.setTestingRate((((float)testRequestResults
                    .stream()
                    .filter(testcaseResultEntity -> testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED) || testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_SKIP) ).toList().size())
                    /
                    (testRequestResults.size())*100.0F));
        } else {
            graphInfo.setTestingRate(0);
        }




        // Set ApplicationRequestsByMonth



        Optional<Date> maxDate = testRequestResults.stream().max(Comparator.comparing(TestcaseResultEntity::getUpdatedAt)).map(TestcaseResultEntity::getUpdatedAt);

        Optional<Date> minDate = testRequestResults.stream().min(Comparator.comparing(TestcaseResultEntity::getUpdatedAt)).map(TestcaseResultEntity::getUpdatedAt);

        int max;
        int min;

        if(minDate.isPresent() && maxDate.isPresent()){
            min = minDate.get().getYear();
            max = maxDate.get().getYear();
        } else {
            throw new OperationFailedException("Date not found");
        }

        List<ApplicationRequests> applicationRequests = new ArrayList<>();
        for(int year = min; year <= max; year++){
            int finalYear = year;
            List<TestcaseResultEntity> yearlyTestRequestResults = testRequestResults.stream().filter(testcaseResultEntity -> testcaseResultEntity.getUpdatedAt().getYear() == finalYear).toList();

            List<ApplicationRequestDataByMonth> applicationRequestDataByMonthList = new ArrayList<>();

            for(int month = 1 ; month <= 12 ; month++){
                int finalMonth = month;
                List<TestcaseResultEntity> monthlyTestRequestResults = yearlyTestRequestResults.stream().filter(testcaseResultEntity -> testcaseResultEntity.getUpdatedAt().getMonth() == finalMonth).toList();

                ApplicationRequestDataByMonth applicationRequestDataByMonth = new ApplicationRequestDataByMonth();

                applicationRequestDataByMonth.setMonth(month);
                int compliant = monthlyTestRequestResults.stream().filter(testcaseResultEntity -> Boolean.TRUE.equals(testcaseResultEntity.getSuccess())).toList().size();
                applicationRequestDataByMonth.setCompliant(compliant);
                applicationRequestDataByMonth.setNonCompliant(monthlyTestRequestResults.size() - compliant);

                applicationRequestDataByMonthList.add(applicationRequestDataByMonth);
            }
            ApplicationRequests applicationRequest = new ApplicationRequests();
            applicationRequest.setYear(year+1900);

            applicationRequest.setApplicationRequestDataByMonthList(applicationRequestDataByMonthList);

            applicationRequests.add(applicationRequest);

        }

        graphInfo.setApplicationRequestsByMonth(applicationRequests);


//        // Get all Testcase Results completed in the last seven months
//        List<TestcaseResultEntity> testcaseResultEntitiesByMonth = this.findTestcaseResultsUpdatedLastSevenMonths();
//
//        // Get all Test Request Results generated in the last seven months
//        List<TestcaseResultEntity> testRequestResultEntitiesByMonth = testcaseResultEntitiesByMonth.stream().filter(testcaseResultEntity -> testcaseResultEntity.getRefObjUri().equals(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI) && testcaseResultEntity.getState().equals(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED)).toList();
//
//        // Initialize list to store application requests per month map data
//        List<ApplicationRequests> applicationRequestsByMonth = new ArrayList<>();
//
//        Calendar calendar = Calendar.getInstance();
//
//        int lastMonth= -1, compliantByMonth = 0, nonCompliantByMonth = 0;
//
//        ApplicationRequests applicationRequestByMonth = new ApplicationRequests();
//
//        for(TestcaseResultEntity testcaseResultEntity : testRequestResultEntitiesByMonth){
//
//            // Get the month of the testcaseResultEntity's updatedAt field
//            calendar.setTime(testcaseResultEntity.getUpdatedAt());
//            int month = calendar.get(Calendar.MONTH);
//
//            if(month!=lastMonth){
//
//                if(lastMonth!=-1){
//                    applicationRequestByMonth.setCompliant(compliantByMonth);
//                    applicationRequestByMonth.setNonCompliant(nonCompliantByMonth);
//
//                    applicationRequestsByMonth.add(applicationRequestByMonth);
//                }
//                applicationRequestByMonth = new ApplicationRequests();
//
//                applicationRequestByMonth.setYear(calendar.get(Calendar.YEAR));
//                applicationRequestByMonth.setMonth(calendar.get(Calendar.MONTH)+1);
//
//                compliantByMonth = 0;
//                nonCompliantByMonth = 0;
//
//                lastMonth = month;
//
//            }
//
//            compliantByMonth = compliantByMonth + (Boolean.TRUE.equals(testcaseResultEntity.getSuccess())?1:0);
//            nonCompliantByMonth = nonCompliantByMonth + (Boolean.FALSE.equals(testcaseResultEntity.getSuccess())?1:0);
//
//
//        }
//        applicationRequestByMonth.setCompliant(compliantByMonth);
//        applicationRequestByMonth.setNonCompliant(nonCompliantByMonth);
//
//        applicationRequestsByMonth.add(applicationRequestByMonth);
//
//        // Add Application Requests By Month map data to return Graph Info variable
//        graphInfo.setApplicationRequestsByMonth(applicationRequestsByMonth);




        // Compliant Application



        // Store returnable data of compliant application
        List<CompliantApplication> compliantApplications = new ArrayList<>();

        // Get Top five test Request Results according to compliant/non-compliant ratio
        List<TestcaseResultEntity> topFiveTestRequestsResult = testcaseResultService.findTopFiveTestRequestsResult();


        // Get Components using ids using ref ids from the component testcase result list
        List<ComponentEntity> allComponentEntities = componentService.findAll();

        // Calculate and set compliant applications in compliant applications list
        for(int i = 0 ; i < 5 ; i++){

            CompliantApplication compliantApplication = new CompliantApplication();

            compliantApplication.setApplicationName(topFiveTestRequestsResult.get(i).getName());
            compliantApplication.setTestcasesPassed(topFiveTestRequestsResult.get(i).getCompliant());
            compliantApplication.setRank(i+1);
            compliantApplication.setTotalTestcases(topFiveTestRequestsResult.get(i).getCompliant() + topFiveTestRequestsResult.get(i).getNonCompliant());

            // Search for Component Testcase Results of the current application of the loop
            TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
            searchFilter.setParentTestcaseResultId(topFiveTestRequestsResult.get(i).getId());

            List<TestcaseResultEntity> componentResults = testcaseResultService.searchTestcaseResults(searchFilter, contextInfo);

            /* QUESTION : Database call to get all components and then use java logic to match component OR Call database each time to get specific component */

            List<ComponentEntity> componentEntities = allComponentEntities.stream().filter(componentEntity -> componentResults.stream().anyMatch(testcaseResultEntity -> testcaseResultEntity.getRefId().equals(componentEntity.getId()))).toList();

            /* QUESTION : Which Rank to be used?? */
            List<Component> components = getComponents(componentEntities, componentResults);

            compliantApplication.setComponents(components);

            compliantApplications.add(compliantApplication);

        }

        // Set Compliant Application List to returnable Graph Info variable
        graphInfo.setCompliantApplications(compliantApplications);



        //PIE CHART



        // Find All test requests
        List<TestRequestEntity> allTestRequests = testRequestRepository.findAll();

        Map<String,Integer> pieChart = new HashMap<>();

        // Put counts of each status of test requests in the pieChart variable
        pieChart.put(TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS, allTestRequests.stream().filter(testRequestEntity -> testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS)).toList().size());
        pieChart.put(TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING, allTestRequests.stream().filter(testRequestEntity -> testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING)).toList().size());
        pieChart.put(TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED, allTestRequests.stream().filter(testRequestEntity -> testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED)).toList().size());
        pieChart.put(TestRequestServiceConstants.TEST_REQUEST_STATUS_SKIPPED, allTestRequests.stream().filter(testRequestEntity -> testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_SKIPPED)).toList().size());
        pieChart.put(TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED, allTestRequests.stream().filter(testRequestEntity -> testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED)).toList().size());
        pieChart.put(TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED, allTestRequests.stream().filter(testRequestEntity -> testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED)).toList().size());

        // Set pieChart values to returnable Graph Info variable
        graphInfo.setPieChart(pieChart);




        //AWARD GRAPH




        List<AwardGraph> awardGraphs = new ArrayList<>();


        List<ComponentEntity> allComponents = componentService.findAll();

        for(ComponentEntity componentEntity : allComponents){

            List<Object[]> bestFiveTestcaseResultPerComponent = this.findBestFiveTestcaseResultPerComponent(componentEntity.getId());

            List<AwardApplication> awardApplicationList = new ArrayList<>();

            AwardGraph awardGraph = new AwardGraph();

            awardGraph.setComponentName(componentEntity.getName());

            if(bestFiveTestcaseResultPerComponent.isEmpty()){


                awardGraph.setAwardApplicationList(new ArrayList<AwardApplication>());


            } else {


                for(Object[] testcaseResultPerComponent : bestFiveTestcaseResultPerComponent){
                    AwardApplication awardApplication = new AwardApplication();

                    awardApplication.setPassedTestcases((int)testcaseResultPerComponent[0]);
                    awardApplication.setTotalTestcases((int) testcaseResultPerComponent[0] + (int) testcaseResultPerComponent[1]);
                    awardApplication.setAppName(((TestRequestEntity)testcaseResultPerComponent[2]).getName());

                    awardApplicationList.add(awardApplication);
                }
                Comparator<AwardApplication> comparator = Comparator.comparing(
                        awardApplication -> {
                            if(awardApplication.getTotalTestcases()!=0){
                                return (awardApplication.getPassedTestcases() / awardApplication.getTotalTestcases());
                            } else {
                                return awardApplication.getPassedTestcases();
                            }
                        });

                awardApplicationList.sort(comparator.reversed());

                awardGraph.setAwardApplicationList(awardApplicationList);


            }


            awardGraphs.add(awardGraph);

        }

        Comparator<AwardGraph> comparator = Comparator.comparing(
                awardGraph -> {
                    if(Boolean.FALSE.equals(awardGraph.getAwardApplicationList().isEmpty())){
                        if(awardGraph.getAwardApplicationList().get(0).getTotalTestcases()!=0){
                            return (awardGraph.getAwardApplicationList().get(0).getPassedTestcases()/awardGraph.getAwardApplicationList().get(0).getTotalTestcases());
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }

                });

        awardGraphs.sort(comparator.reversed());

        for(AwardGraph awardGraph : awardGraphs){

            awardGraph.setComponentRank(awardGraphs.indexOf(awardGraph)+1);
        }

        graphInfo.setAwardGraph(awardGraphs);







//        // Set max size of the repository call request
//        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "compliant"));
//
//        List<ComponentEntity> allComponents = componentService.findAll();
//
//        // Call the repository method
//        List<TestcaseResultEntity> bestOfEachComponent = testcaseResultService.findBestOfEachComponent(allComponents);
//
//        // Set data in awardGraph variable
//        for( TestcaseResultEntity  testcaseResultEntity : bestOfEachComponent){
//
//            AwardGraph awardGraph = new AwardGraph();
//
//            awardGraph.setComponentName(testcaseResultEntity.getName());
//            awardGraph.setPassedTestcases(testcaseResultEntity.getCompliant());
//            awardGraph.setTotalTestcases(testcaseResultEntity.getCompliant() + testcaseResultEntity.getNonCompliant());
//
//            awardGraph.setComponentRank(bestOfEachComponent.indexOf(testcaseResultEntity)+1);
//            if(testcaseResultEntity.getTestRequest()!=null){
//                awardGraph.setAppName(testcaseResultEntity.getTestRequest().getName());
//            }
//
//
//
//            awardGraphs.add(awardGraph);
//
//        }
//
//        // Set awardGraph values in the returnable Graph Info variable
//        graphInfo.setAwardGraph(awardGraphs);
//



        //Percentage Cumulative Graph



        List<PercentageCumulativeGraph> percentageCumulativeGraphs = new ArrayList<>();

        // Find all components
        List<ComponentEntity> componentEntities = componentService.findAll();

        List<Object[]> filteredComponentsResults = testcaseResultRepository.nameComplianceAndNonCompliance(ComponentServiceConstants.COMPONENT_REF_OBJ_URI, TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED);

        for(Object[] componentResult: filteredComponentsResults){
            PercentageCumulativeGraph percentageCumulativeGraph = new PercentageCumulativeGraph();

            percentageCumulativeGraph.setCompliantTestRequests((Long)componentResult[0]);
            percentageCumulativeGraph.setTotalTestRequests((Long)componentResult[1] + (Long)componentResult[0]);
            percentageCumulativeGraph.setComponentName(String.valueOf(componentResult[2]));
            componentEntities = componentEntities.stream().filter(componentEntity -> !(componentEntity.getName().equals(String.valueOf(componentResult[2])))).collect(Collectors.toList());

            percentageCumulativeGraphs.add(percentageCumulativeGraph);
        }
        for(ComponentEntity component : componentEntities){
            PercentageCumulativeGraph percentageCumulativeGraph = new PercentageCumulativeGraph();

            percentageCumulativeGraph.setComponentName(component.getName());
            percentageCumulativeGraph.setTotalTestRequests(0L);
            percentageCumulativeGraph.setCompliantTestRequests(0L);

            percentageCumulativeGraphs.add(percentageCumulativeGraph);
        }

        // Sort percentageCumulativeGraph using percentage of compliant test Requests
        percentageCumulativeGraphs.sort(Comparator.comparing(percentageCumulativeGraph -> {
            if(percentageCumulativeGraph.getTotalTestRequests()!=0){
                return percentageCumulativeGraph.getCompliantTestRequests() / percentageCumulativeGraph.getTotalTestRequests();
            } else {
                return percentageCumulativeGraph.getCompliantTestRequests();
            }
        }));

            // Set component rank based in List index as list is sorted
        for( int i = 0 ; i < percentageCumulativeGraphs.size() ; i++ ){
            percentageCumulativeGraphs.get(i).setComponentRank( i + 1 );
        }

        // Set percentageCumulativeGraph in returnable Graph Info variable
        graphInfo.setPercentageCumulativeGraph(percentageCumulativeGraphs);

        // Return Graph Info
        return graphInfo;
    }

    private static List<Component> getComponents(List<ComponentEntity> componentEntities, List<TestcaseResultEntity> componentResults) {
        List<Component> components = new ArrayList<>();
        for(int j = 0; j < componentEntities.size() ; j++){

            Component component = new Component();
            component.setComponentRank(componentEntities.get(j).getRank());
            component.setComponentName(componentEntities.get(j).getName());
            component.setTestcasesPassed(componentResults.get(j).getCompliant());
            component.setTotalTestcases(componentResults.get(j).getCompliant() + componentResults.get(j).getNonCompliant());

            components.add(component);
        }
        return components;
    }

    private List<TestcaseResultEntity> findTestcaseResultsUpdatedLastSevenMonths(){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -6); // Subtract 7 months from the current date
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set date of start as 1

        Date sevenMonthsAgo = calendar.getTime();
        return testcaseResultRepository.findRecordsUpdatedLastSevenMonths(sevenMonthsAgo);

    }
    private List<TestcaseResultEntity> findTestcaseResultsUpdatedLastSevenYears(){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -6); // Subtract 7 years from the current date
        calendar.set(Calendar.DAY_OF_YEAR, 1);

        Date sevenYearsAgo = calendar.getTime();
        return testcaseResultRepository.findRecordsUpdatedLastSevenMonths(sevenYearsAgo);

    }
    private List<Object[]> findBestFiveTestcaseResultPerComponent(String componentId){
        return testcaseResultService.findBestFiveTestcaseResultPerComponent(componentId);
    }



}
