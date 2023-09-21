/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testing.harness.tool.usermanagement.repository;

import com.testing.harness.tool.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.testing.harness.tool.usermanagement.filter.UserSearchFilter;
import com.testing.harness.tool.usermanagement.models.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This repository is for making queries on the User model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public interface UserCustomRepository {

    public Page<UserEntity> advanceUserSearch(
            UserSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException;
}
