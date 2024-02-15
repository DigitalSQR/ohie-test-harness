/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestcaseExecutioner;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.argusoft.path.tht.testprocessmanagement.validator.TestRequestValidator;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants.TEST_REQUEST_STATUS;

/**
 * This TestRequestServiceServiceImpl contains implementation for TestRequest service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "TestRequestServiceServiceImpl")
public class TestRequestServiceServiceImpl implements TestRequestService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestRequestServiceServiceImpl.class);

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
    @Timed(name = "reinitializeTestingProcess")
    public void reinitializeTestingProcess(
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
                Constant.REINITIALIZE_PROCESS_VALIDATION,
                testcaseResultService,
                contextInfo);

        testcaseExecutioner.reinitializeTestingProcess(
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

    @Override
    @Timed(name = "startTestingProcess")
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
        System.out.println("Inside start Testing Process");
        try {
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
        } catch (DoesNotExistException | VersionMismatchException e) {
            throw new RuntimeException(e);
        }
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
            DataValidationErrorException, DoesNotExistException {

        if (testRequestEntity == null) {
            LOGGER.error("caught InvalidParameterException in TestRequestServiceServiceImpl ");
            throw new InvalidParameterException("TestRequestEntity is missing");
        }

        defaultValueCreateTestRequest(testRequestEntity, contextInfo);

        TestRequestValidator.validateCreateUpdateTestRequest(Constant.CREATE_VALIDATION,
                testRequestEntity,
                this,
                userService,
                componentService,
                contextInfo);

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
    @Timed(name = "updateTestRequest")
    public TestRequestEntity updateTestRequest(TestRequestEntity testRequestEntity,
                                               ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        if (testRequestEntity == null) {
            LOGGER.error("caught InvalidParameterException in TestRequestServiceServiceImpl ");
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
    @Timed(name = "searchTestRequests")
    public Page<TestRequestEntity> searchTestRequests(
            TestRequestCriteriaSearchFilter testRequestSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<TestRequestEntity> testRequestEntitySpecification = testRequestSearchFilter.buildSpecification(contextInfo);
        return testRequestRepository.findAll(testRequestEntitySpecification, pageable);
    }


    @Override
    @Timed(name = "searchTestRequests")
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
    @Timed(name = "getTestRequestById")
    public TestRequestEntity getTestRequestById(String testRequestId,
                                                ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(testRequestId)) {
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
    @Timed(name = "validateTestRequest")
    public List<ValidationResultInfo> validateTestRequest(
            String validationTypeKey,
            TestRequestEntity testRequestEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        if (testRequestEntity == null) {
            LOGGER.error("caught InvalidParameterException in TestRequestServiceServiceImpl ");
            throw new InvalidParameterException("TestRequestEntity is missing");
        }
        List<ValidationResultInfo> errors = TestRequestValidator.validateTestRequest(validationTypeKey, testRequestEntity, this, userService, componentService, contextInfo);
        return errors;
    }

    @Override
    public TestRequestEntity changeState(String testRequestId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {

        TestRequestEntity testRequestEntity = this.getTestRequestById(testRequestId, contextInfo);

        defaultValueChangeState(testRequestEntity, stateKey, contextInfo);

        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        ValidationUtils.statusPresent(TEST_REQUEST_STATUS, stateKey, errors);

        validateChangeStateForAccepted(testRequestEntity,stateKey);

        //validate transition
        ValidationUtils.transitionValid(TestRequestServiceConstants.TEST_REQUEST_STATUS_MAP, testRequestEntity.getState(), stateKey, errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    errors);
        }

        testRequestEntity.setState(stateKey);
        testRequestEntity = testRequestRepository.saveAndFlush(testRequestEntity);

        changeStateCallback(testRequestEntity, contextInfo);
        return testRequestEntity;
    }

    private void validateChangeStateForAccepted(TestRequestEntity testRequestEntity, String nextState) throws DataValidationErrorException {
        if(TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED.equals(nextState)) {

            Set<TestRequestUrlEntity> testRequestUrls = testRequestEntity.getTestRequestUrls();

            if (testRequestUrls.isEmpty()) {
                throwValidationException("Components not found to test", "component");
            }

            List<ComponentEntity> componentListToTest = testRequestUrls.stream()
                    .map(TestRequestUrlEntity::getComponent)
                    .toList();

            if (componentListToTest.stream().noneMatch(componentEntity -> ComponentServiceConstants.COMPONENT_STATUS_ACTIVE.equals(componentEntity.getState()))) {
                throwValidationException("No active component(s) found to start testing", "component");
            }

            for (ComponentEntity componentEntity : componentListToTest) {
                if (componentEntity.getSpecifications().stream().noneMatch(specificationEntity -> SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE.equals(specificationEntity.getState()))) {
                    throwValidationException("No active specifications found for component named " + componentEntity.getName() + ", can't start testing for this request.", "specification");
                }

                for (SpecificationEntity specification : componentEntity.getSpecifications()) {
                    if (specification.getTestcases().stream().noneMatch(testcaseEntity -> TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE.equals(testcaseEntity.getState()))) {
                        throwValidationException("No active testcases found for specification named " + specification.getName() + ", can't start testing for this request.", "specification");
                    }
                }
            }
        }
    }

    private void throwValidationException(String message, String element) throws DataValidationErrorException {
        ValidationResultInfo validationResultInfo = new ValidationResultInfo();
        validationResultInfo.setMessage(message);
        validationResultInfo.setLevel(ErrorLevel.ERROR);
        validationResultInfo.setElement(element);
        throw new DataValidationErrorException(message, Collections.singletonList(validationResultInfo));
    }

    private void changeStateCallback(TestRequestEntity testRequestEntity, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        if (testRequestEntity.getState().equals(TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED)) {
            createDraftTestcaseResultsByTestRequestAndProcessKey(testRequestEntity, contextInfo);
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
                                createDraftTestCaseResultIfNotExists(
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
                                counter++;
                            }
                        }
                    }
                }
            }
        }
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
        if (!StringUtils.isEmpty(parentTestcaseResultId)) {
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
}
