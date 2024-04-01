package com.argusoft.path.tht.reportmanagement.repository;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @Query("SELECT entity FROM TestcaseResultEntity entity WHERE entity.refId = :refId")
    public List<TestcaseResultEntity> findByRefId(@Param("refId") String refId);

    @Query("SELECT e FROM TestcaseResultEntity e WHERE e.updatedAt >= :sevenMonthsAgo ORDER BY e.updatedAt ASC")
    List<TestcaseResultEntity> findRecordsUpdatedLastSevenMonths(@Param("sevenMonthsAgo") Date sevenMonthsAgo);

    @Query("SELECT e FROM TestcaseResultEntity e WHERE e.refObjUri = :refObjUri " +
            "ORDER BY CASE WHEN e.nonCompliant = 0 THEN e.compliant " +
            "ELSE (e.compliant/e.nonCompliant) END DESC")
    Page<TestcaseResultEntity> findTopFiveTestRequestsResult(String refObjUri, Pageable pageable);

    @Query("SELECT e FROM TestcaseResultEntity e WHERE e.refId = :refId")
    List<TestcaseResultEntity> findBestOfEachComponent(String refId);

    //@Query("SELECT SUM(e.compliant), SUM(e.nonCompliant), name, ref_id FROM TestcaseResultEntity e WHERE e.refObjUri = :refObjUri GROUP BY refId, name")
    @Query("SELECT SUM(e.compliant), SUM(e.nonCompliant), e.name, e.refId FROM TestcaseResultEntity e WHERE e.refObjUri = :refObjUri AND e.state = :state GROUP BY e.refId, e.name")
    List<Object[]> nameComplianceAndNonCompliance(String refObjUri, String state);

}
