package com.argusoft.path.tht.testcasemanagement.models.mapper;

import com.argusoft.path.tht.common.configurations.ModelDtoMapper;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    // Custom mapping method for Page<TestcaseEntity> to Page<TestcaseInfo>
    default Page<TestcaseInfo> pageEntityToDto(Page<TestcaseEntity> page) {
        List<TestcaseEntity> testcaseEntities = page.getContent();
        List<TestcaseInfo> testcaseDtoList = this.modelToDto(testcaseEntities);
        return new PageImpl<>(testcaseDtoList, page.getPageable(), page.getTotalElements());
    }

    default String setToSpecificationId(SpecificationEntity specificationEntity) {
        if (specificationEntity == null) return null;
        return specificationEntity.getId();
    }

    default SpecificationEntity setToSpecification(String specificationId) {
        if (StringUtils.isEmpty(specificationId)) {
            return null;
        }
        SpecificationEntity specificationEntity = new SpecificationEntity();
        specificationEntity.setId(specificationId);
        return specificationEntity;
    }

}
