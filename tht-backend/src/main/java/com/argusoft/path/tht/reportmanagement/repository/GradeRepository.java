package com.argusoft.path.tht.reportmanagement.repository;

import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the Grade model.
 *
 * @author Dhruv
 */

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, String> {

    @Query(value = "SELECT g FROM GradeEntity g WHERE g.percentage <= (:percentage) ORDER BY percentage DESC")
    Page<GradeEntity> getGradeBasedOnActualPercentage(@Param("percentage") Integer percentage, Pageable pageable);

}
