package com.argusoft.path.tht.reportmanagement.repository;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the TestcaseResult model.
 *
 * @author Dhruv
 */
@Repository
public interface TestcaseResultRepository
        extends JpaRepository<TestcaseResultEntity, String>, JpaSpecificationExecutor<TestcaseResultEntity> {

    @Query("SELECT DISTINCT entity FROM TestcaseResultEntity entity \n")
    public Page<TestcaseResultEntity> findTestcaseResults(Pageable pageable);

}
