package com.argusoft.path.tht.reportmanagement.mock;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestResultRelationRepository;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultAttributesRepository;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultRepository;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.mock.ComponentServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.mock.TestRequestServiceMockImpl;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.usermanagement.mock.UserServiceMockImpl;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
public class TestcaseResultServiceMockImpl {
    @Autowired
    private TestcaseResultRepository testcaseResultRepository;
    @Autowired
    private TestRequestRepository testRequestRepository;

    @Autowired
    TestResultRelationRepository testResultRelationRepository;


    @Autowired
    TestcaseResultAttributesRepository testcaseResultAttributesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRequestServiceMockImpl testRequestServiceMock;

    @Autowired
    private UserServiceMockImpl userServiceMock;

    @Autowired
    private ComponentServiceMockImpl componentServiceMock;


    @Transactional
    public void init() {
        userServiceMock.init();
        testRequestServiceMock.init();


        createTestcaseResult("TestcaseResult.01","TestRequest.01",null,
                null,null,false,false,
                "com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo",
                "TestRequest.02", true,true,true,null,
                "AAA",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                true,1,"user.06");

        createTestcaseResult("TestcaseResult.02","TestRequest.01","TestcaseResult.01",
                null,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo",
                "component.client.registry",
                true,true,true,null,
                "Client Registry (CR)",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                true,2,"user.06");

        createTestcaseResult("TestcaseResult.03","TestRequest.01","TestcaseResult.02",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo",
                "specification.cr.crwf.1",
                true,false,false,null,
                "CRWF-1",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,false,true,
                true,3,"user.06");

        createTestcaseResult("TestcaseResult.04","TestRequest.01","TestcaseResult.03",
                63920L,"Passed",true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.cr.crwf.1.1",
                true,false,false,null,
                "Verify Create Patient",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,false,true,
                true,4,"user.06");

        createTestcaseResult("TestcaseResult.06","TestRequest.01","TestcaseResult.02",
                null,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.cr.crf.9.1",
                false,true,false,null,
                "Does the system provide features for user management such as creating, modifying, or deleting users from the system?",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING,false,true,
                false,41,"user.06");
        createTestcaseResult("TestcaseResult.07","TestRequest.11",null,
                null,null,true,false,
                "com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo",
                "TestRequest.11",
                false,true,false,null,
                "TestRequest 11",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS,true,true,
                false,5,"user.06");
        createTestcaseResult("TestcaseResult.08","TestRequest.11","TestcaseResult.07",
                null,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo",
                "component.02",
                false,true,false,null,
                "Component 2",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,true,true,
                false,6,"user.06");
        createTestcaseResult("TestcaseResult.09","TestRequest.11","TestcaseResult.08",
                null,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo",
                "specification.01",
                false,true,false,null,
                "Specification 1",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,true,true,
                false,7,"user.06");
        createTestcaseResult("TestcaseResult.10","TestRequest.11","TestcaseResult.09",
                468L,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.222",
                false,true,false,null,
                "Testcase 222",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,true,true,
                false,8,"user.06");
        createTestcaseResult("TestcaseResult.11","TestRequest.11","TestcaseResult.09",
                658L,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.03",
                false,true,false,null,
                "Testcase 03",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,true,true,
                false,9,"user.06");
        createTestcaseResult("TestcaseResult.12","TestRequest.12",null,
                null,null,true,false,
                "com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo",
                "TestRequest.12",
                false,true,false,null,
                "Does the system provide features for user management such as creating, modifying, or deleting users from the system?",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS,false,true,
                false,10,"user.06");
        createTestcaseResult("TestcaseResult.13","TestRequest.12","TestcaseResult.12",
                null,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo",
                "component.02",
                false,true,false,null,
                "Component 2",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS,true,true,
                false,11,"user.06");
        createTestcaseResult("TestcaseResult.14","TestRequest.12","TestcaseResult.13",
                null,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo",
                "specification.01",
                false,true,false,null,
                "Specification 1",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS,true,true,
                false,12,"user.06");
        createTestcaseResult("TestcaseResult.15","TestRequest.12","TestcaseResult.14",
                468L,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.222",
                false,true,false,null,
                "Testcase 222",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,true,true,
                false,13,"user.06");
        createTestcaseResult("TestcaseResult.16","TestRequest.12","TestcaseResult.14",
                535L,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.03",
                false,true,false,null,
                "Testcase 03",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING,true,true,
                false,14,"user.06");
        createTestcaseResult("TestcaseResult.17","TestRequest.13",null,
                null,null,true,false,
                "com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo",
                "TestRequest.13",
                false,true,false,null,
                "Does the system provide features for user management such as creating, modifying, or deleting users from the system?",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,false,true,
                false,15,"user.06");
        createTestcaseResult("TestcaseResult.18","TestRequest.13","TestcaseResult.17",
                null,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo",
                "component.02",
                false,true,false,null,
                "Component 2",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,16,"user.06");
        createTestcaseResult("TestcaseResult.19","TestRequest.13","TestcaseResult.18",
                null,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo",
                "specification.01",
                false,true,false,null,
                "Specification 1",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,17,"user.06");
        createTestcaseResult("TestcaseResult.20","TestRequest.13","TestcaseResult.19",
                468L,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.222",
                false,true,false,null,
                "Testcase 222",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,18,"user.06");
        createTestcaseResult("TestcaseResult.21","TestRequest.11","TestcaseResult.19",
                535L,null,true,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.03",
                false,true,false,null,
                "Testcase 03",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,19,"user.06");
        createTestcaseResult("TestcaseResult.22","TestRequest.14",null,
                null,null,false,false,
                TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                "TestRequest.14",
                true,false,false,null,
                "Does the system provide features for user management such as creating, modifying, or deleting users from the system?",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,false,true,
                false,20,"user.06");
        createTestcaseResult("TestcaseResult.23","TestRequest.14","TestcaseResult.22",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo",
                "component.04",
                true,false,false,null,
                "Component 4",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,21,"user.06");
        createTestcaseResult("TestcaseResult.24","TestRequest.14","TestcaseResult.23",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo",
                "specification.06",
                true,false,false,null,
                "Specification 6",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,22,"user.06");
        createTestcaseResult("TestcaseResult.25","TestRequest.14","TestcaseResult.24",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.12",
                true,false,false,null,
                "Testcase 12",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,23,"user.06");
        createTestcaseResult("TestcaseResult.26","TestRequest.15",null,
                null,null,false,false,
                TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                "TestRequest.15",
                true,false,false,null,
                "Does the system provide features for user management such as creating, modifying, or deleting users from the system?",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,false,true,
                false,24,"user.06");
        createTestcaseResult("TestcaseResult.27","TestRequest.15","TestcaseResult.26",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo",
                "component.09",
                true,false,false,null,
                "Component 9",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,25,"user.06");
        createTestcaseResult("TestcaseResult.28","TestRequest.15","TestcaseResult.27",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo",
                "specification.13",
                true,false,false,null,
                "Specification 13",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,26,"user.06");
        createTestcaseResult("TestcaseResult.29","TestRequest.15","TestcaseResult.28",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.13",
                true,false,false,null,
                "Testcase 13",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,true,true,
                false,27,"user.06");
        createTestcaseResult("TestcaseResult.30","TestRequest.16",null,
                null,null,false,false,
                TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                "TestRequest.16",
                false,true,false,null,
                "Does the system provide features for user management such as creating, modifying, or deleting users from the system?",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,false,true,
                false,28,"user.06");
        createTestcaseResult("TestcaseResult.31","TestRequest.16","TestcaseResult.30",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo",
                "component.10",
                false,true,false,null,
                "Component 10",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,true,true,
                false,29,"user.06");
        createTestcaseResult("TestcaseResult.32","TestRequest.16","TestcaseResult.31",
                null,null,false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo",
                "specification.14",
                false,true,false,null,
                "Specification 14",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,true,true,
                false,30,"user.06");
        createTestcaseResult("TestcaseResult.33","TestRequest.16","TestcaseResult.32",
                null,"Test Fail",false,false,
                "com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo",
                "testcase.14",
                false,true,false,null,
                "Testcase 14",TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED,true,true,
                false,31,"user.06");


    }

