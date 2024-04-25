package com.argusoft.path.tht.reportmanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultAnswerInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestResultRelationRepository;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultRepository;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseOptionServiceMockImpl;
import com.argusoft.path.tht.reportmanagement.mock.TestcaseResultServiceMockImpl;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestcaseResultServiceImplTest extends TestingHarnessToolTestConfiguration {
    ContextInfo contextInfo;
    @Autowired
    private TestcaseResultServiceMockImpl testcaseResultServiceMock;
    @Autowired
    private TestcaseOptionServiceMockImpl testcaseOptionServiceMock;

    @Autowired
    private TestResultRelationRepository testResultRelationRepository;
    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private TestRequestRepository testRequestRepository;

    @Autowired
    private TestcaseResultRepository testcaseResultRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    @Override
    public void init() {
        super.init();
        testcaseOptionServiceMock.init();
        testcaseResultServiceMock.init();
        contextInfo = Constant.SUPER_USER_CONTEXT;
    }
    @AfterEach
    void after() {
        testResultRelationRepository.deleteAll();
        testcaseResultServiceMock.clear();
        testcaseOptionServiceMock.clear();
    }
    @Test
    void testCreateTestcaseResult() throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, VersionMismatchException {


        // Test case 1 : Create a new TestcaseResult

        TestcaseResultEntity testcaseResultEntity = new TestcaseResultEntity();
        HashSet<TestcaseResultAttributesEntity> testcaseResultAttributesEntities = new HashSet<>();
        testcaseResultEntity.setTestcaseResultAttributesEntities(testcaseResultAttributesEntities);
        testcaseResultEntity.setTestRequest(testRequestRepository.findById("TestRequest.02").get());
        testcaseResultEntity.setParentTestcaseResult(null);
        testcaseResultEntity.setDuration(null);
        testcaseResultEntity.setMessage(null);
        testcaseResultEntity.setSuccess(null);
        testcaseResultEntity.setHasSystemError(false);
        testcaseResultEntity.setCreatedAt(new Date());
        testcaseResultEntity.setCreatedBy("iwasiwala");
        testcaseResultEntity.setRefObjUri("com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo");
        testcaseResultEntity.setTester(userRepository.getReferenceById("user.06"));
        testcaseResultEntity.setId("TestcaseResult.05");
        testcaseResultEntity.setAutomated(true);
        testcaseResultEntity.setManual(true);
        testcaseResultEntity.setRefId("TestRequest.02");
        testcaseResultEntity.setRecommended(true);
        testcaseResultEntity.setUpdatedAt(new Date());
        testcaseResultEntity.setUpdatedBy("iwasiwala");
        testcaseResultEntity.setVersion(1L);
        testcaseResultEntity.setDescription(null);
        testcaseResultEntity.setName("AAA");
        testcaseResultEntity.setState(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT);
        testcaseResultEntity.setFunctional(true);
        testcaseResultEntity.setRequired(true);
        testcaseResultEntity.setWorkflow(true);
        testcaseResultEntity.setRank(1);

        TestcaseResultEntity resultantTestcaseResultEntity = testcaseResultService.createTestcaseResult(testcaseResultEntity,contextInfo);
        assertEquals(testcaseResultEntity.getId(),resultantTestcaseResultEntity.getId());


        // Test case 2 : Create a new TestcaseResult with same id

        assertThrows(DataValidationErrorException.class, () -> {
            testcaseResultService.createTestcaseResult(testcaseResultEntity,contextInfo);
        });


        // Test case 3 : With empty id

        testcaseResultEntity.setId(null);
        assertEquals(testcaseResultEntity.getName(),resultantTestcaseResultEntity.getName());


        //Test case 4 : TestcaseResultEntity is null

        TestcaseResultEntity testcaseResultEntity1 = null;
        assertThrows(InvalidParameterException.class, () -> {
            testcaseResultService.createTestcaseResult(testcaseResultEntity1, contextInfo);
        });

    }

    @Test
    @Transactional
    void testUpdateTestcaseResult()
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, VersionMismatchException {

//      Test case 1 :  Update the testcaseResult data

        TestcaseResultEntity testcaseResultEntity = testcaseResultService.getTestcaseResultById("TestcaseResult.01",contextInfo);
        testcaseResultEntity.setMessage("Testing");
        testcaseResultEntity.setGrade("B");
        testcaseResultEntity.setSuccess(false);
        TestcaseResultEntity resultantTestcaseResultEntity = testcaseResultService.updateTestcaseResult(testcaseResultEntity,contextInfo);
        assertEquals(testcaseResultEntity.getMessage(),resultantTestcaseResultEntity.getMessage());
        assertEquals(testcaseResultEntity.getGrade(),resultantTestcaseResultEntity.getGrade());
        assertEquals(testcaseResultEntity.getSuccess(),resultantTestcaseResultEntity.getSuccess());

        // Test case 2 : Given TestcaseResult id does not exist

        TestcaseResultEntity testcaseResultEntity1 = testcaseResultService.getTestcaseResultById("TestcaseResult.02",contextInfo);
        assertThrows(DataValidationErrorException.class, () -> {
            testcaseResultService.changeState(testcaseResultEntity1.getId(),"testcase.result.status.test",contextInfo);
        });

        //Test case 3 : TestcaseResultEntity is null

        TestcaseResultEntity testcaseResultEntity3 = null;

        assertThrows(InvalidParameterException.class, () -> {
            testcaseResultService.updateTestcaseResult(testcaseResultEntity3, contextInfo);
        });


    }

    @Test
    @Transactional
    @Disabled
    void testSubmitTestcaseResult() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {

        TestcaseResultEntity testcaseResultEntity = testcaseResultRepository.findById("TestcaseResult.06").get();

        TestResultRelationEntity testResultRelationEntity = new TestResultRelationEntity();
        testResultRelationEntity.setRefId("testcase.cr.crf.9.1.option.1");
        testResultRelationEntity.setRefObjUri(TestcaseOptionServiceConstants.TESTCASE_OPTION_REF_OBJ_URI);
        testResultRelationEntity.setTestcaseResultEntity(testcaseResultEntity);
        testResultRelationRepository.saveAndFlush(testResultRelationEntity);

        TestResultRelationEntity testcaseTestResultRelationEntity = new TestResultRelationEntity();
        testResultRelationEntity.setRefId("testcase.222");
        testResultRelationEntity.setRefObjUri(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI);
        testResultRelationEntity.setTestcaseResultEntity(testcaseResultEntity);
        testResultRelationEntity.setVersionOfRefEntity(0L);
        testResultRelationRepository.saveAndFlush(testcaseTestResultRelationEntity);

        List<TestcaseResultAnswerInfo>  testcaseResultAnswerInfos = new ArrayList<>();
        TestcaseResultAnswerInfo testcaseResultAnswerInfo = new TestcaseResultAnswerInfo();
        testcaseResultAnswerInfo.setTestcaseResultId(testcaseResultEntity.getId());
        testcaseResultAnswerInfo.setSelectedTestcaseOptionIds(Set.of("testcase.cr.crf.9.1.option.1"));
        testcaseResultAnswerInfos.add(testcaseResultAnswerInfo);


        testcaseResultService.submitTestcaseResult(testcaseResultAnswerInfos,contextInfo);
        TestcaseResultEntity resultantTestcaseResultEntity = testcaseResultRepository.findById("TestcaseResult.05").get();

        assertEquals(resultantTestcaseResultEntity.getState(),TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED);

    }

@Test
void testSearchTestcaseResults() throws InvalidParameterException, OperationFailedException {


    //      Test case 1 :  Search testcaseResult by automated state

    TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
    testcaseResultCriteriaSearchFilter.setAutomated(true);
    testcaseResultCriteriaSearchFilter.setState(List.of(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED));
    List<TestcaseResultEntity> resultantTestcaseResultEntity = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter,contextInfo);
    assertEquals(1,resultantTestcaseResultEntity.size());


    //      Test case 2 :  Search testcaseResult by required state

    TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter1 = new TestcaseResultCriteriaSearchFilter();
    testcaseResultCriteriaSearchFilter.setRequired(true);
    testcaseResultCriteriaSearchFilter.setState(List.of(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED));
    List<TestcaseResultEntity> resultantTestcaseResultEntity1 = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter,contextInfo);
    assertEquals(1,resultantTestcaseResultEntity1.size());


    //      Test case 3 :  Search testcaseResult by functional state

    TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter2 = new TestcaseResultCriteriaSearchFilter();
    testcaseResultCriteriaSearchFilter.setFunctional(true);
    testcaseResultCriteriaSearchFilter.setState(List.of(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED));
    List<TestcaseResultEntity> resultantTestcaseResultEntity2 = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter,contextInfo);
    assertEquals(0,resultantTestcaseResultEntity2.size());


}

