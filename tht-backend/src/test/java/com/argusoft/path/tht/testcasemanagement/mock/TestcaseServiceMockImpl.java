package com.argusoft.path.tht.testcasemanagement.mock;

import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.repository.SpecificationRepository;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TestcaseServiceMockImpl {

    @Autowired
    private SpecificationServiceMockImpl specificationServiceMock;

    @Autowired
    private SpecificationRepository specificationRepository;

    @Autowired
    private TestcaseRepository testcaseRepository;

    public void init() {
        String userId = "ivasiwala";
        specificationServiceMock.init();
        createTestcase(1, "testcase.status.active", "testcase.test", "testcase.test", "testcase.222", true, "specification.01", userId,null);
        createTestcase(3, "testcase.status.active", "Verify code membership", "testcase for client repository", "testcase.03", true, "specification.01", userId,null);
        createTestcase(6, "testcase.status.active", "Verify duplicate Patients", "testcase for client repository", "testcase.02", false, "specification.10", userId,"CRF1TestCase1");
        createTestcase(0, "testcase.status.active", "Verify mismatch records", "testcase for client repository", "testcase.10", true, "specification.10", userId,"CRF1TestCase1");

    }

    public TestcaseEntity createTestcase(int rank, String state, String name, String description, String id, Boolean manual, String specificationId, String userId,String beanName) {
        TestcaseEntity testcase = new TestcaseEntity();
        testcase.setRank(rank);
        testcase.setState(state);
        testcase.setName(name);
        testcase.setDescription(description);
        testcase.setId(id);
        testcase.setManual(manual);
        testcase.setSpecification(specificationRepository.findById(specificationId).get());
        testcase.setCreatedBy(userId);
        testcase.setUpdatedBy(userId);
        testcase.setCreatedAt(new Date());
        testcase.setUpdatedAt(new Date());
        if(beanName != null){
            testcase.setBeanName(beanName);
        }
        return testcaseRepository.save(testcase);
    }


    public void clear() {
        testcaseRepository.deleteAll();
        testcaseRepository.flush();
        specificationServiceMock.clear();
    }

}
