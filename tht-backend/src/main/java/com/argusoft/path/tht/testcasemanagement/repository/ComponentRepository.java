/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the Component model.
 *
 * @author Dhruv
 */
@Repository
public interface ComponentRepository
        extends JpaRepository<ComponentEntity, String>, JpaSpecificationExecutor<ComponentEntity> {

    @Query("SELECT DISTINCT entity FROM ComponentEntity entity \n")
    public Page<ComponentEntity> findComponents(Pageable pageable);

}
