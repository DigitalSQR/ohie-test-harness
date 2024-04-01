package com.argusoft.path.tht.notificationmanagement.models.mapper;

import com.argusoft.path.tht.notificationmanagement.models.dto.NotificationInfo;
import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Mapper to covert DTO <-> Entity for the Notification.
 *
 * @author Ali
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends ModelDtoMapper<NotificationEntity, NotificationInfo> {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Override
    @Mapping(source = "receiver", target = "receiverId")
    NotificationInfo modelToDto(NotificationEntity notificationEntity);

    @Override
    @InheritInverseConfiguration
    NotificationEntity dtoToModel(NotificationInfo notificationInfo);

    @Override
    List<NotificationInfo> modelToDto(List<NotificationEntity> notificationEntities);

    @Override
    List<NotificationEntity> dtoToModel(List<NotificationInfo> notificationInfos);

    default String setToOReceiverId(UserEntity receiver) {
        if (receiver == null) {
            return null;
        }
        return receiver.getId();
    }

    default UserEntity setToReceiver(String receiverId) {
        if (!StringUtils.hasLength(receiverId)) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(receiverId);
        return userEntity;
    }

}
