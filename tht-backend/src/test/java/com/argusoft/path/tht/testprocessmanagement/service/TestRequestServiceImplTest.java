package com.argusoft.path.tht.testprocessmanagement.service;

import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.reportmanagement.mock.TestcaseResultServiceMockImpl;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.mock.*;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.mock.TestRequestServiceMockImpl;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntityId;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestValueEntity;
import com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestMapper;
import com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestValueMapper;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class TestRequestServiceImplTest extends TestingHarnessToolRestTestConfiguration{

    @Autowired
    TestcaseRepository testcaseRepository;
    @Autowired
    private TestcaseResultServiceMockImpl testcaseResultServiceMockImpl;

    @Autowired
    private TestcaseOptionServiceMockImpl testcaseOptionServiceMockImpl;

    @Autowired
    private TestRequestService testRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private TestRequestRepository testRequestRepository;

    @Autowired
    private TestRequestValueMapper testRequestValueMapper;

    @Autowired
    private TestRequestMapper testRequestMapper;


    private ContextInfo contextInfo;

    @BeforeEach
    @Override
    public void init(){
        super.init();
        testcaseOptionServiceMockImpl.init();
        testcaseResultServiceMockImpl.init();
        contextInfo = Constant.ASSESSE_USER_CONTEXT;
    }

    @AfterEach
    void after() {
        testcaseResultServiceMockImpl.clear();
        testcaseOptionServiceMockImpl.clear();

    }

    @Test
    @Transactional
    void testStartAndStopTestingProcess() throws InvalidParameterException, OperationFailedException, DataValidationErrorException {

        ContextInfo contextInfo = Constant.SUPER_USER_CONTEXT;

        // Test : Valid input for automated testing process of CRF3Testcase1
        String testRequestId = "TestRequest.15";
        String refObjUri = "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo";
        String refId = "component.09";
        Boolean isManual = false;
        Boolean isAutomated = true;
        Boolean isRequired = true;
        Boolean isRecommended = false;
        Boolean isWorkflow = false;
        Boolean isFunctional = true;

        assertDoesNotThrow(() -> {
            testRequestService.startTestingProcess(
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
        });

        // Test 1: Valid input for manual testing process
        String testRequestId1 = "TestRequest.13";
        String refObjUri1 = "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo";
        String refId1 = "component.02";
        Boolean isManual1 = true;
        Boolean isAutomated1 = false;
        Boolean isRequired1 = true;
        Boolean isRecommended1 = false;
        Boolean isWorkflow1 = false;
        Boolean isFunctional1 = true;

        assertDoesNotThrow(() -> {
            testRequestService.startTestingProcess(
                    testRequestId1,
                    refObjUri1,
                    refId1,
                    isManual1,
                    isAutomated1,
                    isRequired1,
                    isRecommended1,
                    isWorkflow1,
                    isFunctional1,
                    contextInfo);
        });

        // Test 1: Valid input for stopping manual testing process
        assertDoesNotThrow(() -> {
            testRequestService.stopTestingProcess(
                    testRequestId1,
                    refObjUri1,
                    refId1,
                    isManual1,
                    isAutomated1,
                    isRequired1,
                    isRecommended1,
                    isWorkflow1,
                    isFunctional1,
                    false,
                    contextInfo);
        });


        // Test 2: Valid input for automated testing process
        String testRequestId2 = "TestRequest.14";
        String refObjUri2 = "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo";
        String refId2 = "component.04";
        Boolean isManual2 = false;
        Boolean isAutomated2 = true;
        Boolean isRequired2 = true;
        Boolean isRecommended2 = false;
        Boolean isWorkflow2 = false;
        Boolean isFunctional2 = true;

        assertDoesNotThrow(() -> {
            testRequestService.startTestingProcess(
                    testRequestId2,
                    refObjUri2,
                    refId2,
                    isManual2,
                    isAutomated2,
                    isRequired2,
                    isRecommended2,
                    isWorkflow2,
                    isFunctional2,
                    contextInfo);
        });

        // Test 2: Valid input for stopping automated testing process
        assertDoesNotThrow(() -> {
            testRequestService.stopTestingProcess(
                    testRequestId2,
                    refObjUri2,
                    refId2,
                    isManual2,
                    isAutomated2,
                    isRequired2,
                    isRecommended2,
                    isWorkflow2,
                    isFunctional2,
                    true,
                    contextInfo);
        });

        // Test 3: Invalid input - Missing test request ID
        assertThrows(InvalidParameterException.class, () -> {
            testRequestService.startTestingProcess(
                    null,
                    refObjUri1,
                    refId1,
                    isManual1,
                    isAutomated1,
                    isRequired1,
                    isRecommended1,
                    isWorkflow1,
                    isFunctional1,
                    contextInfo);
        });

        // Test 3: Invalid input - Missing test request ID
        assertThrows(InvalidParameterException.class, () -> {
            testRequestService.stopTestingProcess(
                    null,
                    refObjUri1,
                    refId1,
                    isManual1,
                    isAutomated1,
                    isRequired1,
                    isRecommended1,
                    isWorkflow1,
                    isFunctional1,
                    true,
                    contextInfo);
        });

        // Test 4: Invalid input - Missing refId
        assertThrows(InvalidParameterException.class, () -> {
            testRequestService.startTestingProcess(
                    testRequestId1,
                    refObjUri1,
                    null,
                    isManual1,
                    isAutomated1,
                    isRequired1,
                    isRecommended1,
                    isWorkflow1,
                    isFunctional1,
                    contextInfo);
        });

        // Test 5: Invalid input - Incorrect Ref Obj Uri
        String refObjUri5 = "com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo";
        assertThrows(DataValidationErrorException.class, () -> {
            testRequestService.startTestingProcess(
                    testRequestId1,
                    refObjUri5,
                    refId1,
                    isManual1,
                    isAutomated1,
                    isRequired1,
                    isRecommended1,
                    isWorkflow1,
                    isFunctional1,
                    contextInfo);
        });

    }


    @Test
    @Transactional
    void createTestRequest()throws
            InvalidParameterException, DoesNotExistException {

        // Test for empty test request
        TestRequestEntity testRequestEntity = new TestRequestEntity();

        assertThrows(InvalidParameterException.class, ()->{
            testRequestService.createTestRequest(null, contextInfo);
        });

        // Test for test request without name
        testRequestEntity.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity.setDescription("Test");
        //testRequestEntity.setName("TEST");
        testRequestEntity.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity = new TestRequestUrlEntity();

        testRequestUrlEntity.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity.setPassword("password");
        testRequestUrlEntity.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities = new HashSet<>();
        testRequestUrlEntities.add(testRequestUrlEntity);

        testRequestEntity.setTestRequestUrls(testRequestUrlEntities);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity, contextInfo);
        });

        // Test for test request without version

