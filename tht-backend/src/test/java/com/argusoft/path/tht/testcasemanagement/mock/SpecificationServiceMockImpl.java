package com.argusoft.path.tht.testcasemanagement.mock;

import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.repository.ComponentRepository;
import com.argusoft.path.tht.testcasemanagement.repository.SpecificationRepository;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SpecificationServiceMockImpl {

    @Autowired
    private SpecificationRepository specificationRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentServiceMockImpl componentServiceMockImpl;

    public void init() {

        String userId = "ivasiwala";
        componentServiceMockImpl.init();
        createSpecification("specification.01", "Specification 1", "Specification 1", 1, "specification.status.active", "component.02", true, true, userId);
        createSpecification("specification.06", "Specification 6", "Specification 6", 0, "specification.status.active", "component.04", true, true, userId);
        createSpecification("specification.07", "Specification 7", "Specification 7", 1, "specification.status.draft", "component.04", true, true, userId);
        createSpecification("specification.08", "Specification 8", "Specification 8", 1, "specification.status.inactive", "component.04", true, true, userId);
        createSpecification("specification.09", "Test 9", "Specification 9", 1, "specification.status.active", "component.05", true, true, userId);
        createSpecification("specification.10", "Spec 10", "Spec 10", 1, "specification.status.draft", "component.04", true, true, userId);
        createSpecification("specification.11", "Spec 11", "Spec 11", 12, "specification.status.draft", "component.05", true, true, userId);
    }

    public SpecificationEntity createSpecification(String specificationId, String name, String description, int rank, String state, String componentId, Boolean functional, Boolean required, String userId) {
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId(specificationId);
        specificationEntity.setName(name);
        specificationEntity.setDescription(description);
        specificationEntity.setState(state);
        specificationEntity.setRank(rank);
        specificationEntity.setRequired(required);
        specificationEntity.setFunctional(functional);
        specificationEntity.setComponent(componentRepository.findById(componentId).get());
        specificationEntity.setCreatedBy(userId);
        specificationEntity.setUpdatedBy(userId);
        specificationEntity.setCreatedAt(new Date());
        specificationEntity.setUpdatedAt(new Date());
        return specificationRepository.save(specificationEntity);
    }

    public void clear() {
        specificationRepository.deleteAll();
        specificationRepository.flush();
        componentServiceMockImpl.clear();
    }
}
