package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestcaseMapper {

    TestcaseMapper INSTANCE = Mappers.getMapper(TestcaseMapper.class);

    @InheritInverseConfiguration
    TestcaseInfo modelToDto(TestcaseEntity testcaseEntity);

    TestcaseEntity dtoToModel(TestcaseInfo testcaseInfo);

    @InheritInverseConfiguration
    List<TestcaseInfo> modelToDto(List<TestcaseEntity> testcaseEntities);

    List<TestcaseEntity> dtoToModel(List<TestcaseInfo> testcaseInfos);

    // Custom mapping method for Page<TestcaseEntity> to Page<TestcaseInfo>
    default Page<TestcaseInfo> pageEntityToDto(Page<TestcaseEntity> page) {
        List<TestcaseEntity> testcaseEntities = page.getContent();
        List<TestcaseInfo> testcaseDtoList = this.modelToDto(testcaseEntities);
        return new PageImpl<>(testcaseDtoList, page.getPageable(), page.getTotalElements());
    }
}
