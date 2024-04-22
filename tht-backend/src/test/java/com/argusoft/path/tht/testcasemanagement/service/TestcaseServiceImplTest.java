package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseOptionServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestcaseServiceImplTest extends TestingHarnessToolTestConfiguration {

    ContextInfo contextInfo;
    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private TestcaseOptionService testcaseOptionService;

    @Autowired
    private TestcaseServiceMockImpl testcaseServiceMock;

    @Autowired
    private TestcaseOptionServiceMockImpl testcaseOptionServiceMock;

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
        testcaseServiceMock.clear();
    }
    @Test
    void testCreateTestcase() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException {

        // Test case 1 : Create a new testcase
        TestcaseEntity testcaseEntity = new TestcaseEntity();
        testcaseEntity.setId("testcase.01");
        testcaseEntity.setName("Verify inbound/outbound transaction");
        testcaseEntity.setDescription("Testcase client repository functional 2");
        testcaseEntity.setState("testcase.status.active");
        testcaseEntity.setRank(1);
        testcaseEntity.setSpecification(specificationService.getSpecificationById("specification.01", contextInfo));
        testcaseEntity.setCreatedBy("ivasiwala");
        testcaseEntity.setUpdatedBy("ivasiwala");
        testcaseEntity.setCreatedAt(new Date());
        testcaseEntity.setUpdatedAt(new Date());
        testcaseEntity.setManual(true);
        TestcaseEntity resultantTestcaseEntity = testcaseService.createTestcase(testcaseEntity, null, contextInfo);
        assertEquals(testcaseEntity.getId(), resultantTestcaseEntity.getId());


        // Test 2: With empty id
        testcaseEntity.setId(null);
        testcaseEntity.setName("Verify code Existence");
        testcaseEntity.setDescription("Testcase client repository functional 3");
        testcaseEntity.setRank(1);
        testcaseEntity.setSpecification(specificationService.getSpecificationById("specification.01", contextInfo));

        resultantTestcaseEntity = testcaseService.createTestcase(testcaseEntity, null, contextInfo);
        assertEquals(testcaseEntity.getName(), resultantTestcaseEntity.getName());

        //Test case 3 : testcaseEntity is null
        TestcaseEntity testcaseEntity2 = null;

        assertThrows(InvalidParameterException.class, () -> {
            testcaseService.createTestcase(testcaseEntity2, null, contextInfo);
        });

        // Test case 4 : Create a new testcase with same id
        assertThrows(DataValidationErrorException.class, () -> {
            testcaseService.createTestcase(testcaseEntity, null, contextInfo);
        });

        // Test case 5 : Create a new testcase to validate Specification
        TestcaseEntity testcaseEntity1 = new TestcaseEntity();
        testcaseEntity1.setName("Verify code Existence");
        testcaseEntity1.setDescription("Testcase client repository functional 3");
        testcaseEntity1.setRank(1);
        testcaseEntity1.setManual(true);
        testcaseEntity1.setState("testcase.status.active");

        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId("specification.test");
        testcaseEntity1.setSpecification(specificationEntity);

        assertThrows(DataValidationErrorException.class, () -> {
            testcaseService.createTestcase(testcaseEntity1, null, contextInfo);
        });

    }

    @Test
    @Transactional
    void testUpdateTestcase() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        // Test case 1 : Update the testcase data
        TestcaseEntity testcaseEntity = testcaseService.getTestcaseById("testcase.03", contextInfo);

        testcaseEntity.setRank(2);
        testcaseEntity.setName("Updated testcase name");
        testcaseEntity.setDescription("Updated testcase description");

        TestcaseEntity updatedTestcase = testcaseService.updateTestcase(testcaseEntity, null, contextInfo);
        assertEquals(testcaseEntity.getName(), updatedTestcase.getName());
        assertEquals(testcaseEntity.getDescription(), updatedTestcase.getDescription());
        assertEquals(testcaseEntity.getRank(), updatedTestcase.getRank());


        //Test case 2 : testcaseEntity is null
        TestcaseEntity testcaseEntity1 = null;

        assertThrows(InvalidParameterException.class, () -> {
            testcaseService.updateTestcase(testcaseEntity1, null, contextInfo);
        });

        // Test case 3 : Given testcase id does not exist
        TestcaseEntity testcaseEntity2 = new TestcaseEntity();
        testcaseEntity2.setId("specification.999");
        testcaseEntity2.setName("Updated name");

        assertThrows(DataValidationErrorException.class, () -> {
            testcaseService.updateTestcase(testcaseEntity2, null, contextInfo);
        });

        // Test case 4 : Testcase update with wrong version
