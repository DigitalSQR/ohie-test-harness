package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComponentMapper {

    ComponentMapper INSTANCE = Mappers.getMapper(ComponentMapper.class);

    @InheritInverseConfiguration
    ComponentInfo modelToDto(ComponentEntity componentEntity);

    ComponentEntity dtoToModel(ComponentInfo componentInfo);

    @InheritInverseConfiguration
    List<ComponentInfo> modelToDto(List<ComponentEntity> componentEntities);

    List<ComponentEntity> dtoToModel(List<ComponentInfo> componentInfos);

    // Custom mapping method for Page<ComponentEntity> to Page<ComponentInfo>
    default Page<ComponentInfo> pageEntityToDto(Page<ComponentEntity> page) {
        List<ComponentEntity> componentEntities = page.getContent();
        List<ComponentInfo> componentDtoList = this.modelToDto(componentEntities);
        return new PageImpl<>(componentDtoList, page.getPageable(), page.getTotalElements());
    }
}
