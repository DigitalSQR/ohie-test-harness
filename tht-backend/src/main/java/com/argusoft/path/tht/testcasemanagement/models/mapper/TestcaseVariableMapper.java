package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseVariableInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper to covert DTO <-> Entity for the TestcaseVariables.
 *
 * @author Aastha
 */
@Mapper(componentModel = "spring")
public interface TestcaseVariableMapper extends ModelDtoMapper<TestcaseVariableEntity, TestcaseVariableInfo> {

    TestcaseVariableMapper INSTANCE = Mappers.getMapper(TestcaseVariableMapper.class);

    TestcaseVariableInfo modelToDto(TestcaseVariableEntity TestcaseVariableEntity);

    @InheritInverseConfiguration
    TestcaseVariableEntity dtoToModel(TestcaseVariableInfo testcaseVariablesInfo);

    List<TestcaseVariableInfo> modelToDto(List<TestcaseVariableEntity> testcaseVariablesEntities);

    List<TestcaseVariableEntity> dtoToModel(List<TestcaseVariableInfo> testcaseVariablesInfos);

}
