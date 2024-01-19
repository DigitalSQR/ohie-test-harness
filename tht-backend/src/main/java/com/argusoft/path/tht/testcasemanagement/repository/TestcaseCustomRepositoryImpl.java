/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.utils.SQLUtils;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * This custom repository implementation is for making queries on the Testcase model.
 *
 * @author Dhruv
 */
@Repository
public class TestcaseCustomRepositoryImpl
        implements TestcaseCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<TestcaseEntity> advanceTestcaseSearch(
            TestcaseSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException {
        StringBuilder jpql = new StringBuilder();
        Map<String, Object> parameters = new HashMap<String, Object>();

        jpql = jpql.append(" FROM TestcaseEntity testcase \n"
                + "LEFT JOIN testcase.specification specification \n");

        if (!searchFilter.isEmpty()) {
            jpql.append("WHERE \n");
            boolean separate;

            separate = SQLUtils.likeQL(
                    "testcase",
                    "name",
                    searchFilter.getName(),
                    parameters,
                    searchFilter.getNameSearchType(),
                    false,
                    jpql);

            separate = SQLUtils.equalQL(
                    "testcase",
                    "state",
                    searchFilter.getState(),
                    parameters,
                    separate,
                    jpql);

            separate = SQLUtils.equalQL(
                    "specification",
                    "id",
                    searchFilter.getSpecificationId(),
                    parameters,
                    separate,
                    jpql);

            separate = SQLUtils.equalQL(
                    "testcase",
                    "isManual",
                    searchFilter.getManual(),
                    parameters,
                    separate,
                    jpql);
        }

        try {
            return SQLUtils.getResultPage("testcase", TestcaseEntity.class, jpql, parameters, pageable, entityManager);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OperationFailedException("Operation Failed while Executing query.", ex);
        }
    }
}