    public TestcaseResultEntity createTestcaseResult(String id, String testRequestId, String parentTestcaseResultId, Long duration, String message, Boolean success,
                                                     Boolean hasSystemError, String refObjUri, String refId, Boolean automated, Boolean manual,
                                                     Boolean recommended, String description, String name, String state, Boolean functional, Boolean required,
                                                     Boolean workflow, Integer rank, String testerId)
    {

        TestcaseResultEntity testcaseResultEntity = new TestcaseResultEntity();
        HashSet<TestcaseResultAttributesEntity> testcaseResultAttributesEntities = new HashSet<>();
        testcaseResultEntity.setTestcaseResultAttributesEntities(testcaseResultAttributesEntities);
        testcaseResultEntity.setTestRequest(testRequestRepository.getReferenceById(testRequestId));
        if(parentTestcaseResultId!=null) {
            testcaseResultEntity.setParentTestcaseResult(testcaseResultRepository.findById(parentTestcaseResultId).get());
        }
        else {
            testcaseResultEntity.setParentTestcaseResult(null);
        }
        testcaseResultEntity.setDuration(duration);
        testcaseResultEntity.setMessage(message);
        testcaseResultEntity.setSuccess(success);
        testcaseResultEntity.setHasSystemError(hasSystemError);
        testcaseResultEntity.setCreatedAt(new Date());
        testcaseResultEntity.setCreatedBy("iwasiwala");
        testcaseResultEntity.setRefObjUri(refObjUri);
        testcaseResultEntity.setTester(userRepository.getReferenceById(testerId));
        testcaseResultEntity.setId(id);
        testcaseResultEntity.setAutomated(automated);
        testcaseResultEntity.setManual(manual);
        testcaseResultEntity.setRefId(refId);
        testcaseResultEntity.setRecommended(recommended);
        testcaseResultEntity.setUpdatedAt(new Date());
        testcaseResultEntity.setUpdatedBy("iwasiwala");
        testcaseResultEntity.setVersion(1L);
        testcaseResultEntity.setDescription(description);
        testcaseResultEntity.setName(name);
        testcaseResultEntity.setState(state);
        testcaseResultEntity.setFunctional(functional);
        testcaseResultEntity.setRequired(required);
        testcaseResultEntity.setWorkflow(workflow);
        testcaseResultEntity.setRank(rank);

        testcaseResultRepository.save(testcaseResultEntity);


        return testcaseResultEntity;
    }

