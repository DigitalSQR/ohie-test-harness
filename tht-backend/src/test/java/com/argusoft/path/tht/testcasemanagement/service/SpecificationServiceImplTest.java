package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.mock.SpecificationServiceMockImpl;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpecificationServiceImplTest extends TestingHarnessToolTestConfiguration {

    @Autowired
    private SpecificationServiceMockImpl specificationServiceMockImpl;

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private ComponentService componentService;

    ContextInfo contextInfo;

    @BeforeEach
    @Override
    public void init() {
        super.init();
        specificationServiceMockImpl.init();
        contextInfo = new ContextInfo();
    }

    @AfterEach
    void after() {
        specificationServiceMockImpl.clear();
    }

    @Test
    public void testCreateSpecification() throws InvalidParameterException, DataValidationErrorException, OperationFailedException, DoesNotExistException {

        // Test case 1 : Create a new specification
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId("specification.123");
        specificationEntity.setName("Specification 123");
        specificationEntity.setDescription("Specification 123");
        specificationEntity.setState("specification.status.active");
        specificationEntity.setRank(1);
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
    public void testGetSpecification() throws InvalidParameterException, DoesNotExistException {

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
    public void testGetSpecifications() throws InvalidParameterException, DoesNotExistException {

        // Test case 1: Passing pageable as null
        assertThrows(InvalidParameterException.class, () -> {
            specificationService.getSpecifications(null, contextInfo);
        });

        // Test case 2: Passing pageable
        Pageable pageable = PageRequest.of(0, 3);

        Page<SpecificationEntity> specifications = specificationService.getSpecifications(pageable, contextInfo);
        assertEquals(3, specifications.getTotalPages());
        // per page
        assertEquals(3, specifications.getNumberOfElements());
        assertEquals(7, specifications.getTotalElements());
    }

    @Test
    public void testSearchSpecification() throws InvalidParameterException, DataValidationErrorException, OperationFailedException {

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

    }

}
