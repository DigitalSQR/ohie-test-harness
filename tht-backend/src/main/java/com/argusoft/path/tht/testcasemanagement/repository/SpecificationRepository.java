package com.argusoft.path.tht.testcasemanagement.repository;


import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This repository is for making queries on the Specification model.
 *
 * @author Dhruv
 */
@Repository
public interface SpecificationRepository
        extends JpaRepository<SpecificationEntity, String>, JpaSpecificationExecutor<SpecificationEntity> {

//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Optional<SpecificationEntity> findById(String id);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Page<SpecificationEntity> findAll(Specification<SpecificationEntity> feature1Specifications, Pageable pageable);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    List<SpecificationEntity> findAll(Specification<SpecificationEntity> feature1Specifications);

    @Query("SELECT e FROM SpecificationEntity e WHERE e.component.id = :componentId")
    Set<SpecificationEntity> findByComponentId(@Param("componentId") String componentId);

}
