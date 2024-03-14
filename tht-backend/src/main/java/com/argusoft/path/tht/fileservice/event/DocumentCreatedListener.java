package com.argusoft.path.tht.fileservice.event;

import com.argusoft.path.tht.fileservice.constant.DocumentServiceConstants;
import com.argusoft.path.tht.fileservice.constant.DocumentUtil;
import com.argusoft.path.tht.fileservice.filter.DocumentCriteriaSearchFilter;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.fileservice.service.DocumentService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Document Created Listener
 *
 * @author Hardik
 */

@Service
public class DocumentCreatedListener {


    public static final Logger LOGGER = LoggerFactory.getLogger(DocumentCreatedListener.class);
    private DocumentService documentService;

    private static DocumentCriteriaSearchFilter getDocumentCriteriaSearchFilterPrepared(DocumentEntity createdDocument) {
        DocumentCriteriaSearchFilter documentCriteriaSearchFilter = new DocumentCriteriaSearchFilter();
        documentCriteriaSearchFilter.setRefId(createdDocument.getRefId());
        documentCriteriaSearchFilter.setRefObjUri(createdDocument.getRefObjUri());
        documentCriteriaSearchFilter.setDocumentType(createdDocument.getDocumentType());
        documentCriteriaSearchFilter.setState(Collections.singletonList(DocumentServiceConstants.DOCUMENT_STATUS_ACTIVE));
        return documentCriteriaSearchFilter;
    }

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @EventListener
    public void changeStateBasedOnCreatedDocumentType(DocumentCreatedEvent event) throws DataValidationErrorException, OperationFailedException, VersionMismatchException {
        try {
            if (event.getSource() instanceof DocumentEntity createdDocument) {
                String allowedActiveTypeForDocumentType = DocumentUtil.getAllowedActiveTypeForDocumentType(createdDocument.getRefObjUri(), createdDocument.getDocumentType());

                switch (allowedActiveTypeForDocumentType) {
                    case DocumentServiceConstants.ALLOWED_ACTIVE_SINGLE_RECORD -> {
                        DocumentCriteriaSearchFilter documentCriteriaSearchFilter = getDocumentCriteriaSearchFilterPrepared(createdDocument);

                        List<DocumentEntity> documentEntities = documentService.searchDocument(documentCriteriaSearchFilter, event.getContextInfo());

                        for (DocumentEntity documentEntity : documentEntities) {
                            if (!documentEntity.getId().equals(createdDocument.getId())) {
                                documentService.changeState(documentEntity.getId(), DocumentServiceConstants.DOCUMENT_STATUS_INACTIVE, event.getContextInfo());
                            }
                        }
                    }
                    default ->
                            LOGGER.error("No case handled with the allowedActiveType ->%s".formatted(allowedActiveTypeForDocumentType));
                }

            } else {
                LOGGER.error("error getting source of document event in DocumentCreatedListener");
            }
        } catch (InvalidParameterException e) {
            LOGGER.error("Caught InvalidParameterException while inactivating document state ", e);
        } catch (DoesNotExistException e) {
            // ignore it
        }
    }
}