@Test
void  testChangeState() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {


//        Test case 1 : Change state of testcaseResult

    String testcaseResultId = "TestcaseResult.06";
    testcaseResultService.changeState(testcaseResultId,TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS,contextInfo);
    TestcaseResultEntity resultantTestcaseResultEntity = testcaseResultService.getTestcaseResultById(testcaseResultId,contextInfo);
    assertEquals(resultantTestcaseResultEntity.getState(),TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS);

    //        Test case 2 : Change state is invalid

    assertThrows(DataValidationErrorException.class, () -> {
        testcaseResultService.changeState(testcaseResultId,"INVALID_STATE",contextInfo);
            });


    //        Test case 2 : With wrong testcaseResultId

    String testcaseResultId1 = "TestcaseResult.100";
    assertThrows(DoesNotExistException.class,()->{
        testcaseResultService.changeState(testcaseResultId1,TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS,contextInfo);
    });

}

@Test
void testGetMultipleTestcaseResultStatus() throws InvalidParameterException, DoesNotExistException, OperationFailedException {
    TestcaseResultEntity testcaseResultEntity = testcaseResultService.getTestcaseResultById("TestcaseResult.03",contextInfo);


//    Test case 1: Check size of multiple TestcaseResultStatus

    List<TestcaseResultEntity> resultantTestcaseResultEntities = testcaseResultService.getMultipleTestcaseResultStatus("TestRequest.01",false,true,true,false,true,false,contextInfo);
   assertEquals(resultantTestcaseResultEntities.size(),2);


//  Test case 2 : With empty testRequestId

   assertThrows(InvalidParameterException.class,()->{
        testcaseResultService.getMultipleTestcaseResultStatus("",false,true,true,false,true,false,contextInfo);
    });


//   Test case 3 : With wrong testRequestId

    assertEquals(0,(testcaseResultService.getMultipleTestcaseResultStatus("TestRequest.100",false,true,true,false,true,false,contextInfo).size()));

    //   assertThrows(DoesNotExistException.class,()->{
//       testcaseResultService.getMultipleTestcaseResultStatus("TestRequest.100",false,true,true,false,true,false,contextInfo);
//
//   });


  }


}