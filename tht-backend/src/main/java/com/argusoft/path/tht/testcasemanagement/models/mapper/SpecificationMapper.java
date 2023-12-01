package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecificationMapper {

    SpecificationMapper INSTANCE = Mappers.getMapper(SpecificationMapper.class);

    @InheritInverseConfiguration
    SpecificationInfo modelToDto(SpecificationEntity specificationEntity);

    SpecificationEntity dtoToModel(SpecificationInfo specificationInfo);

    @InheritInverseConfiguration
    List<SpecificationInfo> modelToDto(List<SpecificationEntity> specificationEntities);

    List<SpecificationEntity> dtoToModel(List<SpecificationInfo> specificationInfos);

    // Custom mapping method for Page<SpecificationEntity> to Page<SpecificationInfo>
    default Page<SpecificationInfo> pageEntityToDto(Page<SpecificationEntity> page) {
        List<SpecificationEntity> specificationEntities = page.getContent();
        List<SpecificationInfo> specificationDtoList = this.modelToDto(specificationEntities);
        return new PageImpl<>(specificationDtoList, page.getPageable(), page.getTotalElements());
    }
}