//        TestRequestEntity testRequestEntity1 = new TestRequestEntity();
//
//        testRequestEntity1.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
//        testRequestEntity1.setAssessee(userService.getUserById("user.01", contextInfo));
//        testRequestEntity1.setDescription("Test");
//        testRequestEntity1.setName("TEST");
//        testRequestEntity1.setState("component.status.accepted");
//
//        TestRequestUrlEntity testRequestUrlEntity1 = new TestRequestUrlEntity();
//
//        testRequestUrlEntity1.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
//        testRequestUrlEntity1.setComponent(componentService.getComponentById("component.02", contextInfo));
//        testRequestUrlEntity1.setPassword("password");
//        testRequestUrlEntity1.setUsername("username");
//
//        Set<TestRequestUrlEntity> testRequestUrlEntities1 = new HashSet<>();
//        testRequestUrlEntities1.add(testRequestUrlEntity1);
//
//        testRequestEntity1.setTestRequestUrls(testRequestUrlEntities1);
//
//        testRequestEntity1.setVersion(null);
//
//        assertThrows(DataValidationErrorException.class, ()->{
//            testRequestService.createTestRequest(testRequestEntity1, contextInfo);
//        });

        // Test for test request without test request url

        TestRequestEntity testRequestEntity5 = new TestRequestEntity();

        testRequestEntity5.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity5.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity5.setDescription("Test");
        testRequestEntity5.setName("TEST");
        testRequestEntity5.setState("component.status.accepted");
        testRequestEntity5.setTestRequestUrls(null);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity5, contextInfo);
        });

        // Test for test request without test request url username

        TestRequestEntity testRequestEntity6 = new TestRequestEntity();

        testRequestEntity6.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity6.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity6.setDescription("Test");
        testRequestEntity6.setName("TEST");
        testRequestEntity6.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity6 = new TestRequestUrlEntity();

        testRequestUrlEntity6.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity6.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity6.setPassword("password");
