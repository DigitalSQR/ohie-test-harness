/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is for making queries on the Testcase model.
 *
 * @author Dhruv
 */
@Repository
public interface TestcaseRepository
        extends JpaRepository<TestcaseEntity, String>, TestcaseCustomRepository {

    @Query("SELECT DISTINCT entity FROM TestcaseEntity entity \n")
    public Page<TestcaseEntity> findTestcases(Pageable pageable);

    @Query("SELECT DISTINCT entity FROM TestcaseEntity entity \n"
            + " WHERE entity.id IN (:ids)")
    public List<TestcaseEntity> findTestcasesByIds(@Param("ids") List<String> ids);

}
