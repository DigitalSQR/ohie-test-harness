package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
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
 * This repository is for making queries on the Testcase model.
 *
 * @author Dhruv
 */
@Repository
public interface TestcaseRepository
        extends JpaRepository<TestcaseEntity, String>, JpaSpecificationExecutor<TestcaseEntity> {

//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Optional<TestcaseEntity> findById(String id);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Page<TestcaseEntity> findAll(Specification<TestcaseEntity> feature1Specifications, Pageable pageable);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    List<TestcaseEntity> findAll(Specification<TestcaseEntity> feature1Specifications);

    @Query("SELECT e FROM TestcaseEntity e WHERE e.specification.id = :specificationId")
    Set<TestcaseEntity> findBySpecificationId(@Param("specificationId") String specificationId);

}