//        testRequestUrlEntity6.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities6 = new HashSet<>();
        testRequestUrlEntities6.add(testRequestUrlEntity6);

        testRequestEntity6.setTestRequestUrls(testRequestUrlEntities6);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity6, contextInfo);
        });

        // Test for test request without test request url password

        TestRequestEntity testRequestEntity7 = new TestRequestEntity();

        testRequestEntity7.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity7.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity7.setDescription("Test");
        testRequestEntity7.setName("TEST");
        testRequestEntity7.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity7 = new TestRequestUrlEntity();

        testRequestUrlEntity7.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity7.setComponent(componentService.getComponentById("component.02", contextInfo));
//        testRequestUrlEntity7.setPassword("password");
        testRequestUrlEntity7.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities7 = new HashSet<>();
        testRequestUrlEntities7.add(testRequestUrlEntity7);

        testRequestEntity7.setTestRequestUrls(testRequestUrlEntities7);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity7, contextInfo);
        });

        // Test for test request without test request url firApiBaseUrl

        TestRequestEntity testRequestEntity8 = new TestRequestEntity();

        testRequestEntity8.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity8.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity8.setDescription("Test");
        testRequestEntity8.setName("TEST");
        testRequestEntity8.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity8 = new TestRequestUrlEntity();

