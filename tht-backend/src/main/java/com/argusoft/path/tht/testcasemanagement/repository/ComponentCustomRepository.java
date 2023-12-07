/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This repository is for making queries on the Component model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public interface ComponentCustomRepository {

    public Page<ComponentEntity> advanceComponentSearch(
            ComponentSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException;
}
