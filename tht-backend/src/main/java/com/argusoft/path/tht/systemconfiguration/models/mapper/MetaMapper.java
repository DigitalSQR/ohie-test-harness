package com.argusoft.path.tht.systemconfiguration.models.mapper;

import com.argusoft.path.tht.systemconfiguration.models.dto.MetaInfo;
import org.mapstruct.Mapper;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface MetaMapper {

    default Date setToCreatedAt(MetaInfo meta) {
        return meta.getCreatedAt();
    }

    default Date setToUpdatedAt(MetaInfo meta) {
        return meta.getUpdatedAt();
    }

    default String setToCreatedBy(MetaInfo meta) {
        return meta.getCreatedBy();
    }

    default String setToUpdatedBy(MetaInfo meta) {
        return meta.getUpdatedBy();
    }

    default Long setToVersion(MetaInfo meta) {
        return meta.getVersion();
    }

    default MetaInfo setToMeta(Date createdAt, String createdBy, Date updatedAt, String updatedBy, Long version) {
        return new MetaInfo(createdAt, createdBy, updatedAt, updatedBy, version);
    }
}
