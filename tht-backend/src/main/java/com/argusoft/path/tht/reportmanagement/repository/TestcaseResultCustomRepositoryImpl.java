/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.repository;

import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.utils.SQLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * This custom repository implementation is for making queries on the TestcaseResult model.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Repository
public class TestcaseResultCustomRepositoryImpl
        implements TestcaseResultCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<TestcaseResultEntity> advanceTestcaseResultSearch(
            TestcaseResultSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException {
        StringBuilder jpql = new StringBuilder();
        Map<String, Object> parameters = new HashMap<String, Object>();

        jpql = jpql.append(" FROM TestcaseResultEntity testcaseResult \n"
                + "JOIN testcaseResult.tester tester \n");

        if (!searchFilter.isEmpty()) {
            jpql.append("WHERE \n");
            boolean separate;

            separate = SQLUtils.likeQL(
                    "testcaseResult",
                    "name",
                    searchFilter.getName(),
                    parameters,
                    searchFilter.getNameSearchType(),
                    false,
                    jpql);

            separate = SQLUtils.likeQL(
                    "testcaseResult",
                    "state",
                    searchFilter.getState(),
                    parameters,
                    searchFilter.getStateSearchType(),
                    separate,
                    jpql);

            separate = SQLUtils.likeQL(
                    "tester",
                    "id",
                    searchFilter.getTesterId(),
                    parameters,
                    SearchType.EXACTLY,
                    separate,
                    jpql);

            separate = SQLUtils.likeQL(
                    "testcaseResult",
                    "refId",
                    searchFilter.getRefId(),
                    parameters,
                    SearchType.EXACTLY,
                    separate,
                    jpql);

            separate = SQLUtils.likeQL(
                    "testcaseResult",
                    "refObjUri",
                    searchFilter.getRefObjUri(),
                    parameters,
                    SearchType.EXACTLY,
                    separate,
                    jpql);

            separate = SQLUtils.likeQL(
                    "testcaseResult",
                    "testRequestId",
                    searchFilter.getTestRequestId(),
                    parameters,
                    SearchType.EXACTLY,
                    separate,
                    jpql);
        }

        try {
            return SQLUtils.getResultPage("testcaseResult", TestcaseResultEntity.class, jpql, parameters, pageable, entityManager);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OperationFailedException("Operation Failed while Executing query.", ex);
        }
    }
}