//        TestcaseEntity testcaseEntity3 = testcaseService.getTestcaseById("testcase.03", contextInfo);
//        testcaseEntity3.setVersion(null);
//        testcaseEntity3.setName("Verify code membership");
////        assertThrows(DataValidationErrorException.class, () -> {
//            testcaseService.updateTestcase(testcaseEntity3, contextInfo);
////        });


    }

    @Test
    void testGetTestcase() throws InvalidParameterException, DoesNotExistException {

        // Test case 1: Passing testcase id as null
        assertThrows(InvalidParameterException.class, () -> {
            testcaseService.getTestcaseById(null, contextInfo);
        });

        // Test case 2: Testcase data does not exist with given id
        assertThrows(DoesNotExistException.class, () -> {
            testcaseService.getTestcaseById("testcase.01", contextInfo);
        });

    }

    @Test
    @Transactional
    void testValidateTestcase() throws InvalidParameterException, DoesNotExistException, OperationFailedException, DataValidationErrorException, VersionMismatchException {
        TestcaseEntity testcaseEntity = testcaseService.getTestcaseById("testcase.03", contextInfo);
        String validationTypeKey = "update.validation.test";
        assertThrows(InvalidParameterException.class, () -> {
            testcaseService.validateTestcase(validationTypeKey, null, testcaseEntity, contextInfo);
        });

        //when validation type key is null
        assertThrows(InvalidParameterException.class, () -> {
            testcaseService.validateTestcase(null, null, testcaseEntity, contextInfo);
        });

        // when testcase entity is null
        assertThrows(InvalidParameterException.class,() ->{
            testcaseService.validateTestcase(validationTypeKey, null, null, contextInfo);
        });

        //validating beanName with valid beanName
        TestcaseEntity testcaseEntity2 = testcaseService.getTestcaseById("testcase.03", contextInfo);
        testcaseEntity2.setBeanName("CRWF1TestCase1");
        testcaseService.updateTestcase(testcaseEntity, null, contextInfo);

        //validating beanName with invalid beanName
        TestcaseEntity testcaseEntity3 = new TestcaseEntity();
        testcaseEntity3.setBeanName("a".repeat(256));
        assertThrows(DataValidationErrorException.class, () -> {
            testcaseService.updateTestcase(testcaseEntity3, null, contextInfo);
        });

        //validate common foreign key
        TestcaseEntity testcaseEntity4 = new TestcaseEntity();
        testcaseEntity4.getSpecification();
        assertThrows(DataValidationErrorException.class, () -> {
            testcaseService.updateTestcase(testcaseEntity3, null, contextInfo);
        });
    }

    @Test
    void testChangeTestcaseRank() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        // Test case 1 : Update the testcase rank
        TestcaseEntity testcaseEntity = this.testcaseService.changeRank("testcase.02", 1, contextInfo);
        assertEquals(1, testcaseEntity.getRank());

        TestcaseEntity testcaseEntity2 = this.testcaseService.changeRank("testcase.10", 1, contextInfo);
        assertEquals(1, testcaseEntity2.getRank());

        // Test case 2: If invalid rank or null is set
        assertThrows(DataValidationErrorException.class, () -> {
            testcaseService.changeRank("testcase.03",null, contextInfo);
        });
    }

    @Test
    void testChangeTestcaseState() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        // Test case 1 : Update the testcase state
        TestcaseEntity testcaseEntity = testcaseService.getTestcaseById("testcase.03", contextInfo);

        testcaseEntity.setState("testcase.status.inactive");

        TestcaseEntity updatedTestcase = testcaseService.changeState("testcase.03","testcase.status.inactive", contextInfo);
        assertEquals(testcaseEntity.getState(), updatedTestcase.getState());

    }
}
