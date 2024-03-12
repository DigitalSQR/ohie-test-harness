package com.argusoft.path.tht.testprocessmanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper to covert DTO <-> Entity for the TestRequest.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface TestRequestMapper extends ModelDtoMapper<TestRequestEntity,TestRequestInfo> {

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

    default Set<TestRequestUrlInfo> setToTestRequestUrls(TestRequestEntity testRequestEntity) {
        if(testRequestEntity.getTestRequestUrls()==null){ return null;}
            return testRequestEntity.getTestRequestUrls().stream()
                    .map(testRequestUrl -> {
                        return new TestRequestUrlInfo(testRequestUrl.getComponent().getId(),
                                testRequestUrl.getFhirApiBaseUrl(),
                                testRequestUrl.getUsername(),
                                testRequestUrl.getPassword(),
                                testRequestUrl.getFhirVersion(),
                                testRequestUrl.getWebsiteUIBaseUrl());
                    })
                    .collect(Collectors.toSet());
    }

    default Set<TestRequestUrlEntity> setToTestRequestUrls(TestRequestInfo testRequestInfo) {
        if(testRequestInfo.getTestRequestUrls()==null){return null;}
            return testRequestInfo.getTestRequestUrls().stream()
                    .map(testRequestUrl -> {
                        TestRequestUrlEntity testRequestUrlEntity = new TestRequestUrlEntity();
                        testRequestUrlEntity.setTestRequestId(testRequestInfo.getId());
                        testRequestUrlEntity.setFhirApiBaseUrl(testRequestUrl.getFhirApiBaseUrl());
                        testRequestUrlEntity.setUsername(testRequestUrl.getUsername());
                        testRequestUrlEntity.setPassword(testRequestUrl.getPassword());
                        testRequestUrlEntity.setWebsiteUIBaseUrl(testRequestUrl.getWebsiteUIBaseUrl());
                        testRequestUrlEntity.setFhirVersion(testRequestUrl.getFhirVersion());

                        ComponentEntity componentEntity = new ComponentEntity();
                        componentEntity.setId(testRequestUrl.getComponentId());
                        testRequestUrlEntity.setComponent(componentEntity);

                        return testRequestUrlEntity;
                    })
                    .collect(Collectors.toSet());
    }

    @Named("setToApproverId")
    default String setToApproverId(UserEntity approver) {
        if (approver == null) return null;
        return approver.getId();
    }

    @Named("setToApprover")
    default UserEntity setToApprover(String approverId) {
        if (!StringUtils.hasLength(approverId)) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(approverId);
        return userEntity;
    }

    @Named("setToAssesseeId")
    default String setToAssesseeId(UserEntity assessee) {
        if (assessee == null) return null;
        return assessee.getId();
    }

    @Named("setToAssessee")
    default UserEntity setToAssessee(String assesseeId) {
        if (!StringUtils.hasLength(assesseeId)) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(assesseeId);
        return userEntity;
    }

}
