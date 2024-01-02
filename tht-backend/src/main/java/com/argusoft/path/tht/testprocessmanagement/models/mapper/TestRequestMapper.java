package com.argusoft.path.tht.testprocessmanagement.models.mapper;

import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestUrlInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper to covert DTO <-> Entity for the TestRequest.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface TestRequestMapper {

    TestRequestMapper INSTANCE = Mappers.getMapper(TestRequestMapper.class);

    @Mapping(source = "approver", target = "approverId", qualifiedByName = "setToApproverId")
    @Mapping(source = "assessee", target = "assesseeId", qualifiedByName = "setToAssesseeId")
    @Mapping(source = "testRequestEntity", target = "testRequestUrls")
    TestRequestInfo modelToDto(TestRequestEntity testRequestEntity);

    @InheritInverseConfiguration
    @Mapping(source = "approverId", target = "approver", qualifiedByName = "setToApprover")
    @Mapping(source = "assesseeId", target = "assessee", qualifiedByName = "setToAssessee")
    @Mapping(source = "testRequestInfo", target = "testRequestUrls")
    TestRequestEntity dtoToModel(TestRequestInfo testRequestInfo);

    List<TestRequestInfo> modelToDto(List<TestRequestEntity> testRequestEntities);

    List<TestRequestEntity> dtoToModel(List<TestRequestInfo> testRequestInfos);

    // Custom mapping method for Page<TestRequestEntity> to Page<TestRequestInfo>
    default Page<TestRequestInfo> pageEntityToDto(Page<TestRequestEntity> page) {
        List<TestRequestEntity> testRequestEntities = page.getContent();
        List<TestRequestInfo> testRequestDtoList = this.modelToDto(testRequestEntities);
        return new PageImpl<>(testRequestDtoList, page.getPageable(), page.getTotalElements());
    }

    default Set<TestRequestUrlInfo> setToTestRequestUrls(TestRequestEntity testRequestEntity) {
        return testRequestEntity.getTestRequestUrls().stream()
                .map(testRequestUrl -> {
                    return new TestRequestUrlInfo(testRequestUrl.getComponent().getId(),
                            testRequestUrl.getBaseUrl(),
                            testRequestUrl.getUsername(),
                            testRequestUrl.getPassword());
                })
                .collect(Collectors.toSet());
    }

    default Set<TestRequestUrlEntity> setToTestRequestUrls(TestRequestInfo testRequestInfo) {
        return testRequestInfo.getTestRequestUrls().stream()
                .map(testRequestUrl -> {
                    TestRequestUrlEntity testRequestUrlEntity = new TestRequestUrlEntity();
                    testRequestUrlEntity.setTestRequestId(testRequestInfo.getId());
                    testRequestUrlEntity.setBaseUrl(testRequestUrl.getBaseUrl());
                    testRequestUrlEntity.setUsername(testRequestUrl.getUsername());
                    testRequestUrlEntity.setPassword(testRequestUrl.getPassword());

                    ComponentEntity componentEntity = new ComponentEntity();
                    componentEntity.setId(testRequestUrl.getComponentId());
                    testRequestUrlEntity.setComponent(componentEntity);

                    return testRequestUrlEntity;
                })
                .collect(Collectors.toSet());
    }

    @Named("setToApproverId")
    default String setToApproverId(UserEntity approver) {
        return approver.getId();
    }

    @Named("setToApprover")
    default UserEntity setToApprover(String approverId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(approverId);
        return userEntity;
    }

    @Named("setToAssesseeId")
    default String setToAssesseeId(UserEntity assessee) {
        return assessee.getId();
    }

    @Named("setToAssessee")
    default UserEntity setToAssessee(String assesseeId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(assesseeId);
        return userEntity;
    }
}