//        testRequestUrlEntity8.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity8.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity8.setPassword("password");
        testRequestUrlEntity8.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities8 = new HashSet<>();
        testRequestUrlEntities8.add(testRequestUrlEntity8);

        testRequestEntity8.setTestRequestUrls(testRequestUrlEntities8);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity8, contextInfo);
        });

        // Test for test request with incorrect approver id

        TestRequestEntity testRequestEntity9 = new TestRequestEntity();

        UserEntity userEntity = new UserEntity();

        // user.09 does not exist
        userEntity.setId("user.09");

        testRequestEntity9.setApprover(userEntity);

        testRequestEntity9.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity9.setDescription("Test");
        testRequestEntity9.setName("TEST");
        testRequestEntity9.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity9 = new TestRequestUrlEntity();

        testRequestUrlEntity9.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity9.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity9.setPassword("password");
        testRequestUrlEntity9.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities9 = new HashSet<>();
        testRequestUrlEntities9.add(testRequestUrlEntity9);

        testRequestEntity9.setTestRequestUrls(testRequestUrlEntities9);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity9, contextInfo);
        });

        // Test for test request with assessee status inactive

        TestRequestEntity testRequestEntity10 = new TestRequestEntity();

        testRequestEntity10.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity10.setAssessee(userService.getUserById("user.03", contextInfo));
        testRequestEntity10.setDescription("Test");
        testRequestEntity10.setName("TEST");
        testRequestEntity10.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity10 = new TestRequestUrlEntity();

        testRequestUrlEntity10.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity10.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity10.setPassword("password");
        testRequestUrlEntity10.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities10 = new HashSet<>();
        testRequestUrlEntities10.add(testRequestUrlEntity10);

        testRequestEntity10.setTestRequestUrls(testRequestUrlEntities10);
        contextInfo = new ContextInfo(
                "inactiveAssessee@yopmail.com",
                "user.03",
                "password",
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(UserServiceConstants.ROLE_ID_ASSESSEE))
        );

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity10, contextInfo);
        });

        // Test for test request with supplied assessee does not have correct role

        TestRequestEntity testRequestEntity11 = new TestRequestEntity();

        testRequestEntity11.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity11.setAssessee(userService.getUserById("user.06", contextInfo));
        testRequestEntity11.setDescription("Test");
        testRequestEntity11.setName("TEST");
        testRequestEntity11.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity11 = new TestRequestUrlEntity();

        testRequestUrlEntity11.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity11.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity11.setPassword("password");
        testRequestUrlEntity11.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities11 = new HashSet<>();
        testRequestUrlEntities11.add(testRequestUrlEntity11);

        testRequestEntity11.setTestRequestUrls(testRequestUrlEntities11);

        contextInfo = new ContextInfo(
                "notAssessee@yopmail.com",
                "user.06",
                "password",
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(UserServiceConstants.ROLE_ID_ASSESSEE))
        );;

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity11, contextInfo);
        });

        // Test for test request with incorrect assessee Id

        TestRequestEntity testRequestEntity12 = new TestRequestEntity();

        UserEntity userEntity12 = new UserEntity();

        // user.09 does not exist
        userEntity12.setId("user.09");

        testRequestEntity12.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));

        testRequestEntity12.setAssessee(userEntity12);
        testRequestEntity12.setDescription("Test");
        testRequestEntity12.setName("TEST");
        testRequestEntity12.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity12 = new TestRequestUrlEntity();

        testRequestUrlEntity12.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity12.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity12.setPassword("password");
        testRequestUrlEntity12.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities12 = new HashSet<>();
        testRequestUrlEntities12.add(testRequestUrlEntity12);

        testRequestEntity12.setTestRequestUrls(testRequestUrlEntities12);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.createTestRequest(testRequestEntity12, contextInfo);
        });

        // Test for test request with proper data

        TestRequestEntity testRequestEntity13 = new TestRequestEntity();

        testRequestEntity13.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity13.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity13.setDescription("Test");
        testRequestEntity13.setName("TEST");
        testRequestEntity13.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity13 = new TestRequestUrlEntity();

        testRequestUrlEntity13.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity13.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity13.setPassword("password");
        testRequestUrlEntity13.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities13 = new HashSet<>();
        testRequestUrlEntities13.add(testRequestUrlEntity13);

        testRequestEntity13.setTestRequestUrls(testRequestUrlEntities13);

        assertDoesNotThrow(()->{
            testRequestService.createTestRequest(testRequestEntity13, contextInfo);
        });


        assertDoesNotThrow(() -> {
            TestRequestValueEntity testRequestValueEntity = new TestRequestValueEntity();
            testRequestValueEntity.setId("Id.001");
            testRequestValueEntity.setTestcaseVariableId("Id.01");
            testRequestValueEntity.setTestRequestValueInput("ValueInput");
            testRequestValueEntity.setTestRequest(new TestRequestEntity());

            TestRequestValueEntity copyTestRequestValueEntity = new TestRequestValueEntity(testRequestValueEntity);

            Set<TestRequestValueEntity> testRequestValueEntities = new HashSet<>();
            testRequestValueEntities.add(testRequestValueEntity);

            testRequestValueMapper.dtoToModel(testRequestValueMapper.modelToDto(testRequestValueEntities));
            testRequestValueMapper.dtoToModel(testRequestValueMapper.modelToDto(testRequestValueEntity));

            List<TestRequestValueEntity> testRequestValueEntities1 = new ArrayList<>();
            testRequestValueMapper.dtoToModel(testRequestValueMapper.modelToDto(testRequestValueEntities1));

            testRequestMapper.setToTestRequest("TestRequest.01");
            testRequestMapper.setToTestRequestId(new TestRequestEntity());

            TestRequestInfo testRequestInfo = new TestRequestInfo();
            testRequestInfo.setTestRequestValues(testRequestValueMapper.modelToDto(testRequestValueEntities));
            testRequestMapper.setToTestRequestValues(testRequestInfo);

            TestRequestUrlEntity testRequestUrlEntity1 = new TestRequestUrlEntity();
            testRequestUrlEntity1.equals(testRequestUrlEntity1);
            testRequestUrlEntity1.setTestRequestId("TestRequest.01");
            testRequestUrlEntity1.setComponent(new ComponentEntity());
            testRequestUrlEntity1.getTestRequestId();
            testRequestUrlEntity1.getComponent();

            TestRequestUrlEntityId testRequestUrlEntityId = new TestRequestUrlEntityId();

            testRequestUrlEntityId.setTestRequestId("TestRequest.01");
            testRequestUrlEntityId.setComponent(new ComponentEntity("component.01"));


            testRequestUrlEntityId.toString();


        });

    }


    @Test
    @Transactional
    void updateTestRequest() throws OperationFailedException, InvalidParameterException, DataValidationErrorException, DoesNotExistException {
         // Test for empty test request
        TestRequestEntity testRequestEntity = new TestRequestEntity();

        assertThrows(InvalidParameterException.class, ()->{
            testRequestService.updateTestRequest(null, contextInfo);
        });

        // Test for test request without name
        testRequestEntity.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity.setDescription("Test");
        //testRequestEntity.setName("TEST");
        testRequestEntity.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity = new TestRequestUrlEntity();

        testRequestUrlEntity.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity.setPassword("password");
        testRequestUrlEntity.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities = new HashSet<>();
        testRequestUrlEntities.add(testRequestUrlEntity);

        testRequestEntity.setTestRequestUrls(testRequestUrlEntities);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity, contextInfo);
        });
        // Test for test request without test request url

        TestRequestEntity testRequestEntity5 = new TestRequestEntity();

        testRequestEntity5.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity5.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity5.setDescription("Test");
        testRequestEntity5.setName("TEST");
        testRequestEntity5.setState("component.status.accepted");
        testRequestEntity5.setTestRequestUrls(null);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity5, contextInfo);
        });

        // Test for test request without test request url username

        TestRequestEntity testRequestEntity6 = new TestRequestEntity();

        testRequestEntity6.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity6.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity6.setDescription("Test");
        testRequestEntity6.setName("TEST");
        testRequestEntity6.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity6 = new TestRequestUrlEntity();

        testRequestUrlEntity6.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity6.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity6.setPassword("password");
