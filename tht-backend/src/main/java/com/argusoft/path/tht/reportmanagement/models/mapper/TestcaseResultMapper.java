package com.argusoft.path.tht.reportmanagement.models.mapper;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Mapper to covert DTO <-> Entity for the TestcaseResult.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface TestcaseResultMapper {

    TestcaseResultMapper INSTANCE = Mappers.getMapper(TestcaseResultMapper.class);

    @Mapping(source = "tester", target = "testerId")
    @Mapping(source = "testcaseOption", target = "testcaseOptionId")
    @Mapping(source = "parentTestcaseResult", target = "parentTestcaseResultId")
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

    default String setToTesterId(UserEntity userEntity) {
        if (userEntity == null) return null;
        return userEntity.getId();
    }

    default UserEntity setToTester(String testerId) {
        if (StringUtils.isEmpty(testerId)) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(testerId);
        return userEntity;
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


    default String setToParentTestcaseResultId(TestcaseResultEntity parentTestcaseResult) {
        if (parentTestcaseResult == null) return null;
        return parentTestcaseResult.getId();
    }

    default TestcaseResultEntity setToParentTestcaseResult(String parentTestcaseResultId) {
        if (StringUtils.isEmpty(parentTestcaseResultId)) {
            return null;
        }
        TestcaseResultEntity testcaseOptionEntity = new TestcaseResultEntity();
        testcaseOptionEntity.setId(parentTestcaseResultId);
        return testcaseOptionEntity;
    }
}
