/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.repository;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This repository is for making queries on the TestRequest model.
 *
 * @author Dhruv
 */
public interface TestRequestCustomRepository {

    public Page<TestRequestEntity> advanceTestRequestSearch(
            TestRequestSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException;
}
