package com.argusoft.path.tht.reportmanagement.mock;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.repository.TestcaseResultRepository;
import com.argusoft.path.tht.testcasemanagement.mock.ComponentServiceMockImpl;
import com.argusoft.path.tht.testprocessmanagement.mock.TestRequestServiceMockImpl;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.usermanagement.mock.UserServiceMockImpl;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;

@Component
public class TestcaseResultServiceMockImpl {
    @Autowired
    private TestcaseResultRepository testcaseResultRepository;
    @Autowired
    private TestRequestRepository testRequestRepository;

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
        testcaseResultEntity.setParentTestcaseResult(null);
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
        testcaseResultRepository.deleteAll();
        testcaseResultRepository.flush();
        testRequestServiceMock.clear();
        userServiceMock.clear();

    }
}
