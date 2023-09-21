/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testing.harness.tool.usermanagement.repository;

import com.testing.harness.tool.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.testing.harness.tool.systemconfiguration.utils.SQLUtils;
import com.testing.harness.tool.usermanagement.filter.UserSearchFilter;
import com.testing.harness.tool.usermanagement.models.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * This custom repository implementation is for making queries on the Role model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Repository
public class UserCustomRepositoryImpl
        implements UserCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<UserEntity> advanceUserSearch(
            UserSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException {
        StringBuilder jpql = new StringBuilder();
        Map<String, Object> parameters = new HashMap<String, Object>();

        jpql = jpql.append(" FROM UserEntity user \n");

        if(!searchFilter.isEmpty()) {
            jpql.append("WHERE \n");
            boolean separate;

            separate = SQLUtils.likeQL(
                    "user",
                    "name",
                    searchFilter.getName(),
                    parameters,
                    searchFilter.getNameSearchType(),
                    false,
                    jpql);

            separate = SQLUtils.equalQL(
                    "user",
                    "userName",
                    searchFilter.getUserName(),
                    parameters,
                    separate,
                    jpql);

            separate = SQLUtils.equalQL(
                    "user",
                    "email",
                    searchFilter.getEmail(),
                    parameters,
                    separate,
                    jpql);
        }

        try {
            return SQLUtils.getResultPage("user", UserEntity.class, jpql, parameters, pageable, entityManager);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OperationFailedException("Operation Failed while Executing query.", ex);
        }
    }
}
