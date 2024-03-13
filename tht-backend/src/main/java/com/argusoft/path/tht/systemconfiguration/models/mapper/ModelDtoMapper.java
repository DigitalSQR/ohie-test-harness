package com.argusoft.path.tht.systemconfiguration.models.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface ModelDtoMapper<E, D> {

    D modelToDto(E entity);

    E dtoToModel(D dto);

    List<D> modelToDto(List<E> entities);

    List<E> dtoToModel(List<D> dtos);

    // Custom mapping method for Page<E> to Page<D>
    default Page<D> pageEntityToDto(Page<E> ePage) {
        List<E> entities = ePage.getContent();
        List<D> dtos = this.modelToDto(entities);
        return new PageImpl<>(dtos, ePage.getPageable(), ePage.getTotalElements());
    }

}
