package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This repository is for making queries on the Specification model.
 *
 * @author Dhruv
 */
@Repository
public interface SpecificationRepository
        extends JpaRepository<SpecificationEntity, String>, JpaSpecificationExecutor<SpecificationEntity> {

    @Query("SELECT DISTINCT entity FROM SpecificationEntity entity \n")
    public Page<SpecificationEntity> findSpecifications(Pageable pageable);

}