//        testRequestUrlEntity6.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities6 = new HashSet<>();
        testRequestUrlEntities6.add(testRequestUrlEntity6);

        testRequestEntity6.setTestRequestUrls(testRequestUrlEntities6);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity6, contextInfo);
        });

        // Test for test request without test request url password

        TestRequestEntity testRequestEntity7 = new TestRequestEntity();

        testRequestEntity7.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity7.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity7.setDescription("Test");
        testRequestEntity7.setName("TEST");
        testRequestEntity7.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity7 = new TestRequestUrlEntity();

        testRequestUrlEntity7.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity7.setComponent(componentService.getComponentById("component.02", contextInfo));
//        testRequestUrlEntity7.setPassword("password");
        testRequestUrlEntity7.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities7 = new HashSet<>();
        testRequestUrlEntities7.add(testRequestUrlEntity7);

        testRequestEntity7.setTestRequestUrls(testRequestUrlEntities7);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity7, contextInfo);
        });

        // Test for test request without test request url firApiBaseUrl

        TestRequestEntity testRequestEntity8 = new TestRequestEntity();

        testRequestEntity8.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity8.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity8.setDescription("Test");
        testRequestEntity8.setName("TEST");
        testRequestEntity8.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity8 = new TestRequestUrlEntity();

