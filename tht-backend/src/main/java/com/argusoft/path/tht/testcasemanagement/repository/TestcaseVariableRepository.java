package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the TestcaseVariables model.
 *
 * @author Dhruv
 */
@Repository
public interface TestcaseVariableRepository
        extends JpaRepository<TestcaseVariableEntity, String>, JpaSpecificationExecutor<TestcaseVariableEntity> {

//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Optional<TestcaseVariablesEntity> findById(String id);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    Page<TestcaseVariablesEntity> findAll(Specification<TestcaseVariablesEntity> feature1Specifications, Pageable pageable);
//
//    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
//    @Override
//    List<TestcaseVariablesEntity> findAll(Specification<TestcaseVariablesEntity> feature1Specifications);


}
