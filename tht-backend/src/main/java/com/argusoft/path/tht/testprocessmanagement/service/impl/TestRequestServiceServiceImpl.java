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
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestcaseExecutioner;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestCriteriaSearchFilter;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    private SpecificationService specificationService;

    @Autowired
    private TestcaseService testcaseService;

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
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {
        TestRequestValidator.validateTestRequestStartReinitializeProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                Constant.REINITIALIZE_PROCESS_VALIDATION,
                testcaseResultService,
                contextInfo);

        testcaseExecutioner.reinitializeTestingProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                contextInfo);
    }

    @Override
    @Timed(name = "startTestingProcess")
    public void startTestingProcess(
            String testRequestId,
            String refObjUri,
            String refId,
            Boolean isManual,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException,
            DataValidationErrorException, DoesNotExistException {
        TestRequestValidator.validateTestRequestStartReinitializeProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                Constant.START_PROCESS_VALIDATION,
                testcaseResultService,
                contextInfo);

        testcaseExecutioner.executeTestingProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                contextInfo);
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

        testRequestEntity.setState(TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING);
        TestRequestValidator.validateCreateUpdateTestRequest(Constant.CREATE_VALIDATION,
                testRequestEntity,
                this,
                userService,
                componentService,
                contextInfo);
        UserEntity principalUser = userService.getPrincipalUser(contextInfo);
        if (principalUser.getRoles().stream().anyMatch(roleEntity -> UserServiceConstants.ROLE_ID_ASSESSEE.equals(roleEntity.getId()))) {
            testRequestEntity.setAssessee(principalUser);
        }

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
            TestRequestCriteriaSearchFilter testRequestSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<TestRequestEntity> testRequestEntitySpecification = testRequestSearchFilter.buildSpecification();
        return testRequestRepository.findAll(testRequestEntitySpecification, pageable);
    }


    @Override
    @Timed(name = "searchTestRequests")
    public List<TestRequestEntity> searchTestRequests(
            TestRequestCriteriaSearchFilter testRequestSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        Specification<TestRequestEntity> testRequestEntitySpecification = testRequestSearchFilter.buildSpecification();
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
            throw new InvalidParameterException("pageable is missing");
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
    public TestRequestEntity changeState(String testRequestId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {

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
        testRequestEntity = testRequestRepository.save(testRequestEntity);

        changeStateCallback(testRequestEntity, contextInfo);
        return testRequestEntity;
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
                ).collect(Collectors.toList());

        Integer counter = 1;
        List<ComponentEntity> filteredComponents = activeComponents.stream().filter(componentEntity -> {
            return componentEntity.getSpecifications().stream().anyMatch(
                    specificationEntity -> {
                        return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                                && specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                            return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE)
                                    && !Objects.equals(Boolean.TRUE, testcaseEntity.getManual());
                        });
                    });
        }).collect(Collectors.toList());
        if (!filteredComponents.isEmpty()) {
            TestcaseResultEntity testRequestTestcaseResult = createDraftTestCaseResultIfNotExists(
                    TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                    testRequestEntity.getId(),
                    testRequestEntity.getId(),
                    testRequestEntity.getName(),
                    counter,
                    Boolean.FALSE,
                    Boolean.TRUE,
                    null,
                    contextInfo);
            counter++;
            for (ComponentEntity componentEntity : filteredComponents) {
                List<SpecificationEntity> filteredSpecifications = componentEntity.getSpecifications().stream().filter(specificationEntity -> {
                            return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                                    && specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                                return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE)
                                        && !Objects.equals(Boolean.TRUE, testcaseEntity.getManual());
                            });
                        })
                        .sorted((a, b) -> b.getRank().compareTo(a.getRank()))
                        .collect(Collectors.toList());

                if (!filteredSpecifications.isEmpty()) {
                    TestcaseResultEntity componentTestcaseResult = createDraftTestCaseResultIfNotExists(
                            ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                            componentEntity.getId(),
                            testRequestEntity.getId(),
                            componentEntity.getName(),
                            counter,
                            Boolean.FALSE,
                            Boolean.TRUE,
                            testRequestTestcaseResult.getId(),
                            contextInfo);
                    counter++;
                    for (SpecificationEntity specificationEntity : filteredSpecifications) {
                        List<TestcaseEntity> filteredTestcases = specificationEntity.getTestcases().stream().filter(testcaseEntity -> {
                                    return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE)
                                            && !Objects.equals(Boolean.TRUE, testcaseEntity.getManual());
                                })
                                .sorted((a, b) -> b.getRank().compareTo(a.getRank()))
                                .collect(Collectors.toList());

                        if (!filteredTestcases.isEmpty()) {
                            TestcaseResultEntity specificationTestcaseResult = createDraftTestCaseResultIfNotExists(
                                    SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                                    specificationEntity.getId(),
                                    testRequestEntity.getId(),
                                    specificationEntity.getName(),
                                    counter,
                                    Boolean.FALSE,
                                    specificationEntity.getRequired(),
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
                                        Boolean.FALSE,
                                        specificationEntity.getRequired(),
                                        specificationTestcaseResult.getId(),
                                        contextInfo);
                                counter++;
                            }
                        }
                    }
                }
            }
        }


        counter = 1;
        filteredComponents = activeComponents.stream().filter(componentEntity -> {
            return componentEntity.getSpecifications().stream().anyMatch(
                    specificationEntity -> {
                        return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                                && specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                            return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE)
                                    && Objects.equals(Boolean.TRUE, testcaseEntity.getManual());
                        });
                    });
        }).collect(Collectors.toList());
        if (!filteredComponents.isEmpty()) {
            TestcaseResultEntity testRequestTestcaseResult = createDraftTestCaseResultIfNotExists(
                    TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                    testRequestEntity.getId(),
                    testRequestEntity.getId(),
                    testRequestEntity.getName(),
                    counter,
                    Boolean.TRUE,
                    Boolean.TRUE,
                    null,
                    contextInfo);
            counter++;
            for (ComponentEntity componentEntity : filteredComponents) {
                List<SpecificationEntity> filteredSpecifications = componentEntity.getSpecifications().stream().filter(specificationEntity -> {
                            return specificationEntity.getState().equals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE)
                                    && specificationEntity.getTestcases().stream().anyMatch(testcaseEntity -> {
                                return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE)
                                        && Objects.equals(Boolean.TRUE, testcaseEntity.getManual());
                            });
                        })
                        .sorted((a, b) -> b.getRank().compareTo(a.getRank()))
                        .collect(Collectors.toList());

                if (!filteredSpecifications.isEmpty()) {
                    TestcaseResultEntity componentTestcaseResult = createDraftTestCaseResultIfNotExists(
                            ComponentServiceConstants.COMPONENT_REF_OBJ_URI,
                            componentEntity.getId(),
                            testRequestEntity.getId(),
                            componentEntity.getName(),
                            counter,
                            Boolean.TRUE,
                            Boolean.TRUE,
                            testRequestTestcaseResult.getId(),
                            contextInfo);
                    counter++;
                    for (SpecificationEntity specificationEntity : filteredSpecifications) {
                        List<TestcaseEntity> filteredTestcases = specificationEntity.getTestcases().stream().filter(testcaseEntity -> {
                                    return testcaseEntity.getState().equals(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE)
                                            && Objects.equals(Boolean.TRUE, testcaseEntity.getManual());
                                })
                                .sorted((a, b) -> b.getRank().compareTo(a.getRank()))
                                .collect(Collectors.toList());

                        if (!filteredTestcases.isEmpty()) {
                            TestcaseResultEntity specificationTestcaseResult = createDraftTestCaseResultIfNotExists(
                                    SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI,
                                    specificationEntity.getId(),
                                    testRequestEntity.getId(),
                                    specificationEntity.getName(),
                                    counter,
                                    Boolean.TRUE,
                                    specificationEntity.getRequired(),
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
                                        Boolean.TRUE,
                                        specificationEntity.getRequired(),
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
                                                                      Boolean isRequired,
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
        testcaseResultEntity.setTestRequestId(testRequestId);
        testcaseResultEntity.setRank(counter);
        testcaseResultEntity.setName(name);
        testcaseResultEntity.setManual(isManual);
        testcaseResultEntity.setRequired(isRequired);
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
}
