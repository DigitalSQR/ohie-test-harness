package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface DocumentService {


    public DocumentEntity createDocument(DocumentEntity documentEntity,
                                         MultipartFile file,
                                         List<String> validationAllowedTypes, ContextInfo contextInfo) throws OperationFailedException, DataValidationErrorException, InvalidFileTypeException, IOException;


    public DocumentEntity getDocument(String documentId, ContextInfo contextInfo) throws DoesNotExistException;

    public DocumentEntity getDocumentByFileId(String fileId, ContextInfo contextInfo) throws DoesNotExistException;


    public List<DocumentEntity> getDocumentsByRefObjectUriAndRefObjectId(String refObjectUri,
                                                                         String refObjectId,
                                                                         ContextInfo contextInfo);


    public DocumentEntity changeOrder(String documentId, Integer orderId, ContextInfo contextInfo) throws DoesNotExistException;

    public DocumentEntity changeState(String documentId, String stateKey ,ContextInfo contextInfo) throws DoesNotExistException;

}
