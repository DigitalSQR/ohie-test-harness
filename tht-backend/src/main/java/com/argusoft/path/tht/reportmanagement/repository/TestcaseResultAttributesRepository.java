package com.argusoft.path.tht.reportmanagement.repository;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This repository is for making queries on the TestcaseResultAttributes model.
 *
 * @author Bhavi
 */

@Repository
public interface TestcaseResultAttributesRepository extends JpaRepository<TestcaseResultAttributesEntity, String>, JpaSpecificationExecutor<TestcaseResultAttributesEntity> {

    @Query("SELECT tra FROM TestcaseResultAttributesEntity tra WHERE tra.testcaseResultEntity = :testcaseResultEntity AND tra.key = :key")
    Optional<TestcaseResultAttributesEntity> findByTestcaseResultEntityAndKey(@Param("testcaseResultEntity") TestcaseResultEntity testcaseResultEntity, @Param("key") String key);

    @Modifying
    @Query("DELETE FROM TestcaseResultAttributesEntity t WHERE t.testcaseResultEntity = :testcaseResultEntity")
    void deleteByTestcaseResultEntity(@Param("testcaseResultEntity") TestcaseResultEntity testcaseResultEntity);

}
