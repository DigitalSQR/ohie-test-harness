package com.argusoft.path.tht.testprocessmanagement.repository;

import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the TestRequestValue model.
 *
 * @author Aastha
 */
@Repository
public interface TestRequestValueRepository
        extends JpaRepository<TestRequestValueEntity, String>, JpaSpecificationExecutor<TestRequestValueEntity> {

//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Optional<TestRequestValueEntity> findById(String id);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Page<TestRequestValueEntity> findAll(Specification<TestRequestValueEntity> feature1Specifications, Pageable pageable);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    List<TestRequestValueEntity> findAll(Specification<TestRequestValueEntity> feature1Specifications);


}
