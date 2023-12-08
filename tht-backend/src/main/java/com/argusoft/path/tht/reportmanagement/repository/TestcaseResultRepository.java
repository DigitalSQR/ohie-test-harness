/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.repository;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is for making queries on the TestcaseResult model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Repository
public interface TestcaseResultRepository
        extends JpaRepository<TestcaseResultEntity, String>, TestcaseResultCustomRepository {

    @Query("SELECT DISTINCT entity FROM TestcaseResultEntity entity \n")
    public Page<TestcaseResultEntity> findTestcaseResults(Pageable pageable);

    @Query("SELECT DISTINCT entity FROM TestcaseResultEntity entity \n"
            + " WHERE entity.id IN (:ids)")
    public List<TestcaseResultEntity> findTestcaseResultsByIds(@Param("ids") List<String> ids);

}