/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This repository is for making queries on the Specification model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public interface SpecificationCustomRepository {

    public Page<SpecificationEntity> advanceSpecificationSearch(
            SpecificationSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException;
}
