package com.argusoft.path.tht.Audit.Service;

import com.argusoft.path.tht.Audit.constant.AuditServiceConstant;
import com.argusoft.path.tht.Audit.filter.SearchFilter;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import io.astefanutti.metrics.aspectj.Metrics;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.AuditQueryCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.persistence.EntityManager;
import java.util.List;
@Service
@Metrics(registry = "AuditServiceDaoImpl")
public class AuditServiceImpl implements AuditService{
    public static final Logger LOGGER = LoggerFactory.getLogger(AuditServiceImpl.class);
    @Autowired
    EntityManager entityManager;


    @Override
    public List<Object> searchAudit(SearchFilter searchFilter, ContextInfo contextInfo) throws DoesNotExistException , DataValidationErrorException , InvalidParameterException {

        try {

            String tableName = searchFilter.getName();
            tableName = tableName.toUpperCase();
            if (AuditServiceConstant.EntityType.valueOf(tableName).getEntityClass() == null) {
                LOGGER.error("Caught Exception in searchAudit");
                throw new DoesNotExistException("No Audit available for " + tableName);
            }

            AuditReader auditReader = AuditReaderFactory.get(entityManager);
            AuditQueryCreator queryCreator = auditReader.createQuery();

            List<String> ids = searchFilter.getIds();
            // Enable the filter on the audit query
            AuditQuery auditQuery = queryCreator.forRevisionsOfEntity(
                    AuditServiceConstant.EntityType.valueOf(tableName).getEntityClass(),
                    true, true);

            if (searchFilter.getName() == null) {
                LOGGER.error("Caught Exception in searchAudit");
                throw new InvalidParameterException("Table name not provided");
            }
            if (ids != null && !ids.isEmpty()) {
                auditQuery.add(AuditEntity.id().in(ids));
            }
            if(searchFilter.getVersionNumber() != null)
            {
                auditQuery.add(AuditEntity.property("version").eq(searchFilter.getVersionNumber()));
            }
            if (searchFilter.getRevType() != null) {
                auditQuery.add(AuditEntity.revisionType().eq(RevisionType.fromRepresentation(searchFilter.getRevType())));
            }


            List<Object> obj =  auditQuery.addOrder(AuditEntity.revisionType().desc()).getResultList();
            AuditServiceConstant.EntityType type = AuditServiceConstant.EntityType.valueOf(tableName);
            return type.createMapperObject(type).modelToDto(obj);
        }
        catch (Exception ex)
        {   LOGGER.error("Caught Exception in searchAudit");
            throw new InvalidParameterException("Invalid parameter value provided");
        }
    }

}

