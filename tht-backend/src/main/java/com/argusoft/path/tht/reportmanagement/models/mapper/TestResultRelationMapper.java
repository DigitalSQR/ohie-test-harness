package com.argusoft.path.tht.reportmanagement.models.mapper;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.reportmanagement.models.dto.TestResultRelationInfo;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestResultRelationMapper {

    TestResultRelationMapper INSTANCE = Mappers.getMapper(TestResultRelationMapper.class);

    @Mapping(source = "version", target = "versionOfRefEntity")
    @Mapping(source = "id", target = "testcaseResultEntityId")
    TestResultRelationInfo modelToDto(TestcaseResultEntity testcaseResultEntity);

    @InheritInverseConfiguration
    TestcaseResultEntity dtoToModel(TestResultRelationInfo testResultRelationInfo);

    List<TestResultRelationInfo> modelToDto(List<TestResultRelationEntity> testcaseResultEntities);

    List<TestResultRelationEntity> dtoToModel(List<TestResultRelationInfo> testResultRelationInfos);

    default Page<TestResultRelationInfo> pageEntityToDto(Page<TestResultRelationEntity> page) {
        List<TestResultRelationEntity> testResultRelationEntities = page.getContent();
        List<TestResultRelationInfo> testResultRelationInfos = this.modelToDto(testResultRelationEntities);
        return new PageImpl<>(testResultRelationInfos, page.getPageable(), page.getTotalElements());
    }

}
