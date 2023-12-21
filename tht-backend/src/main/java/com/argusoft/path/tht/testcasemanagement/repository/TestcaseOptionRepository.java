/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is for making queries on the TestcaseOption model.
 *
 * @author Dhruv
 */
@Repository
public interface TestcaseOptionRepository
        extends JpaRepository<TestcaseOptionEntity, String>, TestcaseOptionCustomRepository {

    @Query("SELECT DISTINCT entity FROM TestcaseOptionEntity entity \n")
    public Page<TestcaseOptionEntity> findTestcaseOptions(Pageable pageable);

    @Query("SELECT DISTINCT entity FROM TestcaseOptionEntity entity \n"
            + " WHERE entity.id IN (:ids)")
    public List<TestcaseOptionEntity> findTestcaseOptionsByIds(@Param("ids") List<String> ids);

}
