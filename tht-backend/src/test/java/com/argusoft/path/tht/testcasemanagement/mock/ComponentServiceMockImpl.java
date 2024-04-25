package com.argusoft.path.tht.testcasemanagement.mock;

import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.ComponentMapper;
import com.argusoft.path.tht.testcasemanagement.repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ComponentServiceMockImpl {

    @Autowired
    ComponentMapper componentMapper;
    @Autowired
    private ComponentRepository componentRepository;

    public void init() {
        createComponent("component.02", "Component 2", "Component 2", 1, "component.status.active");
        createComponent("component.04", "Component 4", "Component 4", 0, "component.status.active");
        createComponent("component.05", "Compo-Test 5", "Compo-Test 5", 1, "component.status.active");
        createComponent("component.06", "Compo-Test 6", "Compo-Test 6", 1, "component.status.inactive");
        createComponent("component.07", "Component 7", "Component 7", 12, "component.status.draft");
        createComponent("component.08", "Component 8", "Component 8", 13, "component.status.active");
    }

    public ComponentInfo createComponent(String id, String name, String description, int rank, String state) {
        ComponentEntity componentEntity = new ComponentEntity();
        componentEntity.setId(id);
        componentEntity.setName(name);
        componentEntity.setDescription(description);
        componentEntity.setState(state);
        componentEntity.setRank(rank);
        componentEntity.setCreatedBy("ivasiwala");
        componentEntity.setUpdatedBy("ivasiwala");
        componentEntity.setCreatedAt(new Date());
        componentEntity.setUpdatedAt(new Date());

        componentRepository.save(componentEntity);
        return componentMapper.modelToDto(componentEntity);
    }

    public void clear() {
        componentRepository.deleteAll();
        componentRepository.flush();
    }

}
