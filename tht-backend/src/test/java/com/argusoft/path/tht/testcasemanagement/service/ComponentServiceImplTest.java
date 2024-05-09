package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.mock.ComponentServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.mock.SpecificationServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.mock.TestcaseServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ComponentServiceImplTest extends TestingHarnessToolTestConfiguration {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentServiceMockImpl componentServiceMockImpl;

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private SpecificationServiceMockImpl specificationServiceMock;

    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private TestcaseServiceMockImpl testcaseServiceMock;

    private ContextInfo contextInfo;

    @BeforeEach
    @Override
    public void init() {
        // Set context info
        contextInfo = new ContextInfo();
        contextInfo.setCurrentDate(new Date());
        testcaseServiceMock.init();
    }

    @AfterEach
    void after() {
        testcaseServiceMock.clear();
    }

    @Test
    void testCreateComponent() throws InvalidParameterException, DataValidationErrorException, OperationFailedException {


        // Test 1 :  With correct data
        ComponentEntity component1 = new ComponentEntity();
        component1.setId("component.01");
        component1.setName("Component 1");
        component1.setDescription("Component 1");
        component1.setRank(1);

        ComponentEntity resultComponent1 = componentService.createComponent(component1, contextInfo);
        assertEquals("component.01", resultComponent1.getId());

        // Test 2 : With duplicate data
        assertThrows(DataValidationErrorException.class, () -> {
            componentService.createComponent(component1, contextInfo);
        });

        // Test 3: With empty id
        ComponentEntity component2 = new ComponentEntity();
        component2.setName("Component 196");
        component2.setDescription("Component 196");
        component2.setRank(1);

        ComponentEntity resultComponent2 = componentService.createComponent(component2, contextInfo);
        assertEquals("Component 196", resultComponent2.getName());

        //Test 4: with null entity
        ComponentEntity component4 = null;
        assertThrows(InvalidParameterException.class, () -> {
            componentService.createComponent(null, contextInfo);
        });

        // Test case 5 : Create a new specification to validate foreign key
        ComponentEntity componentEntity5 = new ComponentEntity();
        componentEntity5.setName("component 111");
        componentEntity5.setDescription("component 111");
        componentEntity5.setRank(1);

        Set<SpecificationEntity> specificationEntitySet = new HashSet<>();
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId("specification.02");
        specificationEntitySet.add(specificationEntity);
        componentEntity5.setSpecifications(specificationEntitySet);

        Assertions.assertThrows(DataValidationErrorException.class, () -> {
            componentService.createComponent(componentEntity5, contextInfo);
        });

    }


    @Test
    @Transactional
    public void testUpdateComponent() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        //  Test case 1 : Update the component data
        ComponentEntity component2 = componentService.getComponentById("component.02", contextInfo);
        //  Before Update
        assertEquals("Component 2", component2.getName());
        assertEquals("Component 2", component2.getDescription());
        assertEquals(Integer.valueOf(1), component2.getRank());

        component2.setRank(3);
        component2.setName("Updated component name");
        component2.setDescription("Updated component description");

        ComponentEntity updatedComponent = componentService.updateComponent(component2, contextInfo);

        // After update
        assertEquals("Updated component name", updatedComponent.getName());
        assertEquals(updatedComponent.getDescription(), "Updated component description");
        assertEquals(Integer.valueOf(3), updatedComponent.getRank());


        // Test case 2 : Given component id does not exist
        ComponentEntity component3 = new ComponentEntity();
        component3.setId("component.03");
        assertThrows(DataValidationErrorException.class, () -> {
            componentService.updateComponent(component3, contextInfo);
        });

        //Test 3: with null entity
        ComponentEntity componentnull = null;
        assertThrows(InvalidParameterException.class, () -> {
            componentService.updateComponent(null, contextInfo);
        });

    }


    @Test
    @Transactional
    public void testUpdateComponentState() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException, VersionMismatchException {

        // Test case 1 : Update the component state

        ComponentEntity component2 = componentService.getComponentById("component.02", contextInfo);

        // Before Update
        assertEquals(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE, component2.getState());

        ComponentEntity updatedComponent = componentService.changeState(component2.getId(), ComponentServiceConstants.COMPONENT_STATUS_INACTIVE, contextInfo);

        // After update
        assertEquals(ComponentServiceConstants.COMPONENT_STATUS_INACTIVE, updatedComponent.getState());


        // Test case 2 : State is invalid

        ComponentEntity component3 = componentService.getComponentById("component.04", contextInfo);

        // Before Update
        assertEquals(ComponentServiceConstants.COMPONENT_STATUS_ACTIVE, component3.getState());

        assertThrows(DataValidationErrorException.class, () -> {
            componentService.changeState(component3.getId(), "component.status.test", contextInfo);
        });

    }


    @Test
    void testGetComponentById() throws InvalidParameterException, DoesNotExistException {

        // Test case 1: Passing component id as null
        assertThrows(InvalidParameterException.class, () -> {
            componentService.getComponentById(null, contextInfo);
        });

        // Test case 2: Component data does not exist with given id
        assertThrows(DoesNotExistException.class, () -> {
            componentService.getComponentById("component.15", contextInfo);
        });

        // Test case 3: Component data exist with given id
        ComponentEntity finalComponent = componentService.getComponentById("component.04", contextInfo);
        assertEquals("component.04", finalComponent.getId());

    }

    @Test
    void testSearchComponent() throws InvalidParameterException, OperationFailedException {

        // Test case 1: Search component by name and state.

        ComponentCriteriaSearchFilter componentSearchFilter1 = new ComponentCriteriaSearchFilter();
        componentSearchFilter1.setName("Test");

        List<String> componentStates1 = new ArrayList<>();
        componentStates1.add("component.status.active");
        componentStates1.add("component.status.draft");

        componentSearchFilter1.setState(componentStates1);

        List<ComponentEntity> componentEntities1 = componentService.searchComponents(componentSearchFilter1, contextInfo);
        assertEquals(1, componentEntities1.size());


        // Test case 2: Search component by name.

        ComponentCriteriaSearchFilter componentSearchFilter2 = new ComponentCriteriaSearchFilter();
        componentSearchFilter2.setName("Test");

        List<String> componentStates2 = new ArrayList<>();

        componentSearchFilter2.setState(componentStates2);

        List<ComponentEntity> componentEntities2 = componentService.searchComponents(componentSearchFilter2, contextInfo);
        assertEquals(2, componentEntities2.size());


        // Test case 3: Search component by state.

        ComponentCriteriaSearchFilter componentSearchFilter3 = new ComponentCriteriaSearchFilter();

        List<String> componentStates3 = new ArrayList<>();
        componentStates3.add("component.status.draft");

        componentSearchFilter3.setState(componentStates3);

        List<ComponentEntity> componentEntities3 = componentService.searchComponents(componentSearchFilter3, contextInfo);
        assertEquals(1, componentEntities3.size());

    }

    @Test
    void changeRank() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {

        assertThrows(DataValidationErrorException.class, () -> {
            componentService.changeRank("component.02", null, contextInfo);
        });

        ComponentEntity componentEntity = this.componentService.changeRank("component.07", 1, contextInfo);
        assertEquals(1, componentEntity.getRank());

        ComponentEntity componentEntity2 = this.componentService.changeRank("component.04", 1, contextInfo);
        assertEquals(1, componentEntity2.getRank());

    }

    @Test
    @Transactional
    void testValidateComponent() throws InvalidParameterException, DoesNotExistException {
        assertThrows(InvalidParameterException.class, () -> {
            componentService.validateComponent("component.create", null, contextInfo);
        });

        ComponentEntity componentEntity = componentService.getComponentById("component.02", contextInfo);
        String validationTypeKey = "update.validation.test";
        Assertions.assertThrows(InvalidParameterException.class, () -> {
            componentService.validateComponent(validationTypeKey, componentEntity, contextInfo);
        });

        //when validation type key is null
        Assertions.assertThrows(InvalidParameterException.class, () -> {
            componentService.validateComponent(null, componentEntity, contextInfo);
        });
    }


    @Test
    @Transactional
    void testValidateTestcaseConfiguration() throws InvalidParameterException, OperationFailedException, DoesNotExistException {
//        SpecificationEntity specification = this.specificationService.getSpecificationById("specification.01", contextInfo);
//        TestcaseEntity testcase = this.testcaseService.getTestcaseById("testcase.222", contextInfo);
        ComponentEntity componentEntity = componentService.getComponentById("component.02", contextInfo);

        //create specification to add in component
        Set<SpecificationEntity> specificationEntitySet = new HashSet<>();
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId("specification.02");
        specificationEntity.setName("Specification 02");
        specificationEntity.setDescription("Specification 02");
        specificationEntity.setState("specification.status.active");
        specificationEntity.setRank(1);
        specificationEntity.setFunctional(true);
        specificationEntity.setRequired(true);
        specificationEntity.setComponent(componentService.getComponentById("component.05", contextInfo));

        //create testcase to add in specification
        Set<TestcaseEntity> testcaseSet = new HashSet<>();
        TestcaseEntity testcaseEntity = new TestcaseEntity();
        testcaseEntity.setId("testcase.01");
        testcaseEntity.setName("Verify inbound/outbound transaction");
        testcaseEntity.setDescription("Testcase client repository functional 2");
        testcaseEntity.setState("testcase.status.active");
        testcaseEntity.setRank(1);
        testcaseEntity.setSpecification(specificationEntity);
        testcaseEntity.setCreatedBy("ivasiwala");
        testcaseEntity.setUpdatedBy("ivasiwala");
        testcaseEntity.setCreatedAt(new Date());
        testcaseEntity.setUpdatedAt(new Date());
        testcaseEntity.setManual(true);
        testcaseSet.add(testcaseEntity);
        specificationEntity.setTestcases(testcaseSet);

        specificationEntitySet.add(specificationEntity);
        componentEntity.setSpecifications(specificationEntitySet);

        List<TestcaseValidationResultInfo> validateComponent = this.componentService.validateTestCaseConfiguration("com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo", "component.02", contextInfo);
        assertEquals(3, validateComponent.size());

        //Testcase 2: component for  wrong ref id
        List<TestcaseValidationResultInfo> validateIncorrectComponentRefId = componentService.validateTestCaseConfiguration("com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo", "no.component", contextInfo);
        assertEquals(1, validateIncorrectComponentRefId.size());

        //Testcase 3: validate for specification for wrong refid
        List<TestcaseValidationResultInfo> validateSpecification = this.componentService.validateTestCaseConfiguration("com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo", "no.specification", contextInfo);
        assertEquals(1, validateSpecification.size());

        //Testcase 4: validate testcase for wrong refid
        List<TestcaseValidationResultInfo> validateTestcase = this.componentService.validateTestCaseConfiguration("com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo", "no.testcase", contextInfo);
        assertEquals(1, validateTestcase.size());

        //Testcase 5: validate wrong refobj uri
        List<TestcaseValidationResultInfo> validateRefobjuri = this.componentService.validateTestCaseConfiguration("", "no.uri", contextInfo);
        assertEquals(8, validateRefobjuri.size());

        //Testcase 6: validate inactive specification
        testcaseEntity.setState("testcase.status.inactive");
        List<TestcaseValidationResultInfo> validateInactiveSpecification = this.componentService.validateTestCaseConfiguration("com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo", "component.02", contextInfo);
        assertEquals(8, validateRefobjuri.size());


    }

}
