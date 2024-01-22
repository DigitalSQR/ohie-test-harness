package com.argusoft.path.tht.reportmanagement.models.mapper;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * Mapper to covert DTO <-> Entity for the TestcaseResult.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface TestcaseResultMapper {

    TestcaseResultMapper INSTANCE = Mappers.getMapper(TestcaseResultMapper.class);

    @Mapping(source = "tester.id", target = "testerId")
    @Mapping(source = "testcaseOption.id", target = "testcaseOptionId")
    @Mapping(source = "parentTestcaseResult.id", target = "parentTestcaseResultId")
    TestcaseResultInfo modelToDto(TestcaseResultEntity testcaseResultEntity);

    @InheritInverseConfiguration
    TestcaseResultEntity dtoToModel(TestcaseResultInfo testcaseResultInfo);

    List<TestcaseResultInfo> modelToDto(List<TestcaseResultEntity> testcaseResultEntities);

    List<TestcaseResultEntity> dtoToModel(List<TestcaseResultInfo> testcaseResultInfos);

    // Custom mapping method for Page<TestcaseResultEntity> to Page<TestcaseResultInfo>
    default Page<TestcaseResultInfo> pageEntityToDto(Page<TestcaseResultEntity> page) {
        List<TestcaseResultEntity> testcaseResultEntities = page.getContent();
        List<TestcaseResultInfo> testcaseResultDtoList = this.modelToDto(testcaseResultEntities);
        return new PageImpl<>(testcaseResultDtoList, page.getPageable(), page.getTotalElements());
    }

}
