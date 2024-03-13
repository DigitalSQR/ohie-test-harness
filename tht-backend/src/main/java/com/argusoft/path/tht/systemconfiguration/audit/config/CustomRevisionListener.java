package com.argusoft.path.tht.systemconfiguration.audit.config;

import com.argusoft.path.tht.systemconfiguration.audit.entity.CustomRevisionEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Component
public class CustomRevisionListener implements RevisionListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(CustomRevisionListener.class);
    @Autowired
    private final ApplicationContext applicationContext;

    @Autowired
    public CustomRevisionListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
        try {
            customRevisionEntity.setRevisionNumber(getNextRevisionNumber());
        } catch (OperationFailedException e) {
            LOGGER.error("Caught Exception in newRevision" + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private int getNextRevisionNumber() throws OperationFailedException {
        try {
            EntityManager entityManager = this.applicationContext.getBean(EntityManager.class);
            Query query = entityManager.createQuery("SELECT MAX(revisionNumber) FROM revision");
            Integer maxRevision = (Integer) query.getSingleResult();
            return (maxRevision != null) ? maxRevision + 1 : 1;
        } catch (Exception ex) {
            LOGGER.error("Caught Exception in newRevision" + ex);
            throw new OperationFailedException(ex.getMessage());
        }
    }
}
