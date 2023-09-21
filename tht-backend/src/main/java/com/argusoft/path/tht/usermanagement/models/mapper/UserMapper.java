package com.argusoft.path.tht.usermanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.MetaMapper;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends MetaMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @InheritInverseConfiguration
    UserInfo modelToDto(UserEntity userEntity);

    @Mapping(source = "meta.createdAt", target = "createdAt")
    @Mapping(source = "meta.createdBy", target = "createdBy")
    @Mapping(source = "meta.updatedAt", target = "updatedAt")
    @Mapping(source = "meta.updatedBy", target = "updatedBy")
    @Mapping(source = "meta.version", target = "version")
    UserEntity dtoToModel(UserInfo userInfo);

    @InheritInverseConfiguration
    List<UserInfo> modelToDto(List<UserEntity> userEntities);

    @Mapping(source = "meta.createdAt", target = "createdAt")
    @Mapping(source = "meta.createdBy", target = "createdBy")
    @Mapping(source = "meta.updatedAt", target = "updatedAt")
    @Mapping(source = "meta.updatedBy", target = "updatedBy")
    @Mapping(source = "meta.version", target = "version")
    List<UserEntity> dtoToModel(List<UserInfo> userInfos);

    // Custom mapping method for Page<UserEntity> to Page<UserDto>
    default Page<UserInfo> pageEntityToDto(Page<UserEntity> page) {
        List<UserEntity> userEntities = page.getContent();
        List<UserInfo> userDtoList = this.modelToDto(userEntities);
        return new PageImpl<>(userDtoList, page.getPageable(), page.getTotalElements());
    }
}
