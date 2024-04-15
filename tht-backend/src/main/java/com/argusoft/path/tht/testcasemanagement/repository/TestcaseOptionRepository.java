package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

/**
 * This repository is for making queries on the TestcaseOption model.
 *
 * @author Dhruv
 */
@Repository
public interface TestcaseOptionRepository
        extends JpaRepository<TestcaseOptionEntity, String>, JpaSpecificationExecutor<TestcaseOptionEntity> {

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Override
    Optional<TestcaseOptionEntity> findById(String id);

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Override
    Page<TestcaseOptionEntity> findAll(Specification<TestcaseOptionEntity> feature1Specifications, Pageable pageable);

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Override
    List<TestcaseOptionEntity> findAll(Specification<TestcaseOptionEntity> feature1Specifications);


}
