/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.repository;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.utils.SQLUtils;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * This custom repository implementation is for making queries on the TestRequest model.
 *
 * @author Dhruv
 */
@Repository
public class TestRequestCustomRepositoryImpl
        implements TestRequestCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<TestRequestEntity> advanceTestRequestSearch(
            TestRequestSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException {
        StringBuilder jpql = new StringBuilder();
        Map<String, Object> parameters = new HashMap<String, Object>();

        jpql = jpql.append(" FROM TestRequestEntity testRequest \n");

        if (!searchFilter.isEmpty()) {
            jpql.append("WHERE \n");
            boolean separate;

            separate = SQLUtils.likeQL(
                    "testRequest",
                    "name",
                    searchFilter.getName(),
                    parameters,
                    searchFilter.getNameSearchType(),
                    false,
                    jpql);

            separate = SQLUtils.inQL(
                    "testRequest",
                    "state",
                    searchFilter.getState(),
                    parameters,
                    separate,
                    jpql);
        }

        try {
            return SQLUtils.getResultPage("testRequest", TestRequestEntity.class, jpql, parameters, pageable, entityManager);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OperationFailedException("Operation Failed while Executing query.", ex);
        }
    }
}