    public void clear() {
        ContextInfo contextInfo = Constant.SUPER_USER_CONTEXT;

        testResultRelationRepository.deleteAll();
        testcaseResultAttributesRepository.deleteAll();
        testResultRelationRepository.flush();
        testcaseResultAttributesRepository.flush();
        try {
            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();

            testcaseResultCriteriaSearchFilter.setRefObjUri(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI);
            List<TestcaseResultEntity> testcaseResults =  testcaseResultRepository.findAll(testcaseResultCriteriaSearchFilter.buildSpecification(contextInfo));
            for(TestcaseResultEntity testcaseResult : testcaseResults){
                testcaseResultRepository.deleteById(testcaseResult.getId());
                testcaseResultRepository.flush();
            }

            testcaseResultCriteriaSearchFilter.setRefObjUri(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI);
            List<TestcaseResultEntity> specificationResults =  testcaseResultRepository.findAll(testcaseResultCriteriaSearchFilter.buildSpecification(contextInfo));
            for(TestcaseResultEntity testcaseResult : specificationResults){
                testcaseResultRepository.deleteById(testcaseResult.getId());
                testcaseResultRepository.flush();
            }

            testcaseResultCriteriaSearchFilter.setRefObjUri(ComponentServiceConstants.COMPONENT_REF_OBJ_URI);
            List<TestcaseResultEntity> componentResults =  testcaseResultRepository.findAll(testcaseResultCriteriaSearchFilter.buildSpecification(contextInfo));
            for(TestcaseResultEntity testcaseResult : componentResults){
                testcaseResultRepository.deleteById(testcaseResult.getId());
                testcaseResultRepository.flush();
            }

            List<TestcaseResultEntity> testRequestResultRemaining = testcaseResultRepository.findAll();

            testcaseResultCriteriaSearchFilter.setRefObjUri(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI);
            List<TestcaseResultEntity> testRequestResults = testcaseResultRepository.findAll(testcaseResultCriteriaSearchFilter.buildSpecification(contextInfo));
            for(TestcaseResultEntity testcaseResult : testRequestResults){
                testcaseResultRepository.deleteById(testcaseResult.getId());
                testcaseResultRepository.flush();
            }
//            testcaseResultRepository.deleteById("TestcaseResult.22");
//
//            testcaseResultRepository.flush();
//
//            testcaseResultRepository.deleteAll();
//            testcaseResultRepository.flush();
            List<TestcaseResultEntity> testRequestResultRemaining1 = testcaseResultRepository.findAll();
            System.out.println(testRequestResultRemaining1);

        } catch (Exception e) {
            System.out.println("----------------------------------------------------\n" + e.toString() +"\n ---------------------------------------------------------");
        }
        testRequestServiceMock.clear();
        userServiceMock.clear();

    }
}
