package com.argusoft.path.tht.usermanagement.models.mapper;

import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper to covert DTO <-> Entity for the User.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "roles", target = "roleIds")
    UserInfo modelToDto(UserEntity userEntity);


    @InheritInverseConfiguration
    UserEntity dtoToModel(UserInfo userInfo);

    List<UserInfo> modelToDto(List<UserEntity> userEntities);

    List<UserEntity> dtoToModel(List<UserInfo> userInfos);

    // Custom mapping method for Page<UserEntity> to Page<UserDto>
    default Page<UserInfo> pageEntityToDto(Page<UserEntity> page) {
        List<UserEntity> userEntities = page.getContent();
        List<UserInfo> userDtoList = this.modelToDto(userEntities);
        return new PageImpl<>(userDtoList, page.getPageable(), page.getTotalElements());
    }

    default Set<String> setToRoleIds(Set<RoleEntity> roles) {
        return roles.stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toSet());
    }

    default Set<RoleEntity> setToRoles(Set<String> roleIds) {
        return roleIds.stream()
                .map(id -> {
                    RoleEntity roleEntity = new RoleEntity();
                    roleEntity.setId(id);
                    return roleEntity;
                })
                .collect(Collectors.toSet());
    }
}
