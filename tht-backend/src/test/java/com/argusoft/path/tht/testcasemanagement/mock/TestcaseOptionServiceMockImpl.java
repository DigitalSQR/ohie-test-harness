package com.argusoft.path.tht.testcasemanagement.mock;

import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseOptionRepository;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TestcaseOptionServiceMockImpl {
    @Autowired
    private TestcaseOptionRepository testcaseOptionRepository;

    @Autowired
    private TestcaseRepository testcaseRepository;

    @Autowired
    private TestcaseServiceMockImpl testcaseServiceMock;

    public void init() {

        String userId = "ivasiwala";
        testcaseServiceMock.init();
        createTestcaseOption("testcaseOption.01","Yes, I've used the system to manually accept or reject merge suggestions.","Criteria to be added",1,"testcase.option.status.active","testcase.03",userId,true);
        createTestcaseOption("testcaseOption.04","Yes, Duplicate Patients are not allowed","Criteria to be added",1,"testcase.option.status.active","testcase.222",userId,true);
        createTestcaseOption("testcaseOption.02","No, I couldn't find a way to choose specifics fields to be merged.","Criteria not to be added",2,"testcase.option.status.active","testcase.03",userId,false);
    }

    public TestcaseOptionEntity createTestcaseOption(String testcaseOptionId, String name, String description, int rank, String state, String testcaseId, String userId,Boolean success) {
        TestcaseOptionEntity testcaseOptionEntity = new TestcaseOptionEntity();
        testcaseOptionEntity.setId(testcaseOptionId);
        testcaseOptionEntity.setName(name);
        testcaseOptionEntity.setDescription(description);
        testcaseOptionEntity.setState(state);
        testcaseOptionEntity.setRank(rank);
        testcaseOptionEntity.setTestcase(testcaseRepository.findById(testcaseId).get());
        testcaseOptionEntity.setCreatedBy(userId);
        testcaseOptionEntity.setUpdatedBy(userId);
        testcaseOptionEntity.setCreatedAt(new Date());
        testcaseOptionEntity.setUpdatedAt(new Date());
        testcaseOptionEntity.setSuccess(success);
        return testcaseOptionRepository.save(testcaseOptionEntity);
    }

    public void clear() {
        testcaseOptionRepository.deleteAll();
        testcaseOptionRepository.flush();
        testcaseServiceMock.clear();
    }
}
