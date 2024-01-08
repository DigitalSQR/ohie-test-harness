/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.utils.SQLUtils;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * This custom repository implementation is for making queries on the Specification model.
 *
 * @author Dhruv
 */
@Repository
public class SpecificationCustomRepositoryImpl
        implements SpecificationCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<SpecificationEntity> advanceSpecificationSearch(
            SpecificationSearchFilter searchFilter,
            Pageable pageable
    ) throws OperationFailedException {
        StringBuilder jpql = new StringBuilder();
        Map<String, Object> parameters = new HashMap<String, Object>();

        jpql = jpql.append(" FROM SpecificationEntity specification \n"
                + "LEFT JOIN specification.component component \n"
                + "LEFT JOIN specification.testcases testcase \n");

        if (!searchFilter.isEmpty()) {
            jpql.append("WHERE \n");
            boolean separate;

            separate = SQLUtils.likeQL(
                    "specification",
                    "name",
                    searchFilter.getName(),
                    parameters,
                    searchFilter.getNameSearchType(),
                    false,
                    jpql);

            separate = SQLUtils.likeQL(
                    "specification",
                    "state",
                    searchFilter.getState(),
                    parameters,
                    separate,
                    jpql);


            separate = SQLUtils.likeQL(
                    "component",
                    "id",
                    searchFilter.getComponentId(),
                    parameters,
                    separate,
                    jpql);

            separate = SQLUtils.likeQL(
                    "testcase",
                    "isManual",
                    searchFilter.getManual(),
                    parameters,
                    separate,
                    jpql);
        }

        try {
            return SQLUtils.getResultPage("specification", SpecificationEntity.class, jpql, parameters, pageable, entityManager);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OperationFailedException("Operation Failed while Executing query.", ex);
        }
    }
}
