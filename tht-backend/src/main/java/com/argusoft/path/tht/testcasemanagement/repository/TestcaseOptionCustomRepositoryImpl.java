/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.utils.SQLUtils;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * This custom repository implementation is for making queries on the TestcaseOption model.
 *
 * @author Dhruv
 */
@Repository
public class TestcaseOptionCustomRepositoryImpl
        implements TestcaseOptionCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<TestcaseOptionEntity> advanceTestcaseOptionSearch(
            TestcaseOptionSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException {
        StringBuilder jpql = new StringBuilder();
        Map<String, Object> parameters = new HashMap<String, Object>();

        jpql = jpql.append(" FROM TestcaseOptionEntity testcaseOption \n" +
                "LEFT JOIN testcaseOption.testcase testcase \n");

        if (!searchFilter.isEmpty()) {
            jpql.append("WHERE \n");
            boolean separate;

            separate = SQLUtils.likeQL(
                    "testcaseOption",
                    "name",
                    searchFilter.getName(),
                    parameters,
                    searchFilter.getNameSearchType(),
                    false,
                    jpql);

            separate = SQLUtils.likeQL(
                    "testcaseOption",
                    "state",
                    searchFilter.getState(),
                    parameters,
                    separate,
                    jpql);


            separate = SQLUtils.likeQL(
                    "testcase",
                    "id",
                    searchFilter.getTestcaseId(),
                    parameters,
                    separate,
                    jpql);
        }

        try {
            return SQLUtils.getResultPage("testcaseOption", TestcaseOptionEntity.class, jpql, parameters, pageable, entityManager);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OperationFailedException("Operation Failed while Executing query.", ex);
        }
    }
}
