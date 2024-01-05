package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.testcasemanagement.models.dto.DocumentInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper {


    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    DocumentInfo modelToDto(DocumentEntity componentEntity);

    DocumentEntity dtoToModel(DocumentInfo componentEntity);

    List<DocumentEntity> dtoToModel(List<DocumentInfo> documentInfos);

    List<DocumentInfo> modelToDto(List<DocumentEntity> documentEntities);

}
