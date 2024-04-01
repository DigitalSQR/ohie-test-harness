package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.mock.SpecificationServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpecificationServiceImplTest extends TestingHarnessToolTestConfiguration {

    ContextInfo contextInfo;
    @Autowired
    private SpecificationServiceMockImpl specificationServiceMockImpl;
    @Autowired
    private SpecificationService specificationService;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private TestcaseService testcaseService;
    @Autowired
    private TestcaseServiceMockImpl testcaseServiceMock;

    @BeforeEach
    @Override
    public void init() {
        super.init();
        testcaseServiceMock.init();
        contextInfo = new ContextInfo();
    }


    @AfterEach
    void after() {
        testcaseServiceMock.clear();
        specificationServiceMockImpl.clear();
    }

    @Test
    void testCreateSpecification() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException {

        // Test case 1 : Create a new specification
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId("specification.123");
        specificationEntity.setName("Specification 123");
        specificationEntity.setDescription("Specification 123");
        specificationEntity.setState("specification.status.active");
        specificationEntity.setRank(1);
        specificationEntity.setFunctional(true);
        specificationEntity.setRequired(true);
        specificationEntity.setComponent(componentService.getComponentById("component.05", contextInfo));
        specificationEntity.setCreatedBy("ivasiwala");
        specificationEntity.setUpdatedBy("ivasiwala");
        specificationEntity.setCreatedAt(new Date());
        specificationEntity.setUpdatedAt(new Date());

        SpecificationEntity resultantspecificationEntity = specificationService.createSpecification(specificationEntity, contextInfo);
        assertEquals(specificationEntity.getId(), resultantspecificationEntity.getId());

        // Test case 2 : Create a new specification with same id
        assertThrows(DataValidationErrorException.class, () -> {
            specificationService.createSpecification(specificationEntity, contextInfo);
        });

        // Test 3: With empty id
        specificationEntity.setId(null);
        specificationEntity.setName("Specification 196");
        specificationEntity.setDescription("Specification 196");
        specificationEntity.setRank(1);
        specificationEntity.setComponent(componentService.getComponentById("component.05", contextInfo));

        resultantspecificationEntity = specificationService.createSpecification(specificationEntity, contextInfo);
        assertEquals(specificationEntity.getName(), resultantspecificationEntity.getName());

        //Test case 4 : specificationEntity is null
        SpecificationEntity specificationEntity4 = null;

        assertThrows(InvalidParameterException.class, () -> {
            specificationService.createSpecification(specificationEntity4, contextInfo);
        });


        // Test case 5 : Create a new specification to validate foreign key
        SpecificationEntity specificationEntity5 = new SpecificationEntity();
        specificationEntity5.setName("Specification 111");
        specificationEntity5.setDescription("Specification 111");
        specificationEntity5.setState("specification.status.active");
        specificationEntity5.setRank(1);
        specificationEntity5.setFunctional(true);
        specificationEntity5.setRequired(true);
        specificationEntity5.setComponent(componentService.getComponentById("component.05", contextInfo));
        Set<TestcaseEntity> testcaseSet = new HashSet<>();
        TestcaseEntity testcase = new TestcaseEntity();
        testcase.setId("testcase.1");
        testcaseSet.add(testcase);
        specificationEntity5.setTestcases(testcaseSet);

        assertThrows(DataValidationErrorException.class, () -> {
            specificationService.createSpecification(specificationEntity5, contextInfo);
        });

        // Test case 6 : Create a new specification to validate Component
        SpecificationEntity specificationEntity6 = new SpecificationEntity();
        specificationEntity6.setName("Specification 111");
        specificationEntity6.setDescription("Specification 111");
        specificationEntity6.setState("specification.status.active");
        specificationEntity6.setRank(1);
        specificationEntity6.setFunctional(true);
        specificationEntity6.setRequired(true);

        ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setId("component.test");
        specificationEntity6.setComponent(componentEntity);

        assertThrows(DataValidationErrorException.class, () -> {
            specificationService.createSpecification(specificationEntity6, contextInfo);
        });

    }

    @Test
    @Transactional
    public void testUpdateSpecification() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        // Test case 1 : Update the specification data
        SpecificationEntity specificationEntity = specificationService.getSpecificationById("specification.01", contextInfo);

        specificationEntity.setRank(3);
        specificationEntity.setName("Updated specification name");
        specificationEntity.setDescription("Updated specification description");

        SpecificationEntity updatedSpecification = specificationService.updateSpecification(specificationEntity, contextInfo);
        assertEquals(specificationEntity.getName(), updatedSpecification.getName());
        assertEquals(specificationEntity.getDescription(), updatedSpecification.getDescription());
        assertEquals(specificationEntity.getRank(), updatedSpecification.getRank());


        // Test case 2 : Given specification id does not exist
        SpecificationEntity specificationEntity2 = new SpecificationEntity();
        specificationEntity2.setId("specification.999");
        specificationEntity2.setName("Updated name");

        assertThrows(DataValidationErrorException.class, () -> {
            specificationService.updateSpecification(specificationEntity2, contextInfo);
        });

        //Test case 3 : specificationEntity is null
        SpecificationEntity specificationEntity3 = null;

        assertThrows(InvalidParameterException.class, () -> {
            specificationService.updateSpecification(specificationEntity3, contextInfo);
        });

//        // Test case 4 : Update the specification data when meta.version is different
//        SpecificationEntity specificationEntity4 = specificationService.getSpecificationById("specification.01", contextInfo);
//        specificationEntity4.setName("Updated specification name for specificationEntity4");
//        SpecificationEntity updatedSpecification4 = specificationService.updateSpecification(specificationEntity4, contextInfo);
//        updatedSpecification4.setName("Again Updating specification name for specificationEntity4");
//        MetaInfo meta = new MetaInfo();
//        meta.setVersion(5L);
//        updatedSpecification4.setMeta(meta);
//        updatedSpecification4.setMeta(specificationEntity4.getMeta());
//
//        assertThrows(DataValidationErrorException.class, ()->{
//            specificationService.updateSpecification(specificationEntity4, contextInfo);
//        });

    }

    @Test
    @Transactional
    public void testUpdateSpecificationState() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        // Test case 1 : Update the specification state

        SpecificationEntity specificationEntity = specificationService.getSpecificationById("specification.01", contextInfo);

        // Before Update
        assertEquals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE, specificationEntity.getState());

        SpecificationEntity updatedSpecification = specificationService.changeState(specificationEntity.getId(), SpecificationServiceConstants.SPECIFICATION_STATUS_INACTIVE, contextInfo);

        // After update
        assertEquals(SpecificationServiceConstants.SPECIFICATION_STATUS_INACTIVE, updatedSpecification.getState());


        // Test case 2 : State is invalid

        SpecificationEntity specificationEntity2 = specificationService.getSpecificationById("specification.06", contextInfo);

        // Before Update
        assertEquals(SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE, specificationEntity2.getState());

        assertThrows(DataValidationErrorException.class, () -> {
            specificationService.changeState(specificationEntity2.getId(), "specification.status.test", contextInfo);
        });

    }

    @Test
    void testGetSpecification() throws InvalidParameterException, DoesNotExistException {

        // Test case 1: Passing specification id as null
        assertThrows(InvalidParameterException.class, () -> {
            specificationService.getSpecificationById(null, contextInfo);
        });

        // Test case 2: Specification data does not exist with given id
        assertThrows(DoesNotExistException.class, () -> {
            specificationService.getSpecificationById("specification.05", contextInfo);
        });

        // Test case 3: Specification data exist with given id
        SpecificationEntity finalSpecification = specificationService.getSpecificationById("specification.01", contextInfo);
        assertEquals("specification.01", finalSpecification.getId());

    }

    @Test
    void testSearchSpecification() throws InvalidParameterException, DataValidationErrorException, OperationFailedException {

        // Test case 1: Search specification by name, state, and component.

        SpecificationCriteriaSearchFilter specificationSearchFilter1 = new SpecificationCriteriaSearchFilter();
        specificationSearchFilter1.setName("Specification");
        specificationSearchFilter1.setComponentId("component.04");

        List<String> specificationStates1 = new ArrayList<>();
        specificationStates1.add("specification.status.active");
        specificationStates1.add("specification.status.draft");

        specificationSearchFilter1.setState(specificationStates1);

        List<SpecificationEntity> specificationEntities1 = specificationService.searchSpecifications(specificationSearchFilter1, contextInfo);
        assertEquals(2, specificationEntities1.size());


        // Test case 2: Search specification by name and component.
        SpecificationCriteriaSearchFilter specificationSearchFilter2 = new SpecificationCriteriaSearchFilter();

        List<String> specificationStates2 = new ArrayList<>();
        specificationSearchFilter2.setName("Specification");
        specificationSearchFilter2.setComponentId("component.04");
        specificationSearchFilter2.setState(specificationStates2);

        List<SpecificationEntity> specificationEntities2 = specificationService.searchSpecifications(specificationSearchFilter2, contextInfo);
        assertEquals(3, specificationEntities2.size());

        // Test case 3: Search specification by name and state.
        SpecificationCriteriaSearchFilter specificationSearchFilter3 = new SpecificationCriteriaSearchFilter();

        List<String> specificationStates3 = new ArrayList<>();
        specificationStates3.add("specification.status.active");

        specificationSearchFilter3.setName("Specification");
        specificationSearchFilter3.setState(specificationStates3);

        List<SpecificationEntity> specificationEntities3 = specificationService.searchSpecifications(specificationSearchFilter3, contextInfo);
        assertEquals(2, specificationEntities3.size());


        // Test case 4: Search specification by state and component.
        SpecificationCriteriaSearchFilter specificationSearchFilter4 = new SpecificationCriteriaSearchFilter();

        List<String> specificationStates4 = new ArrayList<>();
        specificationStates4.add("specification.status.inactive");
        specificationSearchFilter4.setComponentId("component.04");
        specificationSearchFilter4.setState(specificationStates4);

        List<SpecificationEntity> specificationEntities4 = specificationService.searchSpecifications(specificationSearchFilter4, contextInfo);
        assertEquals(1, specificationEntities4.size());


        // Test case 5: Search specification by name.
        SpecificationCriteriaSearchFilter specificationSearchFilter5 = new SpecificationCriteriaSearchFilter();

        List<String> specificationStates5 = new ArrayList<>();
        specificationSearchFilter5.setName("Specification");
        specificationSearchFilter5.setState(specificationStates5);

        List<SpecificationEntity> specificationEntities5 = specificationService.searchSpecifications(specificationSearchFilter5, contextInfo);

        assertEquals(4, specificationEntities5.size());


        // Test case 6: Search specification by component.
        SpecificationCriteriaSearchFilter specificationSearchFilter6 = new SpecificationCriteriaSearchFilter();

        List<String> specificationStates6 = new ArrayList<>();
        specificationSearchFilter6.setComponentId("component.04");
        specificationSearchFilter6.setState(specificationStates6);

        List<SpecificationEntity> specificationEntities6 = specificationService.searchSpecifications(specificationSearchFilter6, contextInfo);
        assertEquals(4, specificationEntities6.size());


        // Test case 7: Search specification by state

        SpecificationCriteriaSearchFilter specificationSearchFilter7 = new SpecificationCriteriaSearchFilter();

        List<String> specificationStates7 = new ArrayList<>();
        specificationStates7.add("specification.status.draft");

        specificationSearchFilter7.setState(specificationStates7);

        List<SpecificationEntity> specificationEntities7 = specificationService.searchSpecifications(specificationSearchFilter7, contextInfo);

        assertEquals(3, specificationEntities7.size());

        // Test case 8: Search specification by manual

        SpecificationCriteriaSearchFilter specificationSearchFilter8 = new SpecificationCriteriaSearchFilter();
        specificationSearchFilter8.setManual(false);
        List<SpecificationEntity> specificationEntities8 = specificationService.searchSpecifications(specificationSearchFilter8, contextInfo);
        assertEquals(1, specificationEntities8.size());

        // Test case 9: Search specification by id

        SpecificationCriteriaSearchFilter specificationSearchFilter9 = new SpecificationCriteriaSearchFilter("specification.01");
        List<SpecificationEntity> specificationEntities9 = specificationService.searchSpecifications(specificationSearchFilter9, contextInfo);
        assertEquals(1, specificationEntities9.size());

    }

    @Test
    @Transactional
    public void testValidateSpecification() throws InvalidParameterException, DoesNotExistException, OperationFailedException {
        SpecificationEntity specificationEntity = specificationService.getSpecificationById("specification.01", contextInfo);
        String validationTypeKey = "update.validation.test";
        assertThrows(InvalidParameterException.class, () -> {
            specificationService.validateSpecification(validationTypeKey, specificationEntity, contextInfo);
        });

        //when validation type key is null
        assertThrows(InvalidParameterException.class, () -> {
            specificationService.validateSpecification(null, specificationEntity, contextInfo);
        });

        //when specification entity is null
        Assert.assertThrows(InvalidParameterException.class, () -> {
            specificationService.validateSpecification("specificatation.create", null, contextInfo);
        });
    }

    @Test
    @Transactional
    public void testCreateSpecificationWithTestcase() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        SpecificationEntity specificationEntity = specificationService.getSpecificationById("specification.01", contextInfo);

        //create testcase
        TestcaseEntity testcase = testcaseService.getTestcaseById("testcase.222", contextInfo);
        Set<TestcaseEntity> testcaseSet = new HashSet<>();
        testcaseSet.add(testcase);
        specificationEntity.setTestcases(testcaseSet);

        SpecificationEntity updatedSpecification = specificationService.updateSpecification(specificationEntity, contextInfo);
        assertEquals(specificationEntity.getId(), updatedSpecification.getId());
    }



    @Test
    void changeRank() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {

        Assert.assertThrows(DataValidationErrorException.class, () -> {
            specificationService.changeRank("specification.01", null, contextInfo);
        });

        SpecificationEntity specificationEntity = this.specificationService.changeRank("specification.11", 1, contextInfo);
        assertEquals(1, specificationEntity.getRank());

        SpecificationEntity specificationEntity2 = this.specificationService.changeRank("specification.06", 1, contextInfo);
        assertEquals(1, specificationEntity2.getRank());
    }

}
