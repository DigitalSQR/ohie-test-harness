package com.argusoft.path.tht.reportmanagement.models.mapper;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultAttributesInfo;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper to covert DTO <-> Entity for the TestcaseResult.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface TestcaseResultMapper extends ModelDtoMapper<TestcaseResultEntity, TestcaseResultInfo> {

    TestcaseResultMapper INSTANCE = Mappers.getMapper(TestcaseResultMapper.class);

    @Mapping(source = "tester", target = "testerId")
    @Mapping(source = "parentTestcaseResult", target = "parentTestcaseResultId")
    @Mapping(source = "testRequest", target = "testRequestId")
    TestcaseResultInfo modelToDto(TestcaseResultEntity testcaseResultEntity);

    @InheritInverseConfiguration
    TestcaseResultEntity dtoToModel(TestcaseResultInfo testcaseResultInfo);

    List<TestcaseResultInfo> modelToDto(List<TestcaseResultEntity> testcaseResultEntities);

    List<TestcaseResultEntity> dtoToModel(List<TestcaseResultInfo> testcaseResultInfos);

    default Set<TestcaseResultAttributesInfo> setToTestcaseResultAttributes(TestcaseResultEntity testcaseResultEntity) {
        if (testcaseResultEntity.getTestcaseResultAttributesEntities() == null) {
            return null;
        }
        return (Set<TestcaseResultAttributesInfo>) testcaseResultEntity.getTestcaseResultAttributesEntities().stream()
                .map(testcaseResultAttributesEntity -> {
                    return new TestcaseResultAttributesInfo(testcaseResultAttributesEntity.getKey(), testcaseResultAttributesEntity.getValue()
                    );
                }).collect(Collectors.toList());
    }

    default String setToTesterId(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return userEntity.getId();
    }

    default UserEntity setToTester(String testerId) {
        if (!StringUtils.hasLength(testerId)) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(testerId);
        return userEntity;
    }

    default String setToTestcaseOptionId(TestcaseOptionEntity testcaseOptionEntity) {
        if (testcaseOptionEntity == null) {
            return null;
        }
        return testcaseOptionEntity.getId();
    }

    default TestcaseOptionEntity setToTestcaseOption(String testcaseOptionId) {
        if (!StringUtils.hasLength(testcaseOptionId)) {
            return null;
        }
        TestcaseOptionEntity testcaseOptionEntity = new TestcaseOptionEntity();
        testcaseOptionEntity.setId(testcaseOptionId);
        return testcaseOptionEntity;
    }

    default String setToParentTestcaseResultId(TestcaseResultEntity parentTestcaseResult) {
        if (parentTestcaseResult == null) {
            return null;
        }
        return parentTestcaseResult.getId();
    }

    default TestcaseResultEntity setToParentTestcaseResult(String parentTestcaseResultId) {
        if (!StringUtils.hasLength(parentTestcaseResultId)) {
            return null;
        }
        TestcaseResultEntity testcaseOptionEntity = new TestcaseResultEntity();
        testcaseOptionEntity.setId(parentTestcaseResultId);
        return testcaseOptionEntity;
    }

    default String setToTestRequestId(TestRequestEntity testRequestEntity) {
        if (testRequestEntity == null) {
            return null;
        }
        return testRequestEntity.getId();
    }

    default TestRequestEntity setToTestRequest(String testRequestId) {
        if (!StringUtils.hasLength(testRequestId)) {
            return null;
        }
        TestRequestEntity testRequestEntity = new TestRequestEntity();
        testRequestEntity.setId(testRequestId);
        return testRequestEntity;
    }
}
