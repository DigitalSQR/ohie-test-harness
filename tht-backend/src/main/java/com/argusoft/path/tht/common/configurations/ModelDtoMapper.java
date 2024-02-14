package com.argusoft.path.tht.common.configurations;

import java.util.List;

public interface ModelDtoMapper<E,D> {
    D modelToDto(E entity);
    E dtoToModel(D Dto);
    List<D> modelToDto(List<E> entities);
    List<E> dtoToModel(List<D> models);
}
