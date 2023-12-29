package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseOptionInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * Mapper to covert DTO <-> Entity for the TestcaseOption.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface TestcaseOptionMapper {

    TestcaseOptionMapper INSTANCE = Mappers.getMapper(TestcaseOptionMapper.class);

    @InheritInverseConfiguration
    @Mapping(source = "testcase", target = "testcaseId")
    TestcaseOptionInfo modelToDto(TestcaseOptionEntity testcaseOptionEntity);

    @Mapping(source = "testcaseId", target = "testcase")
    TestcaseOptionEntity dtoToModel(TestcaseOptionInfo testcaseOptionInfo);

    @InheritInverseConfiguration
    List<TestcaseOptionInfo> modelToDto(List<TestcaseOptionEntity> testcaseOptionEntities);

    List<TestcaseOptionEntity> dtoToModel(List<TestcaseOptionInfo> testcaseOptionInfos);

    // Custom mapping method for Page<TestcaseOptionEntity> to Page<TestcaseOptionInfo>
    default Page<TestcaseOptionInfo> pageEntityToDto(Page<TestcaseOptionEntity> page) {
        List<TestcaseOptionEntity> testcaseOptionEntities = page.getContent();
        List<TestcaseOptionInfo> testcaseOptionDtoList = this.modelToDto(testcaseOptionEntities);
        return new PageImpl<>(testcaseOptionDtoList, page.getPageable(), page.getTotalElements());
    }

    default String setToTestcaseId(TestcaseEntity testcaseEntity) {
        return testcaseEntity.getId();
    }

    default TestcaseEntity setToTestcase(String testcaseId) {
        TestcaseEntity testcaseEntity = new TestcaseEntity();
        testcaseEntity.setId(testcaseId);
        return testcaseEntity;
    }
}
