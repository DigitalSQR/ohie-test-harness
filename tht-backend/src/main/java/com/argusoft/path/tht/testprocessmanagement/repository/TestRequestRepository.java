/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.repository;

import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
