package com.argusoft.path.tht.testcasemanagement.repository;

import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.utils.SQLUtils;
import com.argusoft.path.tht.testcasemanagement.filter.DocumentSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DocumentCustomRepositoryImpl implements DocumentCustomRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<DocumentEntity> advanceComponentSearch(DocumentSearchFilter searchFilter, Pageable pageable) throws OperationFailedException {
        StringBuilder jpql = new StringBuilder();
        Map<String, Object> parameters = new HashMap<String, Object>();

        jpql = jpql.append(" FROM DocumentEntity component \n");

        if (!searchFilter.isEmpty()) {
            jpql.append("WHERE \n");
            boolean separate;

            separate = SQLUtils.likeQL(
                    "document",
                    "name",
                    searchFilter.getName(),
                    parameters,
                    searchFilter.getNameSearchType(),
                    false,
                    jpql);

            separate = SQLUtils.likeQL(
                    "document",
                    "state",
                    searchFilter.getState(),
                    parameters,
                    SearchType.EXACTLY,
                    separate,
                    jpql);

            separate = SQLUtils.likeQL(
                    "document",
                    "ref_obj_uri",
                    searchFilter.getRefObjUri(),
                    parameters,
                    SearchType.EXACTLY,
                    separate,
                    jpql);

            separate = SQLUtils.likeQL(
                    "document",
                    "ref_id",
                    searchFilter.getRefId(),
                    parameters,
                    SearchType.EXACTLY,
                    separate,
                    jpql);

            separate = SQLUtils.likeQL(
                    "document",
                    "file_type",
                    searchFilter.getFileType(),
                    parameters,
                    SearchType.EXACTLY,
                    separate,
                    jpql);

        }

        try {
            return SQLUtils.getResultPage("document", DocumentEntity.class, jpql, parameters, pageable, entityManager);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OperationFailedException("Operation Failed while Executing query.", ex);
        }
    }
}
