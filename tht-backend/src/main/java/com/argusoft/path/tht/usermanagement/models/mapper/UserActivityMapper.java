package com.argusoft.path.tht.usermanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.usermanagement.models.dto.UserActivityInfo;
import com.argusoft.path.tht.usermanagement.models.entity.UserActivityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserActivityMapper extends ModelDtoMapper<UserActivityEntity, UserActivityInfo> {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserActivityInfo modelToDto(UserActivityEntity userActivityEntity);

    UserActivityEntity dtoToModel(UserActivityInfo userActivityInfo);

    List<UserActivityInfo> modelToDto(List<UserActivityEntity> userActivityEntities);

    List<UserActivityEntity> dtoToModel(List<UserActivityInfo> userActivityInfos);

}
