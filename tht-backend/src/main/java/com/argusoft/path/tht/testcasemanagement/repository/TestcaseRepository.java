package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the Testcase model.
 *
 * @author Dhruv
 */
@Repository
public interface TestcaseRepository
        extends JpaRepository<TestcaseEntity, String>, JpaSpecificationExecutor<TestcaseEntity> {

    @Query("SELECT DISTINCT entity FROM TestcaseEntity entity \n")
    public Page<TestcaseEntity> findTestcases(Pageable pageable);

}
