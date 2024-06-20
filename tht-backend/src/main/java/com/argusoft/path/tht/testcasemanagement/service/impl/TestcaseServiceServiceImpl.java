package com.argusoft.path.tht.testcasemanagement.service.impl;

import com.argusoft.path.tht.fileservice.constant.TestcaseEntityDocumentTypes;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.fileservice.service.DocumentService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.CommonStateChangeValidator;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseRepository;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.repository.TestcaseVariableRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseVariableService;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.conformance.create.response.ConformanceResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.conformance.create.restresponse.ConformanceStatementCreateRecord;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.request.DeployRequest;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.response.Actor;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.response.DeployResponse;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.response.Error;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.deploy.response.Warning;
import com.argusoft.path.tht.testcasemanagement.testbed.services.ConformanceStatementManagementRestService;
import com.argusoft.path.tht.testcasemanagement.testbed.services.TestSuiteManagementRestService;
import com.argusoft.path.tht.testcasemanagement.validator.TestcaseValidator;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * This TestcaseServiceServiceImpl contains implementation for Testcase service.
 *
 * @author Dhruv
 */
@Service
public class TestcaseServiceServiceImpl implements TestcaseService {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseServiceServiceImpl.class);

    TestcaseRepository testcaseRepository;

    TestcaseVariableRepository testcaseVariableRepository;
    private ApplicationContext applicationContext;
    private SpecificationService specificationService;
    private ComponentService componentService;
    private TestcaseOptionService testcaseOptionService;
    private DocumentService documentService;
    private TestcaseService testcaseService;

    private TestcaseVariableService testcaseVariableService;

    @Value("${testbed.specification-api}")
    private String specificationAPIKey;

    @Value("${testbed.system-api}")
    private String systemAPIKey;

    private TestSuiteManagementRestService testSuiteManagementRestService;

    private ConformanceStatementManagementRestService conformanceStatementManagementRestService;

    @Autowired
    public void setTestcaseRepository(TestcaseRepository testcaseRepository) {
        this.testcaseRepository = testcaseRepository;
    }

    @Autowired
    public void setTestcaseVariableRepository(TestcaseVariableRepository testcaseVariableRepository) {
        this.testcaseVariableRepository = testcaseVariableRepository;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setSpecificationService(SpecificationService specificationService) {
        this.specificationService = specificationService;
    }

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Autowired
    public void setComponentService(ComponentService componentService) {
        this.componentService = componentService;
    }

    @Autowired
    public void setTestcaseOptionService(TestcaseOptionService testcaseOptionService) {
        this.testcaseOptionService = testcaseOptionService;
    }


    @Autowired
    public void setTestcaseService(TestcaseService testcaseService) {
        this.testcaseService = testcaseService;
    }

    @Autowired
    public void setTestcaseVariableService(TestcaseVariableService testcaseVariableService) {
        this.testcaseVariableService = testcaseVariableService;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseEntity createTestcase(TestcaseEntity testcaseEntity,
                                         MultipartFile zipFileForAutomationTest,
                                         ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {

            if (testcaseEntity == null) {
                LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseServiceServiceImpl.class.getSimpleName());
                throw new InvalidParameterException("testcaseEntity is missing");
            }

            //TODO : This will get removed if required java
            if(!Boolean.TRUE.equals(testcaseEntity.getManual())){
                testcaseEntity.setTestcaseRunEnvironment(TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENT_EU_TESTBED);
            }

            defaultValueCreateTestCase(testcaseEntity, contextInfo);

            TestcaseValidator.validateCreateUpdateTestCase(Constant.CREATE_VALIDATION,
                    testcaseEntity,
                    zipFileForAutomationTest,
                    this,
                    specificationService,
                    applicationContext,
                    contextInfo);

        testcaseEntity = testcaseRepository.saveAndFlush(testcaseEntity);

        if (TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENT_EU_TESTBED.equals(testcaseEntity.getTestcaseRunEnvironment())) {
            String testSuiteId = createTestSuiteId(testcaseEntity,0);
            String sutActorId = getSutActorIdFromMultipartFile(zipFileForAutomationTest);
            DeployResponse deployResponse = createDocumentOfTestsuiteZipAndDeployOnTestbed(testcaseEntity, testSuiteId, zipFileForAutomationTest, contextInfo);
            String sutActorApiKey = getSutActorApiKey(sutActorId, deployResponse);
            testcaseEntity.setSutActorApiKey(sutActorApiKey);
            testcaseEntity.setTestSuiteId(testSuiteId);
            createConformanceStatementFromSutActorApiKey(sutActorApiKey);

            testcaseEntity = testcaseRepository.saveAndFlush(testcaseEntity);

        }
        return testcaseEntity;
    }

    private void createConformanceStatementFromSutActorApiKey(String sutActorApiKey) throws DataValidationErrorException, OperationFailedException {
        // create conformance statement
        try {
            ConformanceStatementCreateRecord conformanceStatement = conformanceStatementManagementRestService.createConformanceStatement(systemAPIKey, sutActorApiKey);

            if(!conformanceStatement.isCreated()){
                if(conformanceStatement.conformanceResponse().getErrorCode().equals("514")) {
                    LOGGER.info("Conformance statement for this actor is already created");
                }
                else {
                    ConformanceResponse conformanceResponse = conformanceStatement.conformanceResponse();
                    ValidationResultInfo validationResultInfo = new ValidationResultInfo();
                    validationResultInfo.setLevel(ErrorLevel.ERROR);
                    validationResultInfo.setMessage(conformanceResponse.getErrorDescription());
                    throw new DataValidationErrorException("Failed to create conformance statement", Collections.singletonList(validationResultInfo));
                }
            }
        } catch (URISyntaxException e) {
            throw new OperationFailedException("Incorrect URI "+e.getInput());
        }
    }

    private String getSutActorApiKey(String sutActorId, DeployResponse deployResponse) {
        List<Actor> actors = deployResponse.getIdentifiers().getSpecifications().get(0).getActors();
        Optional<Actor> sutActor = actors.stream().filter(actor -> actor.getName().equals(sutActorId)).findFirst();
        String sutActorApiKey = sutActor.get().getIdentifier();
        return sutActorApiKey;
    }

    private String getSutActorIdFromMultipartFile(MultipartFile multipartFile) throws OperationFailedException, DataValidationErrorException {
        ZipInputStream zipInputStream = null;
        Set<String> actorsIdList = new HashSet<>();
        try {
            // 1. Read the MultipartFile into a ZipInputStream
            InputStream multipartFileStream = multipartFile.getInputStream();
            zipInputStream = new ZipInputStream(multipartFileStream);

            ZipEntry entry;

            // 3. Loop through each entry in the zip file
            while ((entry = zipInputStream.getNextEntry()) != null) {
                
                if (!entry.isDirectory() && entry.getName().endsWith(".xml")) {
                    // 4. For XML files, perform modification
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    Document doc = builder.parse(cloneInputStream(zipInputStream));

                    // Find and update <testsuite> id
                    NodeList nodeList = doc.getElementsByTagName("gitb:actor");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element element = (Element) nodeList.item(i);
                        String role = element.getAttribute("role");
                        if("SUT".equals(role)){
                            actorsIdList.add(element.getAttribute("id"));
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new OperationFailedException("Caught Exception while getting SUT actor's id from testcase ", e);
        } finally {
            if(zipInputStream!=null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    throw new OperationFailedException("Problem occurred while getting SUT actor's id from testcase",e);
                }
            }
        }

        if(actorsIdList.size() == 1){
            return actorsIdList.iterator().next();
        }
        else if(actorsIdList.size() > 1){
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("Within a test suite, all the testcases must have the same SUT actor.");
            validationResultInfo.setElement("actorId");

            throw new DataValidationErrorException("Within a test suite, the same actor cannot have two roles, namely SUT and non-SUT.", Collections.singletonList(validationResultInfo));
        }
        throw new OperationFailedException("Unable to find SUT actor id from Testcase, please make sure to add that");
    }

    private static String createTestSuiteId(TestcaseEntity testcaseEntity, Integer version) {
        return testcaseEntity.getId() +
                "~" +
                testcaseEntity.getName() +
                "~" +
                version;
    }

    private DeployResponse createDocumentOfTestsuiteZipAndDeployOnTestbed(TestcaseEntity testcaseEntity, String testSuiteId ,MultipartFile zipFileForAutomationTest, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        DocumentEntity documentEntity = createDocumentForTestSuite(testcaseEntity);
        zipFileForAutomationTest = updateTestSuiteIdInZip(zipFileForAutomationTest,testSuiteId);
        saveDocumentEntity(zipFileForAutomationTest, documentEntity, contextInfo);
        DeployRequest deployRequest = createDeployRequest(documentEntity, contextInfo);
        DeployResponse deployResponse = deployTestSuite(deployRequest, contextInfo);
        handleDeploymentResponse(deployResponse);
        return deployResponse;
    }

    private MultipartFile updateTestSuiteIdInZip(MultipartFile multipartFile, String testSuiteId) throws OperationFailedException{
        try {
            // 1. Read the MultipartFile into a ZipInputStream
            InputStream multipartFileStream = multipartFile.getInputStream();
            ZipInputStream zipInputStream = new ZipInputStream(multipartFileStream);

            // 2. Prepare output ByteArrayOutputStream and ZipOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            ZipEntry entry;

            // 3. Loop through each entry in the zip file
            while ((entry = zipInputStream.getNextEntry()) != null) {

                // Create a temporary byte array output stream to store the modified content
                ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream();

                if (!entry.isDirectory() && entry.getName().endsWith(".xml")) {
                    // 4. For XML files, perform modification
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    Document doc = builder.parse(cloneInputStream(zipInputStream));

                    // Find and update <testsuite> id
                    NodeList nodeList = doc.getElementsByTagName("testsuite");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element element = (Element) nodeList.item(i);
                        element.setAttribute("id", testSuiteId);
                    }

                    // Serialize the modified XML to the temporary output stream
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(tempOutputStream);
                    transformer.transform(source, result);
                } else {
                    // For other files, just copy them as they are
                    IOUtils.copy(zipInputStream, tempOutputStream);
                }

                // Add the content from the temporary output stream to the output zip
                zipOutputStream.putNextEntry(new ZipEntry(entry.getName()));
                zipOutputStream.write(tempOutputStream.toByteArray());
                zipOutputStream.closeEntry();
            }

            // 5. Finish writing the zip and convert it back to MultipartFile
            zipOutputStream.finish();
            InputStream updatedZipInputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return new MockMultipartFile(multipartFile.getName(),
                            multipartFile.getOriginalFilename(), multipartFile.getContentType(), updatedZipInputStream);
        } catch (Exception e) {
            throw new OperationFailedException("Caught Exception while updating id in testsuite ", e);
        }
    }

    private static InputStream cloneInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, byteArrayOutputStream);
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private DocumentEntity createDocumentForTestSuite(TestcaseEntity testcaseEntity){
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setRefObjUri(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI);
        documentEntity.setRefId(testcaseEntity.getId());
        documentEntity.setDocumentType(TestcaseEntityDocumentTypes.TESTCASE_TESTSUITE_AUTOMATION_ZIP.getKey());
        documentEntity.setRank(1);
        return documentEntity;
    }

    private void saveDocumentEntity(MultipartFile zipFileForAutomationTest, DocumentEntity documentEntity, ContextInfo contextInfo) throws OperationFailedException, DataValidationErrorException, DoesNotExistException, InvalidParameterException {
        this.getDocumentService().createDocument(documentEntity, zipFileForAutomationTest, contextInfo);
    }

    private DeployRequest createDeployRequest(DocumentEntity documentEntity, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException {
        DeployRequest deployRequest = new DeployRequest();
        deployRequest.setSpecification(specificationAPIKey);
        ByteArrayResource byteArrayResourceByDocumentId = documentService.getByteArrayResourceByDocumentId(documentEntity.getId(), contextInfo);
        deployRequest.setTestSuite(Base64.encodeBase64String(byteArrayResourceByDocumentId.getByteArray()));
        return deployRequest;
    }

    private DeployResponse deployTestSuite(DeployRequest deployRequest, ContextInfo contextInfo) throws OperationFailedException{
        try {
            return testSuiteManagementRestService.deployTestSuite(deployRequest, contextInfo);
        }catch (URISyntaxException ex){
            throw new OperationFailedException("Incorrect URI "+ex.getInput());
        }catch (RestClientException ex){
            throw new OperationFailedException("Testbed Communication error "+ex.getMessage());
        }
    }

    private void handleDeploymentResponse(DeployResponse deployResponse) throws DataValidationErrorException {
        if (Boolean.FALSE.toString().equals(deployResponse.getCompleted())) {
            List<ValidationResultInfo> validationResultInfos = new ArrayList<>();
            if (deployResponse.getErrors()!=null && !deployResponse.getErrors().isEmpty()) {
                addValidationResults(deployResponse.getErrors(), ErrorLevel.ERROR, validationResultInfos);
            }
            if (deployResponse.getWarnings()!=null && !deployResponse.getWarnings().isEmpty()) {
                addValidationResults( ErrorLevel.WARN, deployResponse.getWarnings(), validationResultInfos);
            }
            String errorMessage = validationResultInfos.isEmpty() ? "Unable to deploy testsuite zip file" : "";
            throw new DataValidationErrorException(errorMessage, validationResultInfos);
        }
    }

    private void addValidationResults(List<Error> items, ErrorLevel level, List<ValidationResultInfo> validationResultInfos) {
        for (Error item : items) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setMessage(item.getDescription());
            validationResultInfo.setElement(item.getLocation());
            validationResultInfo.setLevel(level);
            validationResultInfos.add(validationResultInfo);
        }
    }

    private void addValidationResults( ErrorLevel level, List<Warning> items, List<ValidationResultInfo> validationResultInfos) {
        for (Warning item : items) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setMessage(item.getDescription());
            validationResultInfo.setElement(item.getLocation());
            validationResultInfo.setLevel(level);
            validationResultInfos.add(validationResultInfo);
        }
    }


    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseEntity updateTestcase(TestcaseEntity testcaseEntity,
                                         MultipartFile zipFileForAutomationTest,
                                         ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {

        if (testcaseEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("testcaseEntity is missing");
        }

        //TODO : This will get removed if required java
        if(!Boolean.TRUE.equals(testcaseEntity.getManual())){
            testcaseEntity.setTestcaseRunEnvironment(TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENT_EU_TESTBED);
        }

        TestcaseValidator.validateCreateUpdateTestCase(Constant.UPDATE_VALIDATION,
                testcaseEntity,
                zipFileForAutomationTest,
                this,
                specificationService,
                applicationContext,
                contextInfo);

        setDefaultStateForTestcaseVariable(testcaseEntity);
        List<TestcaseVariableEntity> testcaseVariables = new ArrayList<>();
        if(testcaseEntity.getTestcaseVariables() != null && !testcaseEntity.getTestcaseVariables().isEmpty()){
            for(TestcaseVariableEntity testcaseVariableEntity : testcaseEntity.getTestcaseVariables()){
                try{
                    TestcaseVariableEntity originalTestcaseVariableEntity = testcaseVariableService.getTestcaseVariableById(testcaseVariableEntity.getId(), contextInfo);
                    originalTestcaseVariableEntity.setTestcaseVariableKey(testcaseVariableEntity.getTestcaseVariableKey());
                    originalTestcaseVariableEntity.setDefaultValue(testcaseVariableEntity.getDefaultValue());
                    originalTestcaseVariableEntity.setRoleId(testcaseVariableEntity.getRoleId());
                    originalTestcaseVariableEntity.setState(testcaseVariableEntity.getState());
                    testcaseVariableEntity = originalTestcaseVariableEntity;
                }
                catch(Exception e){

                }
                testcaseVariables.add(testcaseVariableEntity);
            }
        }


        testcaseEntity.setTestcaseVariables(testcaseVariables);

        testcaseEntity = testcaseRepository.saveAndFlush(testcaseEntity);

        if (zipFileForAutomationTest != null
                && TestcaseServiceConstants.TESTCASE_RUN_ENVIRONMENT_EU_TESTBED.equals(testcaseEntity.getTestcaseRunEnvironment())) {
            String testSuiteId = testcaseEntity.getTestSuiteId();
            // Split the testSuiteId into parts
            String[] parts = testSuiteId.split("~");

            // Extract the version string
            String versionString = parts[2];

            // Parse version string to integer
            int versionInteger = Integer.parseInt(versionString);

            // Create updated testSuiteId
            String updatedTestSuiteId = createTestSuiteId(testcaseEntity,  versionInteger+1);
            String sutActorId = getSutActorIdFromMultipartFile(zipFileForAutomationTest);
            DeployResponse deployResponse = createDocumentOfTestsuiteZipAndDeployOnTestbed(testcaseEntity, updatedTestSuiteId ,zipFileForAutomationTest, contextInfo);

            String sutActorApiKey = getSutActorApiKey(sutActorId, deployResponse);

            testcaseEntity.setSutActorApiKey(sutActorApiKey);
            testcaseEntity.setTestSuiteId(updatedTestSuiteId);

            createConformanceStatementFromSutActorApiKey(sutActorApiKey);

            testcaseEntity = testcaseRepository.saveAndFlush(testcaseEntity);
        }
        return testcaseEntity;
    }

    private void setDefaultStateForTestcaseVariable(TestcaseEntity testcaseEntity) {
        if(testcaseEntity.getTestcaseVariables() != null && !testcaseEntity.getTestcaseVariables().isEmpty()) {
            for (TestcaseVariableEntity testcaseVariableEntity : testcaseEntity.getTestcaseVariables()) {
                if(testcaseVariableEntity.getTestcase() == null){
                    testcaseVariableEntity.setTestcase(testcaseEntity);
                }
                if(testcaseVariableEntity.getState() == null) {
                    testcaseVariableEntity.setState(TestcaseServiceConstants.TESTCASE_VARIABLE_STATUS_ACTIVE);
                }
            }
        }
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<TestcaseEntity> searchTestcases(
            TestcaseCriteriaSearchFilter testcaseSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseEntity> testcaseEntitySpecification = testcaseSearchFilter.buildSpecification(contextInfo);
        return this.testcaseRepository.findAll(testcaseEntitySpecification, pageable);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<TestcaseEntity> searchLikeTestcases(
            TestcaseCriteriaSearchFilter testcaseSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseEntity> testcaseEntitySpecification = testcaseSearchFilter.buildLikeSpecification(contextInfo);
        return this.testcaseRepository.findAll(testcaseEntitySpecification, pageable);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public void bulkTestcaseUpload(@ModelAttribute("file") MultipartFile file, ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {
        try {
            // Determine the file type
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.endsWith(".csv")) {
                readCsvFile(file.getInputStream(), contextInfo);
            } else if (originalFilename != null && (originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
                readExcelFile(file.getInputStream(), contextInfo);
            } else {
                throw new OperationFailedException("Invalid file type");
            }
        } catch (IOException e) {
            throw new OperationFailedException("OperationFailed while reading file", e);
        }
    }

    private void readCsvFile(InputStream inputStream, ContextInfo contextInfo) throws IOException, InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        StringBuilder result = new StringBuilder();
        try (Reader reader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
            int rowIndex = 0;
            for (CSVRecord record : csvParser) {
                if (rowIndex < 4) {
                    rowIndex++;
                    continue;
                }
                if(!createTestcaseConfigurationByCSVRecord(record, contextInfo)) {
                    break;
                }
            }
        }
    }

    private void readExcelFile(InputStream inputStream, ContextInfo contextInfo) throws IOException, InvalidFormatException, InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        Workbook workbook = new XSSFWorkbook(inputStream); // Use XSSFWorkbook for .xlsx files
        Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet
        int rowIndex = 0;
        for (Row row : sheet) {
            if (rowIndex < 4) {
                rowIndex++;
                continue;
            }
            if(!createTestcaseConfigurationByRow(row, contextInfo)) {
                break;
            }
        }
        workbook.close();
    }

    @Override
    public List<TestcaseEntity> searchTestcases(
            TestcaseCriteriaSearchFilter testcaseSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<TestcaseEntity> testcaseEntitySpecification = testcaseSearchFilter.buildSpecification(contextInfo);
        return this.testcaseRepository.findAll(testcaseEntitySpecification);
    }

    private boolean createTestcaseConfigurationByCSVRecord(CSVRecord csvRecord, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException {
        int cellIndex = 0;
        String reference = TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE;
        ComponentEntity componentEntity = new ComponentEntity();
        SpecificationEntity specificationEntity = new SpecificationEntity();
        TestcaseEntity testcaseEntity = new TestcaseEntity();
        TestcaseOptionEntity testcaseOptionEntity = new TestcaseOptionEntity();
        String parentReference = "";
        for (String cellValue : csvRecord) {
            cellIndex++;
            if (cellIndex == 1) {
                reference = cellValue;
                if(!StringUtils.hasLength(reference)) {
                    return false;
                }
                continue;
            }
            if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_COMPONENT.equals(reference)) {
                if (cellIndex == 2) {
                    componentEntity.setName(cellValue);
                } else if (cellIndex == 3) {
                    componentEntity.setDescription(cellValue);
                } else {
                    break;
                }
            } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_SPECIFICATION.equals(reference)) {
                if (cellIndex == 2) {
                    specificationEntity.setName(cellValue);
                } else if (cellIndex == 3) {
                    specificationEntity.setDescription(cellValue);
                } else if (cellIndex == 4) {
                    specificationEntity.setFunctional(TestcaseServiceConstants.SHEET_COLUMN_FUNCTIONAL_WORKFLOW_VALUE_FUNCTIONAL.equals(cellValue));
                } else if (cellIndex == 5) {
                    specificationEntity.setRequired(TestcaseServiceConstants.SHEET_COLUMN_REQUIRED_RECOMMENDED_VALUE_REQUIRED.equals(cellValue));
                } else if (cellIndex == 6) {
                    parentReference = cellValue;
                } else {
                    break;
                }
            } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE.equals(reference)) {
                if (cellIndex == 2) {
                    testcaseEntity.setName(cellValue);
                } else if (cellIndex == 3) {
                    testcaseEntity.setDescription(cellValue);
                } else if (cellIndex == 4) {
                    testcaseEntity.setQuestionType(cellValue);
                } else if (cellIndex == 5) {
                    testcaseEntity.setFailureMessage(cellValue);
                } else if (cellIndex == 6) {
                    parentReference = cellValue;
                } else {
                    break;
                }
            } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE_OPTION.equals(reference)) {
                if (cellIndex == 2) {
                    testcaseOptionEntity.setName(cellValue);
                } else if (cellIndex == 3) {
                    testcaseOptionEntity.setDescription(cellValue);
                } else if (cellIndex == 4) {
                    testcaseOptionEntity.setSuccess("TRUE".equalsIgnoreCase(cellValue));
                } else if (cellIndex == 5) {
                    parentReference = cellValue;
                } else {
                    break;
                }
            }
        }

        if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_COMPONENT.equals(reference)) {
            componentEntity.setState(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE);
            componentService.createComponent(componentEntity, contextInfo);
        } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_SPECIFICATION.equals(reference)) {
            specificationEntity.setState(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE);
            ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
            if(!StringUtils.hasLength(parentReference)) {
                throw new OperationFailedException("No component provided for the specification : " + specificationEntity.getName());
            }

            componentCriteriaSearchFilter.setName(parentReference);
            componentCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<ComponentEntity> componentEntities = componentService.searchComponents(componentCriteriaSearchFilter, contextInfo);
            if(componentEntities.isEmpty()) {
                throw new OperationFailedException("No Component Found with name : " + parentReference);
            }
            specificationEntity.setComponent(componentEntities.get(0));

            specificationService.createSpecification(specificationEntity, contextInfo);
        } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE.equals(reference)) {
            testcaseEntity.setState(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
            testcaseEntity.setManual(Boolean.TRUE);
            SpecificationCriteriaSearchFilter specificationCriteriaSearchFilter = new SpecificationCriteriaSearchFilter();
            if(!StringUtils.hasLength(parentReference)) {
                throw new OperationFailedException("No specification provided for the testcase : " + testcaseEntity.getName());
            }

            specificationCriteriaSearchFilter.setName(parentReference);
            specificationCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<SpecificationEntity> specificationEntities = specificationService.searchSpecifications(specificationCriteriaSearchFilter, contextInfo);
            if(specificationEntities.isEmpty()) {
                throw  new OperationFailedException("No Specification found with name : " + parentReference);
            }
            testcaseEntity.setSpecification(specificationEntities.get(0));

            this.createTestcase(testcaseEntity, null, contextInfo);
        } else {
            testcaseOptionEntity.setState(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_ACTIVE);
            TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter = new TestcaseCriteriaSearchFilter();
            if(!StringUtils.hasLength(parentReference)) {
                throw new OperationFailedException("No testcase provided for the testcase Option : " + testcaseOptionEntity.getName());
            }
            testcaseCriteriaSearchFilter.setName(parentReference);
            testcaseCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<TestcaseEntity> testcaseEntities = this.searchTestcases(testcaseCriteriaSearchFilter, contextInfo);
            if(testcaseEntities.isEmpty()) {
                throw new OperationFailedException("No testcase found with name: " + parentReference);
            }
            testcaseOptionEntity.setTestcase(testcaseEntities.get(0));

            testcaseOptionService.createTestcaseOption(testcaseOptionEntity, contextInfo);
        }
        return true;
    }

    private boolean createTestcaseConfigurationByRow(Row row, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DataValidationErrorException, DoesNotExistException {
        int cellIndex = 0;
        String reference = TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE;
        ComponentEntity componentEntity = new ComponentEntity();
        SpecificationEntity specificationEntity = new SpecificationEntity();
        TestcaseEntity testcaseEntity = new TestcaseEntity();
        TestcaseOptionEntity testcaseOptionEntity = new TestcaseOptionEntity();
        String parentReference = "";
        for (Cell cell : row) {
            cellIndex++;
            if (cellIndex == 1) {
                reference = cell.getStringCellValue();
                if(!StringUtils.hasLength(reference)) return false;
                continue;
            }
            if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_COMPONENT.equals(reference)) {
                if (cellIndex == 2) {
                    componentEntity.setName(cell.getStringCellValue());
                } else if (cellIndex == 3) {
                    componentEntity.setDescription(cell.getStringCellValue());
                }
            } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_SPECIFICATION.equals(reference)) {
                if (cellIndex == 2) {
                    specificationEntity.setName(cell.getStringCellValue());
                } else if (cellIndex == 3) {
                    specificationEntity.setDescription(cell.getStringCellValue());
                } else if (cellIndex == 4) {
                    specificationEntity.setFunctional(TestcaseServiceConstants.SHEET_COLUMN_FUNCTIONAL_WORKFLOW_VALUE_FUNCTIONAL.equals(cell.getStringCellValue()));
                } else if (cellIndex == 5) {
                    specificationEntity.setRequired(TestcaseServiceConstants.SHEET_COLUMN_REQUIRED_RECOMMENDED_VALUE_REQUIRED.equals(cell.getStringCellValue()));
                } else if (cellIndex == 6) {
                    parentReference = cell.getStringCellValue();
                } else {
                    break;
                }
            } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE.equals(reference)) {
                if (cellIndex == 2) {
                    testcaseEntity.setName(cell.getStringCellValue());
                } else if (cellIndex == 3) {
                    testcaseEntity.setDescription(cell.getStringCellValue());
                } else if (cellIndex == 4) {
                    testcaseEntity.setQuestionType(cell.getStringCellValue());
                } else if (cellIndex == 5) {
                    testcaseEntity.setFailureMessage(cell.getStringCellValue());
                } else if (cellIndex ==6){
                    parentReference = cell.getStringCellValue();
                } else {
                    break;
                }
            } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE_OPTION.equals(reference)) {
                if (cellIndex == 2) {
                    testcaseOptionEntity.setName(cell.getStringCellValue());
                } else if (cellIndex == 3) {
                    testcaseOptionEntity.setDescription(cell.getStringCellValue());
                } else if (cellIndex == 4) {
                    testcaseOptionEntity.setSuccess("YES".equalsIgnoreCase(cell.getStringCellValue()));
                } else if (cellIndex == 5){
                    parentReference = cell.getStringCellValue();
                } else {
                    break;
                }
            }
        }

        if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_COMPONENT.equals(reference)) {
            componentEntity.setState(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE);
            componentService.createComponent(componentEntity, contextInfo);
        } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_SPECIFICATION.equals(reference)) {
            specificationEntity.setState(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE);
            ComponentCriteriaSearchFilter componentCriteriaSearchFilter = new ComponentCriteriaSearchFilter();
            if(!StringUtils.hasLength(parentReference)) {
                throw new OperationFailedException("No component provided for the specification : " + specificationEntity.getName());
            }
            componentCriteriaSearchFilter.setName(parentReference);
            componentCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<ComponentEntity> componentEntities = componentService.searchComponents(componentCriteriaSearchFilter, contextInfo);
            if(componentEntities.isEmpty()) {
                throw new OperationFailedException("No Component Found with name : " + parentReference);
            }
            specificationEntity.setComponent(componentEntities.get(0));

            specificationService.createSpecification(specificationEntity, contextInfo);
        } else if (TestcaseServiceConstants.SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE.equals(reference)) {
            testcaseEntity.setState(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);
            testcaseEntity.setManual(Boolean.TRUE);
            SpecificationCriteriaSearchFilter specificationCriteriaSearchFilter = new SpecificationCriteriaSearchFilter();
            if(!StringUtils.hasLength(parentReference)) {
                throw new OperationFailedException("No specification provided for the testcase : " + testcaseEntity.getName());
            }
            specificationCriteriaSearchFilter.setName(parentReference);
            specificationCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<SpecificationEntity> specificationEntities = specificationService.searchSpecifications(specificationCriteriaSearchFilter, contextInfo);
            if(specificationEntities.isEmpty()) {
                throw  new OperationFailedException("No Specification found with name : " + parentReference);
            }
            testcaseEntity.setSpecification(specificationEntities.get(0));

            this.createTestcase(testcaseEntity, null, contextInfo);
        } else {
            testcaseOptionEntity.setState(TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_ACTIVE);
            TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter = new TestcaseCriteriaSearchFilter();
            if(!StringUtils.hasLength(parentReference)) {
                throw new OperationFailedException("No testcase provided for the testcase Option : " + testcaseOptionEntity.getName());
            }
            testcaseCriteriaSearchFilter.setName(parentReference);
            testcaseCriteriaSearchFilter.setNameSearchType(SearchType.EXACTLY);
            List<TestcaseEntity> testcaseEntities = this.searchTestcases(testcaseCriteriaSearchFilter, contextInfo);
            if(testcaseEntities.isEmpty()) {
                throw new OperationFailedException("No testcase found with name: " + parentReference);
            }
            testcaseOptionEntity.setTestcase(testcaseEntities.get(0));

            testcaseOptionService.createTestcaseOption(testcaseOptionEntity, contextInfo);
        }
        return true;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public TestcaseEntity getTestcaseById(String testcaseId,
                                          ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (!StringUtils.hasLength(testcaseId)) {
            throw new InvalidParameterException("TestcaseId is missing");
        }
        TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter = new TestcaseCriteriaSearchFilter(testcaseId);
        List<TestcaseEntity> testcaseEntities = this.searchTestcases(testcaseCriteriaSearchFilter, contextInfo);
        return testcaseEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("Testcase does not found with id : " + testcaseId));
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<ValidationResultInfo> validateTestcase(
            String validationTypeKey,
            MultipartFile zipFileForAutomationTest,
            TestcaseEntity testcaseEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {

        if (testcaseEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, TestcaseServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("testcaseEntity is missing");
        }

        return TestcaseValidator.validateTestCase(validationTypeKey, testcaseEntity, zipFileForAutomationTest ,this, specificationService, applicationContext, contextInfo);
    }

    @Override
    public TestcaseEntity changeState(String testcaseId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        TestcaseEntity testcaseEntity = this.getTestcaseById(testcaseId, contextInfo);

        CommonStateChangeValidator.validateStateChange(TestcaseServiceConstants.TESTCASE_STATUS, TestcaseServiceConstants.TESTCASE_STATUS_MAP, testcaseEntity.getState(), stateKey, errors);

        testcaseEntity.setState(stateKey);
        testcaseEntity = testcaseRepository.saveAndFlush(testcaseEntity);

        return testcaseEntity;
    }


    @Override
    public TestcaseEntity changeRank(String testcaseId, Integer rank, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        TestcaseEntity testcaseEntity = this.getTestcaseById(testcaseId, contextInfo);
        Integer oldRank = testcaseEntity.getRank();

        testcaseEntity.setRank(rank);
        TestcaseValidator.validateTestcaseEntityOrder(testcaseEntity,errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    errors);
        }
        testcaseRepository.saveAndFlush(testcaseEntity);

        TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter = new TestcaseCriteriaSearchFilter();
        testcaseCriteriaSearchFilter.setSpecificationId(testcaseEntity.getSpecification().getId());
        testcaseCriteriaSearchFilter.setMinRank(Integer.min(oldRank, rank));
        testcaseCriteriaSearchFilter.setMaxRank(Integer.max(oldRank, rank));
        List<TestcaseEntity> testcases = this.searchTestcases(testcaseCriteriaSearchFilter, contextInfo);

        for(TestcaseEntity currentTestcase : testcases){
            int testcaseRank = currentTestcase.getRank();
            if(!currentTestcase.getId().equals(testcaseEntity.getId())){
                if(oldRank > testcaseRank && testcaseRank >= rank){
                    currentTestcase.setRank(testcaseRank + 1);
                }
                else {
                    if (rank >= testcaseRank && testcaseRank > oldRank) {
                        currentTestcase.setRank(testcaseRank - 1);
                    }
                }
            }
            testcaseRepository.saveAndFlush(currentTestcase);
        }
        return testcaseEntity;
    }


    private void defaultValueCreateTestCase(TestcaseEntity testcaseEntity, ContextInfo contextInfo) throws InvalidParameterException {
        if (!StringUtils.hasLength(testcaseEntity.getId())) {
            testcaseEntity.setId(UUID.randomUUID().toString());
        }
        testcaseEntity.setState(TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE);

        if(testcaseEntity.getTestcaseVariables() != null && !testcaseEntity.getTestcaseVariables().isEmpty()){
            for(TestcaseVariableEntity testcaseVariableEntity : testcaseEntity.getTestcaseVariables()){
                testcaseVariableEntity.setState(TestcaseServiceConstants.TESTCASE_VARIABLE_STATUS_ACTIVE);
            }
        }

        TestcaseCriteriaSearchFilter searchFilter = new TestcaseCriteriaSearchFilter();

        testcaseEntity.setRank(1);
        if (testcaseEntity.getSpecification() != null) {
            searchFilter.setSpecificationId(testcaseEntity.getSpecification().getId());
            List<TestcaseEntity> testCases = this.searchTestcases(searchFilter, Constant.SINGLE_PAGE_SORT_BY_RANK, contextInfo).getContent();
            if (!testCases.isEmpty()) {
                testcaseEntity.setRank(testCases.get(0).getRank() + 1);
            }
        }
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public TestcaseService getTestcaseService() {
        return testcaseService;
    }

    @Autowired
    public void setTestSuiteManagementRestService(TestSuiteManagementRestService testSuiteManagementRestService) {
        this.testSuiteManagementRestService = testSuiteManagementRestService;
    }

    @Autowired
    public void setConformanceStatementManagementRestService(ConformanceStatementManagementRestService conformanceStatementManagementRestService) {
        this.conformanceStatementManagementRestService = conformanceStatementManagementRestService;
    }
}
