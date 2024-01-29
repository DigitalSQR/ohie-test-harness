package com.argusoft.path.tht.reportmanagement.models.mapper;


import com.argusoft.path.tht.reportmanagement.models.dto.GradeInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradeMapper {

    GradeMapper INSTANCE = Mappers.getMapper(GradeMapper.class);

    GradeInfo modelToDto(GradeEntity gradeEntity);

    GradeEntity dtoToModel(GradeInfo gradeInfo);

    List<GradeInfo> modelToDto(List<GradeEntity> gradeEntities);

    List<GradeEntity> dtoToModel(List<GradeInfo> gradeEntities);


}
