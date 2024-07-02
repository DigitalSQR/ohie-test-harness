package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseOptionServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseOptionMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestcaseOptionServiceImplTest extends TestingHarnessToolTestConfiguration {
    ContextInfo contextInfo;
    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private TestcaseOptionService testcaseOptionService;

    @Autowired
    private TestcaseOptionServiceMockImpl testcaseOptionServiceMock;

    @Autowired
    private TestcaseOptionMapper testcaseOptionMapper;

    @BeforeEach
    @Override
    public void init() {
        super.init();
        testcaseOptionServiceMock.init();
        contextInfo = new ContextInfo();
    }

    @AfterEach
    void after() {
        testcaseOptionServiceMock.clear();
    }

    @Test
    void testCreateTestcaseOption() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException {

//        // Test case 1 : Create a new testcase
        TestcaseOptionEntity testcaseOptionEntity = new TestcaseOptionEntity();
        testcaseOptionEntity.setId("testcaseOption.03");
        testcaseOptionEntity.setName("Testcase Option 3");
        testcaseOptionEntity.setDescription("Just a testcase Option");
        testcaseOptionEntity.setState("testcase.option.status.active");
        testcaseOptionEntity.setRank(2);
        testcaseOptionEntity.setSuccess(false);
        testcaseOptionEntity.setCreatedBy("ivasiwala");
        testcaseOptionEntity.setUpdatedBy("ivasiwala");
        testcaseOptionEntity.setCreatedAt(new Date());
        testcaseOptionEntity.setUpdatedAt(new Date());
        testcaseOptionEntity.setTestcase(testcaseService.getTestcaseById("testcase.222",contextInfo));
        TestcaseOptionEntity resultantTestcaseOptionEntity = testcaseOptionService.createTestcaseOption(testcaseOptionEntity, contextInfo);
        assertEquals(testcaseOptionEntity.getId(), resultantTestcaseOptionEntity.getId());


        // Test 2: With empty id
        testcaseOptionEntity.setId(null);
        testcaseOptionEntity.setName("Verify code Existence");
        testcaseOptionEntity.setDescription("Testcase client repository functional 3");
        testcaseOptionEntity.setRank(1);
        testcaseOptionEntity.setSuccess(false);
        TestcaseEntity testcaseEntity = testcaseService.getTestcaseById("testcase.03",contextInfo);
        testcaseOptionEntity.setTestcase(testcaseEntity);
        resultantTestcaseOptionEntity = testcaseOptionService.createTestcaseOption(testcaseOptionEntity, contextInfo);
        assertEquals(testcaseOptionEntity.getName(), resultantTestcaseOptionEntity.getName());

        //Test case 3 : testcaseEntity is null
        TestcaseOptionEntity testcaseOptionEntity2 = null;

        assertThrows(InvalidParameterException.class, () -> {
            testcaseOptionService.createTestcaseOption(testcaseOptionEntity2, contextInfo);
        });

        // Test case 4 : Create a new testcase with same id
        assertThrows(DataValidationErrorException.class, () -> {
            testcaseOptionService.createTestcaseOption(testcaseOptionEntity, contextInfo);
        });

        // Test case 5 : Create a new testcaseOption to validate Testcase
        TestcaseOptionEntity testcaseOptionEntity1 = new TestcaseOptionEntity();
        testcaseOptionEntity1.setName("Testcase Option 1");
        testcaseOptionEntity1.setDescription("Dummy testcase Option");
        testcaseOptionEntity1.setState("testcase.option.status.active");
        testcaseOptionEntity1.setRank(2);
        testcaseOptionEntity1.setSuccess(false);

        TestcaseEntity testcaseEntity1 = new TestcaseEntity();
        testcaseEntity1.setId("testcase.test");
        testcaseOptionEntity1.setTestcase(testcaseEntity1);

        assertThrows(DataValidationErrorException.class, () -> {
            testcaseOptionService.createTestcaseOption(testcaseOptionEntity1, contextInfo);
        });

        assertDoesNotThrow(()-> {
            TestcaseOptionEntity testcaseOptionEntity3 = new TestcaseOptionEntity();
            testcaseOptionEntity3.setRank(123);
            testcaseOptionEntity3.setSuccess(Boolean.TRUE);
            testcaseOptionEntity3.setTestcase(new TestcaseEntity());

            TestcaseOptionEntity copyTestcaseOptionEntity = new TestcaseOptionEntity(testcaseOptionEntity3);

            List<TestcaseOptionEntity> testcaseOptionEntities = new ArrayList<>();
            testcaseOptionEntities.add(testcaseOptionEntity3);

            testcaseOptionMapper.dtoToModel(testcaseOptionMapper.modelToDto(testcaseOptionEntities));
        });
    }


    @Test
    @Transactional
    void testUpdateTestcaseOption() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        // Test case 1 : Update the testcase data
        TestcaseOptionEntity testcaseOptionEntity = testcaseOptionService.getTestcaseOptionById("testcaseOption.01", contextInfo);

        testcaseOptionEntity.setRank(2);
        testcaseOptionEntity.setName("Updated testcaseOption name");
        testcaseOptionEntity.setDescription("Updated testcaseOption description");

        TestcaseOptionEntity updatedTestcaseOption = testcaseOptionService.updateTestcaseOption(testcaseOptionEntity, contextInfo);
        assertEquals(testcaseOptionEntity.getName(), updatedTestcaseOption.getName());
        assertEquals(testcaseOptionEntity.getDescription(), updatedTestcaseOption.getDescription());
        assertEquals(testcaseOptionEntity.getRank(), updatedTestcaseOption.getRank());


        //Test case 2 : testcaseEntity is null
        TestcaseOptionEntity testcaseOptionEntity1 = null;

        assertThrows(InvalidParameterException.class, () -> {
            testcaseOptionService.updateTestcaseOption(testcaseOptionEntity1, contextInfo);
        });

        // Test case 3 : Given testcase id does not exist
        TestcaseOptionEntity testcaseOptionEntity2 = new TestcaseOptionEntity();
        testcaseOptionEntity2.setId("testcaseOption.10");
        testcaseOptionEntity2.setName("Updated name");

        assertThrows(DataValidationErrorException.class, () -> {
            testcaseOptionService.updateTestcaseOption(testcaseOptionEntity2, contextInfo);
        });
    }


    @Test
    void testGetTestcaseOption() throws InvalidParameterException, DoesNotExistException {

        // Test case 1: Passing testcase id as null
        assertThrows(InvalidParameterException.class, () -> {
            testcaseOptionService.getTestcaseOptionById(null, contextInfo);
        });

        // Test case 2: Testcase data does not exist with given id
        assertThrows(DoesNotExistException.class, () -> {
            testcaseOptionService.getTestcaseOptionById("testcaseOption.100", contextInfo);
        });

    }

    @Test
    @Transactional
    void testValidateTestcaseOption() throws InvalidParameterException, DoesNotExistException, OperationFailedException {
        TestcaseOptionEntity testcaseoptionEntity = testcaseOptionService.getTestcaseOptionById("testcaseOption.01", contextInfo);
        String validationTypeKey = "update.validation.test";
        assertThrows(InvalidParameterException.class, () -> {
            testcaseOptionService.validateTestcaseOption(validationTypeKey, testcaseoptionEntity, contextInfo);
        });

        //when validation type key is null
        assertThrows(InvalidParameterException.class, () -> {
            testcaseOptionService.validateTestcaseOption(null,testcaseoptionEntity , contextInfo);
        });

        // when testcaseOption entity is null
        assertThrows(InvalidParameterException.class,() ->{
            testcaseOptionService.validateTestcaseOption(validationTypeKey,null , contextInfo);
        });

    }
}
