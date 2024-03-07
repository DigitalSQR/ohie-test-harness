package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper to covert DTO <-> Entity for the Component.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface ComponentMapper extends ModelDtoMapper<ComponentEntity,ComponentInfo> {

    ComponentMapper INSTANCE = Mappers.getMapper(ComponentMapper.class);

    @Mapping(source = "specifications", target = "specificationIds")
    ComponentInfo modelToDto(ComponentEntity componentEntity);

    @InheritInverseConfiguration
    @Mapping(source = "specificationIds", target = "specifications")
    ComponentEntity dtoToModel(ComponentInfo componentInfo);

    List<ComponentInfo> modelToDto(List<ComponentEntity> componentEntities);

    List<ComponentEntity> dtoToModel(List<ComponentInfo> componentInfos);

    default Set<String> setToSpecificationIds(Set<SpecificationEntity> specificationEntities) {
        if(specificationEntities==null) {return null;}
            return specificationEntities.stream()
                    .map(SpecificationEntity::getId)
                    .collect(Collectors.toSet());
    }

    default Set<SpecificationEntity> setToSpecifications(Set<String> specificationIds) {
        if(specificationIds==null) {return null;}
            return specificationIds.stream()
                    .map(id -> {
                        SpecificationEntity specificationEntity = new SpecificationEntity();
                        specificationEntity.setId(id);
                        return specificationEntity;
                    })
                    .collect(Collectors.toSet());
    }
}
