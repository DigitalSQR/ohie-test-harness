package com.argusoft.path.tht.testprocessmanagement.repository;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * This repository is for making queries on the TestRequest model.
 *
 * @author Dhruv
 */
@Repository
public interface TestRequestRepository
        extends JpaRepository<TestRequestEntity, String>, JpaSpecificationExecutor<TestRequestEntity> {

    @Query("SELECT DISTINCT entity FROM TestRequestEntity entity \n")
    public Page<TestRequestEntity> findTestRequests(Pageable pageable);

    @Query("SELECT DISTINCT entity FROM TestRequestEntity entity \n"
            + " WHERE entity.id IN (:ids)")
    public List<TestRequestEntity> findTestRequestsByIds(@Param("ids") List<String> ids);

    @Query("SELECT e FROM TestRequestEntity e WHERE e.updatedAt >= :sevenMonthsAgo")
    List<TestRequestEntity> findRecordsUpdatedLastSevenMonths(@Param("sevenMonthsAgo") Date sevenMonthsAgo);

    @Query("SELECT COUNT(e) FROM TestRequestEntity e WHERE e.state = :state")
    int findCountByState(@Param("state") String state);

    @Query("SELECT e.id FROM TestRequestEntity e WHERE e.state = :state")
    List<String> getPendingTestRequests(@Param("state") String state);

}
