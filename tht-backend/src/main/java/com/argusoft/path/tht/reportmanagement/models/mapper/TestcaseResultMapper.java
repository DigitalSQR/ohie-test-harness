package com.argusoft.path.tht.reportmanagement.models.mapper;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestcaseResultMapper {

    TestcaseResultMapper INSTANCE = Mappers.getMapper(TestcaseResultMapper.class);

    @InheritInverseConfiguration
    TestcaseResultInfo modelToDto(TestcaseResultEntity testcaseResultEntity);

    TestcaseResultEntity dtoToModel(TestcaseResultInfo testcaseResultInfo);

    @InheritInverseConfiguration
    List<TestcaseResultInfo> modelToDto(List<TestcaseResultEntity> testcaseResultEntities);

    List<TestcaseResultEntity> dtoToModel(List<TestcaseResultInfo> testcaseResultInfos);

    // Custom mapping method for Page<TestcaseResultEntity> to Page<TestcaseResultInfo>
    default Page<TestcaseResultInfo> pageEntityToDto(Page<TestcaseResultEntity> page) {
        List<TestcaseResultEntity> testcaseResultEntities = page.getContent();
        List<TestcaseResultInfo> testcaseResultDtoList = this.modelToDto(testcaseResultEntities);
        return new PageImpl<>(testcaseResultDtoList, page.getPageable(), page.getTotalElements());
    }

    default String setToTesterId(UserEntity userEntity) {
        return userEntity.getId();
    }

    default UserEntity setToTester(String testerId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(testerId);
        return userEntity;
    }
}