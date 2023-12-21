/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This repository is for making queries on the TestcaseOption model.
 *
 * @author Dhruv
 */
public interface TestcaseOptionCustomRepository {

    public Page<TestcaseOptionEntity> advanceTestcaseOptionSearch(
            TestcaseOptionSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException;
}
