package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.mapper.ModelDtoMapper;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Mapper to covert DTO <-> Entity for the Testcase.
 *
 * @author Dhruv
 */
@Mapper(componentModel = "spring")
public interface TestcaseMapper extends ModelDtoMapper<TestcaseEntity,TestcaseInfo> {

    TestcaseMapper INSTANCE = Mappers.getMapper(TestcaseMapper.class);

    @Mapping(source = "specification", target = "specificationId")
    TestcaseInfo modelToDto(TestcaseEntity testcaseEntity);

    @InheritInverseConfiguration
    TestcaseEntity dtoToModel(TestcaseInfo testcaseInfo);

    List<TestcaseInfo> modelToDto(List<TestcaseEntity> testcaseEntities);

    List<TestcaseEntity> dtoToModel(List<TestcaseInfo> testcaseInfos);

    default String setToSpecificationId(SpecificationEntity specificationEntity) {
        if (specificationEntity == null) return null;
        return specificationEntity.getId();
    }

    default SpecificationEntity setToSpecification(String specificationId) {
        if (!StringUtils.hasLength(specificationId)) {
            return null;
        }
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId(specificationId);
        return specificationEntity;
    }

}
