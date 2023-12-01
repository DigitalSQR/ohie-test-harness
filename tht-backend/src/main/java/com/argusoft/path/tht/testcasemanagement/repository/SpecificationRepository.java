/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is for making queries on the Specification model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Repository
public interface SpecificationRepository
        extends JpaRepository<SpecificationEntity, String>, SpecificationCustomRepository {

    @Query("SELECT DISTINCT entity FROM SpecificationEntity entity \n")
    public Page<SpecificationEntity> findSpecifications(Pageable pageable);

    @Query("SELECT DISTINCT entity FROM SpecificationEntity entity \n"
            + " WHERE entity.id IN (:ids)")
    public List<SpecificationEntity> findSpecificationsByIds(@Param("ids") List<String> ids);

}
