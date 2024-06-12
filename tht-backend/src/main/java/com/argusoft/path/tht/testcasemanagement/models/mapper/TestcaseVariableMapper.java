package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseVariableInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Mapper to covert DTO <-> Entity for the TestcaseVariables.
 *
 * @author Aastha
 */
@Mapper(componentModel = "spring")
public interface TestcaseVariableMapper extends ModelDtoMapper<TestcaseVariableEntity, TestcaseVariableInfo> {

    TestcaseVariableMapper INSTANCE = Mappers.getMapper(TestcaseVariableMapper.class);

    @Mapping(source = "testcase", target = "testcaseId", qualifiedByName = "setToTestcaseId")
    TestcaseVariableInfo modelToDto(TestcaseVariableEntity TestcaseVariableEntity);

    @InheritInverseConfiguration
    @Mapping(source = "testcaseId", target = "testcase", qualifiedByName = "setToTestcase")
    TestcaseVariableEntity dtoToModel(TestcaseVariableInfo testcaseVariablesInfo);

    List<TestcaseVariableInfo> modelToDto(List<TestcaseVariableEntity> testcaseVariablesEntities);

    List<TestcaseVariableEntity> dtoToModel(List<TestcaseVariableInfo> testcaseVariablesInfos);

    @Named("setToTestcaseId")
    default String setToTestcaseId(TestcaseEntity testcaseEntity) {
        if (testcaseEntity == null) return null;
        return testcaseEntity.getId();
    }

    @Named("setToTestcase")
    default TestcaseEntity setToTestcase(String testcaseId) {
        if (!StringUtils.hasLength(testcaseId)) {
            return null;
        }
        TestcaseEntity testcaseEntity = new TestcaseEntity();
        testcaseEntity.setId(testcaseId);
        return testcaseEntity;
    }

}
