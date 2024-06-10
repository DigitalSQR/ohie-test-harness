package com.argusoft.path.tht.testprocessmanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestValueInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestValueEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Mapper to covert DTO <-> Entity for the TestRequestValue.
 *
 * @author Aastha
 */
@Mapper(componentModel = "spring")
public interface TestRequestValueMapper extends ModelDtoMapper<TestRequestValueEntity, TestRequestValueInfo> {

    TestRequestValueMapper INSTANCE = Mappers.getMapper(TestRequestValueMapper.class);

    @Mapping(source = "testRequest", target = "testRequestId", qualifiedByName = "setToTestRequestId")
    TestRequestValueInfo modelToDto(TestRequestValueEntity testRequestValueEntity);

    @InheritInverseConfiguration
    @Mapping(source = "testRequestId", target = "testRequest", qualifiedByName = "setToTestRequest")
    TestRequestValueEntity dtoToModel(TestRequestValueInfo testRequestValueInfo);

    List<TestRequestValueInfo> modelToDto(List<TestRequestValueEntity> testRequestValueEntities);

    List<TestRequestValueEntity> dtoToModel(List<TestRequestValueInfo> testRequestValueInfos);


    @Named("setToTestRequestId")
    default String setToTestRequestId(TestRequestEntity testRequestEntity) {
        if (testRequestEntity == null) return null;
        return testRequestEntity.getId();
    }

    @Named("setToTestRequest")
    default TestRequestEntity setToTestRequest(String testRequestId) {
        if (!StringUtils.hasLength(testRequestId)) {
            return null;
        }
        TestRequestEntity testRequestEntity = new TestRequestEntity();
        testRequestEntity.setId(testRequestId);
        return testRequestEntity;
    }
}
