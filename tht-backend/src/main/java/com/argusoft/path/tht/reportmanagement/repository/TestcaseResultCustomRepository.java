/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.repository;

import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This repository is for making queries on the TestcaseResult model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public interface TestcaseResultCustomRepository {

    public Page<TestcaseResultEntity> advanceTestcaseResultSearch(
            TestcaseResultSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException;
}
