package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseOptionServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseMapper;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseVariableMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestcaseServiceImplTest extends TestingHarnessToolTestConfiguration {

    ContextInfo contextInfo;
    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private TestcaseServiceMockImpl testcaseServiceMock;

    @Autowired
    private TestcaseOptionServiceMockImpl testcaseOptionServiceMock;

    @Autowired
    private TestcaseVariableMapper testcaseVariableMapper;

    @Autowired
    private TestcaseMapper testcaseMapper;

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
        testcaseEntity.setId("testcase.31");
        testcaseEntity.setName("Verify inbound/outbound transaction for test");
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

        assertDoesNotThrow(() -> {

            testcaseEntity.setRank(123);
            testcaseEntity.getRank();
            testcaseEntity.setManual(Boolean.TRUE);
            testcaseEntity.getManual();
            testcaseEntity.setBeanName("TestingTestcase");
            testcaseEntity.getBeanName();
            testcaseEntity.setFailureMessage("Test Fail");
            testcaseEntity.getFailureMessage();
            testcaseEntity.setQuestionType("Type 1");
            testcaseEntity.getQuestionType();
            testcaseEntity.setTestcaseRunEnvironment("TestBed");
            testcaseEntity.getTestcaseRunEnvironment();
            testcaseEntity.setSpecification(new SpecificationEntity());
            testcaseEntity.getSpecification();
            testcaseEntity.setTestSuiteId("TestSuite.01");
            testcaseEntity.getTestSuiteId();
            testcaseEntity.setSutActorApiKey("Key.01");
            testcaseEntity.getSutActorApiKey();

            TestcaseEntity copyTestcaseEntity = new TestcaseEntity(testcaseEntity);

            List<TestcaseVariableEntity> testcaseVariableEntities = new ArrayList<>();
            testcaseVariableEntities.add(new TestcaseVariableEntity());

            testcaseEntity.setTestcaseVariables(testcaseVariableEntities);

            TestcaseVariableEntity testcaseVariableEntity = new TestcaseVariableEntity();
            testcaseVariableEntity.setTestcaseVariableKey("key.01");
            testcaseVariableEntity.getTestcaseVariableKey();
            testcaseVariableEntity.setDefaultValue("Default");
            testcaseVariableEntity.getDefaultValue();
            testcaseVariableEntity.setRoleId("ROLE.TESTER");
            testcaseVariableEntity.getRoleId();
            testcaseVariableEntity.setTestcase(new TestcaseEntity());
            testcaseVariableEntity.getTestcase();

            TestcaseVariableEntity copyTestcaseVariableEntity = new TestcaseVariableEntity(testcaseVariableEntity);


            testcaseVariableMapper.dtoToModel(testcaseVariableMapper.modelToDto(testcaseVariableEntity));
            List<TestcaseVariableEntity> testcaseVariableEntities1 = new ArrayList<>();

            testcaseVariableEntities1.add(testcaseVariableEntity);
            testcaseVariableMapper.dtoToModel(testcaseVariableMapper.modelToDto(testcaseVariableEntities1));

            List<TestcaseEntity> testcaseEntities = new ArrayList<>();
            testcaseEntities.add(testcaseEntity);

            testcaseMapper.dtoToModel(testcaseMapper.modelToDto(testcaseEntities));

            testcaseMapper.setToTestcase("testcase.01");
            testcaseMapper.setToTestcaseId(testcaseEntity);

            testcaseVariableMapper.setToTestcase("testcase.01");
            testcaseVariableMapper.setToTestcaseId(testcaseEntity);
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
        assertThrows(InvalidParameterException.class, () -> {
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
            testcaseService.changeRank("testcase.03", null, contextInfo);
        });
    }

    @Test
    void testChangeTestcaseState() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        // Test case 1 : Update the testcase state
        TestcaseEntity testcaseEntity = testcaseService.getTestcaseById("testcase.03", contextInfo);

        testcaseEntity.setState("testcase.status.inactive");

        TestcaseEntity updatedTestcase = testcaseService.changeState("testcase.03", "testcase.status.inactive", contextInfo);
        assertEquals(testcaseEntity.getState(), updatedTestcase.getState());

    }

    @Test
    @Transactional
    void testBulkTestcaseUpload() {

        // CREATE XLSX FILE AND ADD IT IN MULTIPART FILE

        Path testcaseDir;
        String filePath = "";
        try{
            testcaseDir = Files.createTempDirectory("Testcase");
            filePath = testcaseDir.resolve("testcaseOperation.xml").toString();
        } catch (Exception e){

        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        // Data to write
        String[][] data = {
                {"COMPONENT", "COMPONENT_NAME", "COMPONENT_DESCRIPTION"},
                {"SPECIFICATION", "SPECIFICATION_NAME", "SPECIFICATION_DESCRIPTION", "FUNCTIONAL/WORKFLOW", "REQUIRED/RECOMMENDED", "COMPONENT_NAME"},
                {"TESTCASE", "TESTCASE_NAME", "TESTCASE_DESCRIPTION", "SINGLE_SELECT/MULTI_SELECT", "FAILURE_MESSAGE", "SPECIFICATION_NAME"},
                {"TESTCASE_OPTION", "TESTCASE_OPTION_NAME", "TESTCASE_OPTION_DESCRIPTION", "SUCCESS", "TESTCASE_NAME"},
                {"COMPONENT", "new component 6", "new component"},
                {"SPECIFICATION", "new specification 6", "new specification", "FUNCTIONAL", "RECOMMENDED", "new component 6"},
                {"TESTCASE", "new testcase 6", "new testcase", "SINGLE_SELECT", "FAILURE_MESSAGE", "new specification 6"},
                {"TESTCASE_OPTION", "new testcase option 6", "new testcase option", "YES", "new testcase 6"}
        };

        // Write data to sheet
        int rowNum = 0;
        for (String[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (String field : rowData) {
                Cell cell = row.createCell(colNum++);
                cell.setCellValue(field);
            }
        }

        // Resize all columns to fit the content size
        for (int i = 0; i < data[0].length; i++) {
            sheet.autoSizeColumn(i);
        }
        // Write the output to a ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            workbook.write(byteArrayOutputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Convert ByteArrayOutputStream to MultipartFile
        MultipartFile multipartFile = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", byteArrayOutputStream.toByteArray());

        // Send the multipart file containing xlsx file
        assertDoesNotThrow(() -> {
            testcaseService.bulkTestcaseUpload(multipartFile, contextInfo);
        });

        // CREATE CSV FILE AND ADD IT IN MULTIPART FILE

        // Create CSV content (replace this with your own logic to generate CSV content)
//        String csvContent = "COMPONENT,COMPONENT_NAME,COMPONENT_DESCRIPTION\n" +
//                "new component 4,new component,\n"; // Example content

        String csvContent = "COMPONENT,COMPONENT_NAME,COMPONENT_DESCRIPTION\n" +
                "SPECIFICATION,SPECIFICATION_NAME,SPECIFICATION_DESCRIPTION,FUNCTIONAL/WORKFLOW,REQUIRED/RECOMMENDED,COMPONENT_NAME\n" +
                "TESTCASE,TESTCASE_NAME,TESTCASE_DESCRIPTION,SINGLE_SELECT/MULTI_SELECT,FAILURE_MESSAGE,SPECIFICATION_NAME\n" +
                "TESTCASE_OPTION,TESTCASE_OPTION_NAME,TESTCASE_OPTION_DESCRIPTION,SUCCESS,TESTCASE_NAME\n" +
                "COMPONENT,new component 7,new component\n" +
                "SPECIFICATION,new specification 7,new specification,FUNCTIONAL,RECOMMENDED,new component 6\n" +
                "TESTCASE,new testcase 7,new testcase,SINGLE_SELECT,FAILURE_MESSAGE,new specification 6\n" +
                "TESTCASE_OPTION,new testcase option 7,new testcase option,YES,new testcase 7\n";

        // Convert CSV content to InputStream
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // Create MultipartFile from InputStream
        try {
            MultipartFile multipartFileCSV = new MockMultipartFile("file.csv", "file.csv", MediaType.TEXT_PLAIN_VALUE, inputStream);

            // Send the multipart file containing csv file
            assertDoesNotThrow(() -> {
                testcaseService.bulkTestcaseUpload(multipartFileCSV, contextInfo);
            });
        } catch (IOException e) {

        }




    }

}
