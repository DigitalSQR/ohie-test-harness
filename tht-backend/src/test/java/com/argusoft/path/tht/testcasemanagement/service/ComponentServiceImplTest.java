package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.mock.ComponentServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ComponentServiceImplTest extends TestingHarnessToolTestConfiguration {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentServiceMockImpl componentServiceMockImpl;

    private ContextInfo contextInfo;

    @BeforeEach
    @Override
    public void init() {
        // Set context info
        contextInfo = new ContextInfo();
        contextInfo.setCurrentDate(new Date());
        componentServiceMockImpl.init();
    }

    @AfterEach
    void after() {
        componentServiceMockImpl.clear();
    }

    @Test
    public void testCreateComponent() throws InvalidParameterException, DataValidationErrorException, OperationFailedException {


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
    public void testGetComponentById() throws InvalidParameterException, DoesNotExistException {

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

//    @Test
//    public void testGetComponents() throws InvalidParameterException, DoesNotExistException {
//
//        // Test case 1: Passing pageable as null
//        assertThrows(InvalidParameterException.class, () -> {
//            componentService.getComponents(null, contextInfo);
//        });
//
//        // Test case 2: Passing pageable
//        Pageable pageable = PageRequest.of(0, 3);
//
//        Page<ComponentEntity> components = componentService.getComponents(pageable, contextInfo);
//        assertEquals(2, components.getTotalPages());
//        assertEquals(3, components.getNumberOfElements());
//        assertEquals(5, components.getTotalElements());
//    }

    @Test
    public void testSearchComponent() throws InvalidParameterException, OperationFailedException {

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

}
