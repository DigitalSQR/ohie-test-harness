package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;
import com.argusoft.path.tht.testcasemanagement.models.dto.DocumentInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DocumentMapper {


    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    DocumentInfo modelToDto(DocumentEntity componentEntity);

    DocumentEntity DtoToModel(DocumentInfo componentEntity);

}
