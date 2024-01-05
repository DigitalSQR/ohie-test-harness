package com.argusoft.path.tht.testcasemanagement.service;

import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author hardik
 */
@Service
public interface DocumentService {


    /**
     *
     * creates new Document Record
     *
     * @param documentEntity document related data
     * @param file multipart file that should be saved
     * @param validationAllowedTypes allowed Types to validate file with that type
     * @param contextInfo contextInfo
     * @return
     * @throws OperationFailedException When Any Unexpected Exception occures
     * @throws DataValidationErrorException When Data Validation Failes
     * @throws InvalidFileTypeException When File Type is invalid
     */
    public DocumentEntity createDocument(DocumentEntity documentEntity,
                                         MultipartFile file,
                                         List<String> validationAllowedTypes, ContextInfo contextInfo) throws OperationFailedException, DataValidationErrorException, InvalidFileTypeException;


    /**
     * gets Document By docuemntId
     *
     * @param documentId Id of document to be retrieved
     * @param contextInfo ContextInfo
     * @return DocumentEntity
     * @throws DoesNotExistException when document does not exists for that specific id
     */
    public DocumentEntity getDocument(String documentId, ContextInfo contextInfo) throws DoesNotExistException;

    /**
     *
     * gets Document By associated fileId
     *
     * @param fileId Id of file
     * @param contextInfo ContextInfo
     * @return DocumentEntity
     * @throws DoesNotExistException when document does not exists with that associated file Id
     */
    public DocumentEntity getDocumentByFileId(String fileId, ContextInfo contextInfo) throws DoesNotExistException;


    /**
     *
     * gets Document By RefObjectUri and RefObjectId
     *
     * @param refObjectUri RefObjectUri for that document should be fetched
     * @param refObjectId RefObjectId for that document should be fetched
     * @param contextInfo ContextInfo
     * @return list of documentEntity
     */
    public List<DocumentEntity> getDocumentsByRefObjectUriAndRefObjectId(String refObjectUri,
                                                                         String refObjectId,
                                                                         ContextInfo contextInfo);


    /**
     *
     * change order of document with id and giving expected order
     *
     * @param documentId id of document
     * @param orderId expected order Id
     * @param contextInfo ContextInfo
     * @return update documentEntity
     * @throws DoesNotExistException when document Does not exist for that id
     * @throws DataValidationErrorException when validation fails
     */
    public DocumentEntity changeOrder(String documentId, Integer orderId, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException;

    /**
     *
     * change state of document with id and giving the expected state
     *
     * @param documentId id of the document
     * @param stateKey expected statekey
     * @param contextInfo ContextInfo
     * @return DocumentEntity
     * @throws DoesNotExistException when document does not exists for that id
     * @throws DataValidationErrorException when validation fails
     */
    public DocumentEntity changeState(String documentId, String stateKey ,ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException;


    /**
     * get ByteArrayResource to send to the user by documentId
     *
     * @param documentId documentId in which file is associated
     * @param contextInfo ContextInfo
     * @return ByteArrayResource of file
     */
    public ByteArrayResource getByteArrayResourceByDocumentId(String documentId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException;

    /**
     * get ByteArrayResource to send to the user by fileId
     *
     * @param fileId fileId
     * @param contextInfo ContextInfo
     * @return ByteArrayResource of file
     */
    public ByteArrayResource getByteArrayResourceByFileId(String fileId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException;

}
