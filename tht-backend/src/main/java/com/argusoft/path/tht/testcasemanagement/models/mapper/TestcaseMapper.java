package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseVariableInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to covert DTO <-> Entity for the Testcase.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface TestcaseMapper extends ModelDtoMapper<TestcaseEntity, TestcaseInfo> {

    TestcaseMapper INSTANCE = Mappers.getMapper(TestcaseMapper.class);

    @Mapping(source = "specification", target = "specificationId")
    @Mapping(source = "testcaseEntity", target = "testcaseVariables")
    TestcaseInfo modelToDto(TestcaseEntity testcaseEntity);

    @InheritInverseConfiguration
    @Mapping(source = "testcaseInfo", target = "testcaseVariables")
    TestcaseEntity dtoToModel(TestcaseInfo testcaseInfo);

    List<TestcaseInfo> modelToDto(List<TestcaseEntity> testcaseEntities);

    List<TestcaseEntity> dtoToModel(List<TestcaseInfo> testcaseInfos);

    default String setToSpecificationId(SpecificationEntity specificationEntity) {
        if (specificationEntity == null) {
            return null;
        }
        return specificationEntity.getId();
    }

    default SpecificationEntity setToSpecification(String specificationId) {
        if (!StringUtils.hasLength(specificationId)) {
            return null;
        }
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId(specificationId);
        return specificationEntity;
    }

    default List<TestcaseVariableInfo> setToTestcaseVariables(TestcaseEntity testcaseEntity) {
        if (testcaseEntity.getTestcaseVariables() == null) {
            return null;
        }
        return testcaseEntity.getTestcaseVariables().stream()
                .map(testcaseVariable -> {
                    return new TestcaseVariableInfo(testcaseVariable.getKey(),
                            testcaseVariable.getDefaultValue(),
                            testcaseVariable.getRoleId(),
                            testcaseVariable.getTestcaseId(),
                            testcaseVariable.getState(),
                            testcaseVariable.getId(),
                            testcaseVariable.getMeta());
                })
                .collect(Collectors.toList());
    }

    default List<TestcaseVariableEntity> setToTestcaseVariables(TestcaseInfo testcaseInfo) {
        if (testcaseInfo.getTestcaseVariables() == null) {
            return null;
        }
        return testcaseInfo.getTestcaseVariables().stream()
                .map(testcaseVariable -> {
                    TestcaseVariableEntity testcaseVariableEntity = new TestcaseVariableEntity();
                    testcaseVariableEntity.setId(testcaseVariable.getId());
                    testcaseVariableEntity.setTestcaseId(testcaseInfo.getId());
                    testcaseVariableEntity.setKey(testcaseVariable.getKey());
                    testcaseVariableEntity.setRoleId(testcaseVariable.getRoleId());
                    testcaseVariableEntity.setTestcaseId(testcaseVariable.getTestcaseId());
                    testcaseVariableEntity.setState(testcaseVariable.getState());
                    testcaseVariableEntity.setDefaultValue(testcaseVariable.getDefaultValue());

                    return testcaseVariableEntity;
                })
                .collect(Collectors.toList());
    }


}
