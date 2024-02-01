package com.argusoft.path.tht.reportmanagement.models.mapper;

import com.argusoft.path.tht.reportmanagement.models.dto.TestResultOptionInfo;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultOptionEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper to covert DTO <-> Entity for the TestResultOption.
 *
 * @author Aastha
 */
@Mapper(componentModel = "spring")
public interface TestResultOptionMapper {

    TestResultOptionMapper INSTANCE = Mappers.getMapper(TestResultOptionMapper.class);

    @Mapping(source = "testcaseResult", target = "testcaseResultId")
    @Mapping(source = "testcaseOption", target = "testcaseOptionId")
    TestResultOptionInfo modelToDto(TestResultOptionEntity testResultOptionEntity);

    @InheritInverseConfiguration
    TestResultOptionEntity dtoToModel(TestResultOptionInfo testResultOptionInfo);

    List<TestResultOptionInfo> modelToDto(List<TestResultOptionEntity> testResultOptionEntities);

    List<TestResultOptionEntity> dtoToModel(List<TestResultOptionInfo> testResultOptionInfo);

    // Custom mapping method for Page<TestResultOptionEntity> to Page<TestResultOptionInfo>
    default Page<TestResultOptionInfo> pageEntityToDto(Page<TestResultOptionEntity> page) {
        List<TestResultOptionEntity> testResultOptionEntities = page.getContent();
        List<TestResultOptionInfo> testResultOptionDtoList = this.modelToDto(testResultOptionEntities);
        return new PageImpl<>(testResultOptionDtoList, page.getPageable(), page.getTotalElements());
    }


    default Set<TestResultOptionInfo> setToTestResultOptions(TestcaseResultEntity testcaseResultEntity) {
        return testcaseResultEntity.getTestResultOption().stream()
                .map(testResultOption -> {
                    return new TestResultOptionInfo(testResultOption.getTestcaseOption(),
                            testResultOption.getTestcaseResult(),
                            testResultOption.getVersion(),
                            testResultOption.getSelected());
                })
                .collect(Collectors.toSet());
    }

    default Set<TestResultOptionEntity> setToTestResultOptions(TestcaseResultInfo testcaseResultInfo) {
        return testcaseResultInfo.getTestResultOptions().stream()
                .map(testResultOption -> {
                    TestResultOptionEntity testResultOptionEntity = new TestResultOptionEntity();
                    testResultOptionEntity.setTestcaseOption(testResultOption.getTestcaseOptionId());
                    testResultOptionEntity.setTestcaseResult(testcaseResultInfo.getId());
                    testResultOptionEntity.setVersion(testResultOption.getVersion());
                    testResultOptionEntity.setSelected(testResultOption.getSelected());

                    return testResultOptionEntity;
                })
                .collect(Collectors.toSet());
    }

    default String setToTestcaseOptionId(TestcaseOptionEntity testcaseOptionEntity) {
        if (testcaseOptionEntity == null) return null;
        return testcaseOptionEntity.getId();
    }

    default TestcaseOptionEntity setToTestcaseOption(String testcaseOptionId) {
        if (StringUtils.isEmpty(testcaseOptionId)) {
            return null;
        }
        TestcaseOptionEntity testcaseOptionEntity = new TestcaseOptionEntity();
        testcaseOptionEntity.setId(testcaseOptionId);
        return testcaseOptionEntity;
    }

    default String setToTestcaseResultId(TestcaseResultEntity testcaseResultEntity) {
        if (testcaseResultEntity == null) return null;
        return testcaseResultEntity.getId();
    }

    default TestcaseResultEntity setToTestcaseResult(String testcaseResultId) {
        if (StringUtils.isEmpty(testcaseResultId)) {
            return null;
        }
        TestcaseResultEntity testcaseResultEntity = new TestcaseResultEntity();
        testcaseResultEntity.setId(testcaseResultId);
        return testcaseResultEntity;
    }
}
