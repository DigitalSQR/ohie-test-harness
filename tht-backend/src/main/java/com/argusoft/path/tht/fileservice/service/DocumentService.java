package com.argusoft.path.tht.fileservice.service;

import com.argusoft.path.tht.fileservice.filter.DocumentCriteriaSearchFilter;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author hardik
 */
@Service
public interface DocumentService {


    /**
     * creates new Document Record
     *
     * @param documentEntity document related data
     * @param file           multipart file that should be saved
     * @param contextInfo    contextInfo
     * @return
     * @throws OperationFailedException     When Any Unexpected Exception occures
     * @throws DataValidationErrorException When Data Validation Failes
     * @throws InvalidFileTypeException     When File Type is invalid
     */
    public DocumentEntity createDocument(DocumentEntity documentEntity,
                                         MultipartFile file,
                                         ContextInfo contextInfo) throws OperationFailedException, DataValidationErrorException, InvalidFileTypeException, DoesNotExistException, InvalidParameterException;


    /**
     * gets Document By docuemntId
     *
     * @param documentId  Id of document to be retrieved
     * @param contextInfo ContextInfo
     * @return DocumentEntity
     * @throws DoesNotExistException when document does not exists for that specific id
     */
    public DocumentEntity getDocument(String documentId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException;


    public Page<DocumentEntity> searchDocument(DocumentCriteriaSearchFilter exampleDocumentSearchFilter, Pageable pageable, ContextInfo contextInfo) throws InvalidParameterException;


    public List<DocumentEntity> searchDocument(DocumentCriteriaSearchFilter exampleDocumentSearchFilter, ContextInfo contextInfo) throws InvalidParameterException;


    /**
     * change rank of document with id and giving expected rank
     *
     * @param documentId  id of document
     * @param rankId     expected rank Id
     * @param contextInfo ContextInfo
     * @return update documentEntity
     * @throws DoesNotExistException        when document Does not exist for that id
     * @throws DataValidationErrorException when validation fails
     */
    public DocumentEntity changeRank(String documentId, Integer rankId, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, OperationFailedException;

    /**
     * change state of document with id and giving the expected state
     *
     * @param documentId  id of the document
     * @param stateKey    expected statekey
     * @param contextInfo ContextInfo
     * @return DocumentEntity
     * @throws DoesNotExistException        when document does not exists for that id
     * @throws DataValidationErrorException when validation fails
     */
    public DocumentEntity changeState(String documentId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;


    /**
     * get ByteArrayResource to send to the user by documentId
     *
     * @param documentId  documentId in which file is associated
     * @param contextInfo ContextInfo
     * @return ByteArrayResource of file
     */
    public ByteArrayResource getByteArrayResourceByDocumentId(String documentId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException;

    /**
     * get ByteArrayResource to send to the user by fileId
     *
     * @param fileId      fileId
     * @param contextInfo ContextInfo
     * @return ByteArrayResource of file
     */
    public ByteArrayResource getByteArrayResourceByFileId(String fileId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException;

}
