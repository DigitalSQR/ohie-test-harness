/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.systemconfiguration.utils;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Map;

/**
 * This SQLUtils provides methods for SQL.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public final class SQLUtils {

    public static boolean likeQL(
            String tableName,
            String columnName,
            String columnValue,
            Map<String, Object> parameters,
            SearchType searchType,
            boolean separate,
            StringBuilder stringBuilder) {
        if (StringUtils.isEmpty(columnValue)) return separate;
        if (separate) {
            stringBuilder.append("AND ");
        }
        stringBuilder
                .append("UPPER(").append(tableName).append(".").append(columnName).append(")")
                .append(" LIKE :").append(columnName).append(" ");
        parameters.put(columnName, parameterLikePattern(columnValue, searchType));
        return true;
    }

    public static <T> Page<T> getResultPage(
            String tableName,
            Class<T> type,
            StringBuilder jpql,
            Map<String, Object> parameters,
            Pageable pageable,
            EntityManager entityManager
    ) {
        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT DISTINCT COUNT(*)" + jpql.toString(), Long.class);
        parameters.forEach((key, value) -> countQuery.setParameter(key, value));
        long totalCount = countQuery.getSingleResult();

        if (!pageable.getSort().isEmpty()) {
            jpql.append(" order by ");
            String saperator = "";
            for (Sort.Order order : pageable.getSort()) {
                jpql
                        .append(saperator)
                        .append(order.getProperty())
                        .append(" ")
                        .append(order.isDescending() ? "DESC" : "");
                saperator = ",";
            }
        }

        TypedQuery<T> query = (TypedQuery<T>) entityManager.createQuery("SELECT DISTINCT " + tableName + jpql.toString(), type)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        parameters.forEach((key, value) -> query.setParameter(key, value));
        List<T> entities = query.getResultList();
        return new PageImpl<>(entities, pageable, totalCount);
    }

    public static boolean equalQL(
            String tableName,
            String columnName,
            Object columnValue,
            Map<String, Object> parameters,
            boolean separate,
            StringBuilder stringBuilder) {
        if (StringUtils.isEmpty(columnValue)) return separate;
        if (separate) {
            stringBuilder.append("AND ");
        }
        stringBuilder
                .append("UPPER(").append(tableName).append(".").append(columnName).append(")")
                .append(" = :").append(columnName).append(" ");
        parameters.put(columnName, columnValue.toString().toUpperCase());
        return true;
    }

    public static String parameterLikePattern(
            String columnValue,
            SearchType searchType) {
        switch (searchType) {
            case EXACTLY:
                return "%" + columnValue.toUpperCase() + "%";
            case STARTING:
                return "%" + columnValue.toUpperCase();
            case ENDING:
                return columnValue.toUpperCase() + "%";
            //default case is for CONTAINING
            default:
                return columnValue.toUpperCase();
        }
    }
}
