package com.argusoft.path.tht.fileservice.models.mapper;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper extends ModelDtoMapper<DocumentEntity, DocumentInfo> {


    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    @Mapping(source = "owner", target = "ownerId")
    DocumentInfo modelToDto(DocumentEntity documentEntity);

    @InheritInverseConfiguration
    DocumentEntity dtoToModel(DocumentInfo documentEntity);

    List<DocumentEntity> dtoToModel(List<DocumentInfo> documentInfos);

    List<DocumentInfo> modelToDto(List<DocumentEntity> documentEntities);

    default Page<DocumentInfo> pageEntityToDto(Page<DocumentEntity> page) {
        List<DocumentEntity> documentEntities = page.getContent();
        List<DocumentInfo> documentInfoList = this.modelToDto(documentEntities);
        return new PageImpl<>(documentInfoList, page.getPageable(), page.getTotalElements());
    }

    default String setToOwnerId(UserEntity owner) {
        if (owner == null) return null;
        return owner.getId();
    }

    default UserEntity setToOwner(String ownerId) {
        if (!StringUtils.hasLength(ownerId)) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(ownerId);
        return userEntity;
    }
}