//        testRequestUrlEntity8.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity8.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity8.setPassword("password");
        testRequestUrlEntity8.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities8 = new HashSet<>();
        testRequestUrlEntities8.add(testRequestUrlEntity8);

        testRequestEntity8.setTestRequestUrls(testRequestUrlEntities8);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity8, contextInfo);
        });

        // Test for test request with incorrect approver id

        TestRequestEntity testRequestEntity9 = new TestRequestEntity();

        UserEntity userEntity = new UserEntity();

        // user.09 does not exist
        userEntity.setId("user.09");

        testRequestEntity9.setApprover(userEntity);

        testRequestEntity9.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity9.setDescription("Test");
        testRequestEntity9.setName("TEST");
        testRequestEntity9.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity9 = new TestRequestUrlEntity();

        testRequestUrlEntity9.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity9.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity9.setPassword("password");
        testRequestUrlEntity9.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities9 = new HashSet<>();
        testRequestUrlEntities9.add(testRequestUrlEntity9);

        testRequestEntity9.setTestRequestUrls(testRequestUrlEntities9);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity9, contextInfo);
        });

        // Test for test request with assessee status inactive

        TestRequestEntity testRequestEntity10 = new TestRequestEntity();

        testRequestEntity10.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity10.setAssessee(userService.getUserById("user.03", contextInfo));
        testRequestEntity10.setDescription("Test");
        testRequestEntity10.setName("TEST");
        testRequestEntity10.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity10 = new TestRequestUrlEntity();

        testRequestUrlEntity10.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity10.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity10.setPassword("password");
        testRequestUrlEntity10.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities10 = new HashSet<>();
        testRequestUrlEntities10.add(testRequestUrlEntity10);

        testRequestEntity10.setTestRequestUrls(testRequestUrlEntities10);
        contextInfo = new ContextInfo(
                "inactiveAssessee@yopmail.com",
                "user.03",
                "password",
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(UserServiceConstants.ROLE_ID_ASSESSEE))
        );

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity10, contextInfo);
        });

        // Test for test request with supplied assessee does not have correct role

        TestRequestEntity testRequestEntity11 = new TestRequestEntity();

        testRequestEntity11.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity11.setAssessee(userService.getUserById("user.06", contextInfo));
        testRequestEntity11.setDescription("Test");
        testRequestEntity11.setName("TEST");
        testRequestEntity11.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity11 = new TestRequestUrlEntity();

        testRequestUrlEntity11.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity11.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity11.setPassword("password");
        testRequestUrlEntity11.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities11 = new HashSet<>();
        testRequestUrlEntities11.add(testRequestUrlEntity11);

        testRequestEntity11.setTestRequestUrls(testRequestUrlEntities11);

        contextInfo = new ContextInfo(
                "notAssessee@yopmail.com",
                "user.06",
                "password",
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(UserServiceConstants.ROLE_ID_ASSESSEE))
        );;

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity11, contextInfo);
        });

        // Test for test request with incorrect assessee Id

        TestRequestEntity testRequestEntity12 = new TestRequestEntity();

        UserEntity userEntity12 = new UserEntity();

        // user.09 does not exist
        userEntity12.setId("user.09");

        testRequestEntity12.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));

        testRequestEntity12.setAssessee(userEntity12);
        testRequestEntity12.setDescription("Test");
        testRequestEntity12.setName("TEST");
        testRequestEntity12.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity12 = new TestRequestUrlEntity();

        testRequestUrlEntity12.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity12.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity12.setPassword("password");
        testRequestUrlEntity12.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities12 = new HashSet<>();
        testRequestUrlEntities12.add(testRequestUrlEntity12);

        testRequestEntity12.setTestRequestUrls(testRequestUrlEntities12);

        assertThrows(DataValidationErrorException.class, ()->{
            testRequestService.updateTestRequest(testRequestEntity12, contextInfo);
        });

        // Test for test request with proper data

        TestRequestEntity testRequestEntity13 = new TestRequestEntity();

        testRequestEntity13.setApprover(userService.getUserById("SYSTEM_USER", contextInfo));
        testRequestEntity13.setAssessee(userService.getUserById("user.01", contextInfo));
        testRequestEntity13.setDescription("Test");
        testRequestEntity13.setName("TEST");
        testRequestEntity13.setState("component.status.accepted");

        TestRequestUrlEntity testRequestUrlEntity13 = new TestRequestUrlEntity();

        testRequestUrlEntity13.setFhirApiBaseUrl("https://hapi.fhir.org/baseR4");
        testRequestUrlEntity13.setComponent(componentService.getComponentById("component.02", contextInfo));
        testRequestUrlEntity13.setPassword("password");
        testRequestUrlEntity13.setUsername("username");

        Set<TestRequestUrlEntity> testRequestUrlEntities13 = new HashSet<>();
        testRequestUrlEntities13.add(testRequestUrlEntity13);

        testRequestEntity13.setTestRequestUrls(testRequestUrlEntities13);

        Set<TestRequestValueEntity> testRequestValueEntities = new HashSet<>();

        TestRequestValueEntity testRequestValueEntity = new TestRequestValueEntity();
        testRequestValueEntity.setTestRequestValueInput("Input");

        testRequestValueEntities.add(testRequestValueEntity);

        testRequestEntity13.setTestRequestValues(testRequestValueEntities);

        assertDoesNotThrow(()->{
            testRequestService.updateTestRequest(testRequestEntity13, contextInfo);
        });

    }

    @Test
    @Transactional
    void searchTestRequestsWithPageable(){

        TestRequestCriteriaSearchFilter testRequestCriteriaSearchFilter = new TestRequestCriteriaSearchFilter();
        testRequestCriteriaSearchFilter.setAssesseeId("user.01");

        Pageable pageable = PageRequest.of(0, 5);

        contextInfo = Constant.SUPER_USER_CONTEXT;

        assertDoesNotThrow(() -> {
            testRequestService.searchTestRequests(testRequestCriteriaSearchFilter, pageable, contextInfo);
        });

        TestRequestCriteriaSearchFilter testRequestCriteriaSearchFilter1 = new TestRequestCriteriaSearchFilter();
        testRequestCriteriaSearchFilter1.setName("TestRequest 1");

        Pageable pageable1 = PageRequest.of(0, 5);

        contextInfo = Constant.SUPER_USER_CONTEXT;

        assertDoesNotThrow(() -> {
            testRequestService.searchTestRequests(testRequestCriteriaSearchFilter1, pageable1, contextInfo);
        });

    }

    @Test
    @Transactional
    void searchTestRequests(){

        TestRequestCriteriaSearchFilter testRequestCriteriaSearchFilter = new TestRequestCriteriaSearchFilter();
        testRequestCriteriaSearchFilter.setAssesseeId("user.01");

        contextInfo = Constant.SUPER_USER_CONTEXT;

        assertDoesNotThrow(() -> {
            testRequestService.searchTestRequests(testRequestCriteriaSearchFilter, contextInfo);
        });

        TestRequestCriteriaSearchFilter testRequestCriteriaSearchFilter1 = new TestRequestCriteriaSearchFilter();
        testRequestCriteriaSearchFilter1.setName("TestRequest 1");

        contextInfo = Constant.SUPER_USER_CONTEXT;

        assertDoesNotThrow(() -> {
            testRequestService.searchTestRequests(testRequestCriteriaSearchFilter1, contextInfo);
        });

    }

    @Test
    @Transactional
    void getTestRequestById(){

        // Test with no test request id
        contextInfo = Constant.SUPER_USER_CONTEXT;

        assertThrows(InvalidParameterException.class, ()-> {
            testRequestService.getTestRequestById("",contextInfo);
        });

        // Test with test request id that does not exist
        String testRequestId = "TestRequest.08";

        assertThrows(DoesNotExistException.class, ()-> {
            testRequestService.getTestRequestById(testRequestId, contextInfo);
        });

    }

    @Test
    @Transactional
    void validateTestRequest() throws InvalidParameterException, DoesNotExistException {

        contextInfo = Constant.SUPER_USER_CONTEXT;

        // Test without Test Request
        assertThrows(InvalidParameterException.class, () -> {
            testRequestService.validateTestRequest(Constant.CREATE_VALIDATION,null, contextInfo);
        });

        // Test for test request without name

        TestRequestEntity testRequestEntity = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        testRequestEntity.setName(null);

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request with incorrect validation key

        TestRequestEntity testRequestEntity2 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        assertThrows(InvalidParameterException.class, ()->{
            testRequestService.validateTestRequest("INCORRECT_VALIDATION", testRequestEntity2, contextInfo);
        });

        // Test for test request without version

        TestRequestEntity testRequestEntity1 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        testRequestEntity1.setVersion(null);

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.UPDATE_VALIDATION, testRequestEntity1, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request without test request url

        TestRequestEntity testRequestEntity5 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        testRequestEntity5.setTestRequestUrls(null);

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity5, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request without test request url username

        TestRequestEntity testRequestEntity6 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        List<TestRequestUrlEntity> testRequestUrlEntities6 = new ArrayList<>(testRequestEntity6.getTestRequestUrls());

        testRequestUrlEntities6.get(0).setUsername(null);

        Set<TestRequestUrlEntity> testRequestUrlEntities6Set = new HashSet<>(testRequestUrlEntities6);

        testRequestEntity6.setTestRequestUrls(testRequestUrlEntities6Set);

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity6, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request without test request url password

        TestRequestEntity testRequestEntity7 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        List<TestRequestUrlEntity> testRequestUrlEntities7 = new ArrayList<>(testRequestEntity7.getTestRequestUrls());

        testRequestUrlEntities7.get(0).setPassword(null);

        Set<TestRequestUrlEntity> testRequestUrlEntities7Set = new HashSet<>(testRequestUrlEntities7);

        testRequestEntity7.setTestRequestUrls(testRequestUrlEntities7Set);

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity7, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request without test request url firApiBaseUrl

        TestRequestEntity testRequestEntity8 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        List<TestRequestUrlEntity> testRequestUrlEntities8 = new ArrayList<>(testRequestEntity8.getTestRequestUrls());

        testRequestUrlEntities8.get(0).setFhirApiBaseUrl(null);

        Set<TestRequestUrlEntity> testRequestUrlEntities8Set = new HashSet<>(testRequestUrlEntities8);

        testRequestEntity8.setTestRequestUrls(testRequestUrlEntities8Set);

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity8, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request with incorrect approver id

        TestRequestEntity testRequestEntity9 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        UserEntity userEntity = new UserEntity();

        // user.09 does not exist
        userEntity.setId("user.09");

        testRequestEntity9.setApprover(userEntity);

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity9, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request with assessee status inactive

        TestRequestEntity testRequestEntity10 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        UserEntity userEntity1 = new UserEntity();
        userEntity1.setId("user.03");

        testRequestEntity10.setAssessee(userEntity1);

        contextInfo = new ContextInfo(
                "inactiveAssessee@yopmail.com",
                "user.03",
                "password",
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(UserServiceConstants.ROLE_ID_ASSESSEE))
        );

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity10, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request with supplied assessee does not have correct role

        contextInfo = Constant.SUPER_USER_CONTEXT;

        TestRequestEntity testRequestEntity11 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        UserEntity userEntity11 = new UserEntity();

        userEntity11.setId("user.06");

        testRequestEntity11.setAssessee(userEntity11);

        contextInfo = new ContextInfo(
                "notAssessee@yopmail.com",
                "user.06",
                "password",
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(UserServiceConstants.ROLE_ID_TESTER))
        );

        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity11, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request with incorrect assessee Id

        contextInfo = Constant.SUPER_USER_CONTEXT;

        TestRequestEntity testRequestEntity12 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        UserEntity userEntity12 = new UserEntity();

        // user.09 does not exist
        userEntity12.setId("user.09");

        testRequestEntity12.setAssessee(userEntity12);


        assertThrows(DataValidationErrorException.class, ()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity12, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Test for test request with proper data

        TestRequestEntity testRequestEntity13 = new TestRequestEntity(testRequestService.getTestRequestById("TestRequest.01", contextInfo));

        testRequestEntity13.setId("TestRequest.09");

        assertDoesNotThrow(()->{
            List<ValidationResultInfo> errors = testRequestService.validateTestRequest(Constant.CREATE_VALIDATION, testRequestEntity13, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

    }

    @Test
    @Transactional
    void validateChangeState(){

        contextInfo = Constant.SUPER_USER_CONTEXT;

        // Call method with rejected state key
        assertThrows(DataValidationErrorException.class, ()-> {
            List<ValidationResultInfo> errors = testRequestService.validateChangeState("TestRequest.01", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Call method with empty test request urls
        assertThrows(DataValidationErrorException.class, ()-> {
            List<ValidationResultInfo> errors = testRequestService.validateChangeState("TestRequest.03", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Call method with empty test request urls
        assertThrows(DataValidationErrorException.class, ()-> {
            List<ValidationResultInfo> errors = testRequestService.validateChangeState("TestRequest.04", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

//        // Call method with empty fhirApiBaseUrl
//        assertThrows(DataValidationErrorException.class, ()-> {
//            List<ValidationResultInfo> errors = testRequestService.validateChangeState("TestRequest.06", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED, contextInfo);
//            if(!errors.isEmpty()){
//                throw new DataValidationErrorException("errors", errors);
//            }
//        });

        // Call method with invalid state transition
        assertThrows(DataValidationErrorException.class, ()-> {
            List<ValidationResultInfo> errors = testRequestService.validateChangeState("TestRequest.02", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Call method with invalid state transition
        assertThrows(DataValidationErrorException.class, ()-> {
            List<ValidationResultInfo> errors = testRequestService.validateChangeState("TestRequest.02", "Testing", "test.request.status.inprogress1", contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

        // Call method with correct data
        assertDoesNotThrow(() -> {
            List<ValidationResultInfo> errors = testRequestService.validateChangeState("TestRequest.02", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS, contextInfo);
            if(!errors.isEmpty()){
                throw new DataValidationErrorException("errors", errors);
            }
        });

    }

    @Test
    @Transactional
    void getApplicationStats() {
        assertDoesNotThrow(()->{
            testRequestService.getApplicationsStats(contextInfo);
        });
    }

    @Test
    @Transactional
    void changeState(){

        contextInfo = Constant.SUPER_USER_CONTEXT;

        // Call method with rejected state key
        assertThrows(DataValidationErrorException.class, ()-> {
            testRequestService.changeState("TestRequest.01", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_REJECTED, contextInfo);
        });

        // Call method with empty test request urls
        assertThrows(DataValidationErrorException.class, ()-> {
            testRequestService.changeState("TestRequest.03", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED, contextInfo);

        });

        // Call method with empty test request urls
        assertThrows(DataValidationErrorException.class, ()-> {
            testRequestService.changeState("TestRequest.04", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED, contextInfo);
        });
        // Call method with empty fhirApiBaseUrl
        assertDoesNotThrow(()-> {
            testRequestService.changeState("TestRequest.06", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_ACCEPTED, contextInfo);
        });

        // Call method with invalid state transition
        assertThrows(DataValidationErrorException.class, ()-> {
            testRequestService.changeState("TestRequest.02", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_PENDING, contextInfo);
        });

        // Call method with invalid state transition
        assertThrows(DataValidationErrorException.class, ()-> {
            testRequestService.changeState("TestRequest.02", "Testing", "test.request.status.inprogress1", contextInfo);
        });

        // Call method with correct data
        assertDoesNotThrow(() -> {
            testRequestService.changeState("TestRequest.02", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_INPROGRESS, contextInfo);
        });

        assertDoesNotThrow(() -> {
            testRequestService.changeState("TestRequest.11", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED, contextInfo);
        });

        assertDoesNotThrow(() -> {
            testRequestService.changeState("TestRequest.16", "Testing", TestRequestServiceConstants.TEST_REQUEST_STATUS_FINISHED, contextInfo);
        });

    }

    @Test
    @Transactional
    void getDashboard(){

        contextInfo = Constant.SUPER_USER_CONTEXT;

        assertDoesNotThrow(() -> {
            testRequestService.getDashboard(contextInfo);
        });
    }
}
