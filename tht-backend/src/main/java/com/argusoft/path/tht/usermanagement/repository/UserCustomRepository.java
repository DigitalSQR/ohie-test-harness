/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.repository;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * This repository is for making queries on the User model.
 *
 * @author Dhruv
 */
public interface UserCustomRepository {

    public Page<UserEntity> advanceUserSearch(
            UserSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException;
}
