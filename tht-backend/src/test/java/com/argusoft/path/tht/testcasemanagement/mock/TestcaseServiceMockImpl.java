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

    public void init(){
        String userId = "ivasiwala";
        specificationServiceMock.init();
        createTestcase(1, "testcase.status.active", "testcase.test" ,"testcase.test", "testcase.222", true, "specification.01", userId);
    }

    public TestcaseEntity createTestcase(int rank, String state, String name, String description, String id, Boolean manual, String specificationId, String userId){
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
        return testcaseRepository.save(testcase);
    }


    public void clear() {
        testcaseRepository.deleteAll();
        testcaseRepository.flush();
        specificationServiceMock.clear();
    }

}
