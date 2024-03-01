package com.argusoft.path.tht.fileservice.service.impl;


import com.argusoft.path.tht.fileservice.FileDetails;
import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.fileservice.MultipartFileTypeTesterPredicate;
import com.argusoft.path.tht.fileservice.constant.DocumentServiceConstants;
import com.argusoft.path.tht.fileservice.constant.DocumentUtil;
import com.argusoft.path.tht.fileservice.constant.FileType;
import com.argusoft.path.tht.fileservice.event.DocumentCreatedEvent;
import com.argusoft.path.tht.fileservice.filter.DocumentCriteriaSearchFilter;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.fileservice.repository.DocumentRepository;
import com.argusoft.path.tht.fileservice.service.DocumentService;
import com.argusoft.path.tht.fileservice.service.FileService;
import com.argusoft.path.tht.fileservice.validator.DocumentValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.Module;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class DocumentServiceImpl implements DocumentService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    FileService fileService;

    @Autowired
    UserService userService;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static byte[] getFileContentByFileId(String fileId) throws OperationFailedException {
        byte[] fileContentByFilePathAndFileName;
        try {
            fileContentByFilePathAndFileName = FileService.getFileContentByFilePathAndFileName(null, fileId);
        } catch (IOException e) {
            LOGGER.error(ValidateConstant.IO_EXCEPTION + DocumentServiceImpl.class.getSimpleName(), e);
            throw new OperationFailedException("Exception occurred due to I/O Exception", e);
        }
        return fileContentByFilePathAndFileName;
    }

    @Override
    public DocumentEntity createDocument(DocumentEntity documentEntity, MultipartFile file,
                                         ContextInfo contextInfo) throws OperationFailedException, DataValidationErrorException, InvalidFileTypeException, DoesNotExistException, InvalidParameterException {

        defaultValueCreateDocument(documentEntity, file, contextInfo);

        //validate documentEntity
        DocumentValidator.validateDocumentEntity(Constant.CREATE_VALIDATION, documentEntity, contextInfo);

        //get Document Types
        Set<FileType> allowedFileTypesForDocumentType = DocumentUtil.getAllowedFileTypesForDocumentType(documentEntity.getRefObjUri(), documentEntity.getDocumentType());

        //save file
        FileDetails fileDetails = null;
        try {
            fileDetails = storeFileAndGetFileDetails(file, allowedFileTypesForDocumentType);
        } catch (InvalidFileTypeException e) {
            LOGGER.error(ValidateConstant.INVALID_FILE_TYPE_EXCEPTION + DocumentServiceImpl.class.getSimpleName(), e);
            DocumentValidator.setErrorMessageForFileType(e);
        }

        //set FileId to DocumentEntity as it is UUID
        documentEntity.setFileId(fileDetails.getFileId());
        documentEntity.setName(fileDetails.getFileName());

        DocumentEntity document = documentRepository.saveAndFlush(documentEntity);
        eventPublisher.publishEvent(new DocumentCreatedEvent(document, contextInfo));
        return document;
    }

    private FileDetails storeFileAndGetFileDetails(MultipartFile file, Set<FileType> allowedFileTypes) throws OperationFailedException, InvalidFileTypeException {
        MultipartFileTypeTesterPredicate multipartFileTypeTesterPredicate = new MultipartFileTypeTesterPredicate(allowedFileTypes);
        FileDetails fileDetails = null;
        try {
            fileDetails = FileService.storeFile(file, multipartFileTypeTesterPredicate);
        } catch (IOException e) {
            LOGGER.error(ValidateConstant.IO_EXCEPTION + DocumentServiceImpl.class.getSimpleName(), e);
            throw new OperationFailedException("Operation Failed due to IOException", e);
        }
        return fileDetails;
    }

    private String getFileType(MultipartFile file) throws OperationFailedException {
        try {
            return FileService.detectInputStreamTypeWithTika(file.getInputStream());
        } catch (IOException e) {
            LOGGER.error(ValidateConstant.IO_EXCEPTION + DocumentServiceImpl.class.getSimpleName(), e);
            throw new OperationFailedException("File type validation failed due to an I/O error: " + e.getMessage());
        }
    }

    @Override
    public DocumentEntity getDocument(String documentId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException {

        DocumentCriteriaSearchFilter documentCriteriaSearchFilter = new DocumentCriteriaSearchFilter(documentId);
        try {
            List<DocumentEntity> documentEntities = this.searchDocument(documentCriteriaSearchFilter, contextInfo);
            return documentEntities.stream()
                    .findFirst()
                    .orElseThrow(() -> new DoesNotExistException("DocumentEntity does not found with id : " + documentId));

        } catch (InvalidParameterException e) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + DocumentServiceImpl.class.getSimpleName(), e);
            throw new OperationFailedException("InvalidParameterException while getting document ", e);
        }

    }

    @Override
    public Page<DocumentEntity> searchDocument(DocumentCriteriaSearchFilter exampleDocumentSearchFilter, Pageable pageable, ContextInfo contextInfo) throws InvalidParameterException {
        Specification<DocumentEntity> documentEntityExample = exampleDocumentSearchFilter.buildSpecification(contextInfo);
        return documentRepository.findAll(documentEntityExample, pageable);
    }

    @Override
    public List<DocumentEntity> searchDocument(DocumentCriteriaSearchFilter exampleDocumentSearchFilter, ContextInfo contextInfo) throws InvalidParameterException {
        Specification<DocumentEntity> documentEntityExample = exampleDocumentSearchFilter.buildSpecification(contextInfo);
        return documentRepository.findAll(documentEntityExample);
    }

    @Override
    public DocumentEntity changeRank(String documentId, Integer rankId, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, OperationFailedException {

        DocumentValidator.validateDocumentRank(rankId);
        DocumentEntity document = this.getDocument(documentId, contextInfo);

        String refObjUri = document.getRefObjUri();
        String refId = document.getRefId();

        List<DocumentEntity> documentsByRefObjectUriAndRefObjectId = null;
        try {
            documentsByRefObjectUriAndRefObjectId = getDocumentsByRefObjectUriAndRefObjectId(refObjUri, refId, contextInfo);
        } catch (InvalidParameterException e) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + DocumentServiceImpl.class.getSimpleName(), e);
            //TODO add logger
        }

        TreeMap<Integer, DocumentEntity> documentEntityTreeMap = new TreeMap<>();
        for (int i = 1; i <= documentsByRefObjectUriAndRefObjectId.size(); i++) {
            documentEntityTreeMap.put(i, documentsByRefObjectUriAndRefObjectId.get(i--));
        }

        boolean foundTheInBetweenDocumentEntity = false;
        if (documentEntityTreeMap.containsKey(rankId)) {
            for (Map.Entry<Integer, DocumentEntity> integerDocumentEntityEntry : documentEntityTreeMap.entrySet()) {
                if (Objects.equals(integerDocumentEntityEntry.getKey(), rankId)) {
                    foundTheInBetweenDocumentEntity = true;
                }
                if (foundTheInBetweenDocumentEntity) {
                    DocumentEntity documentEntity = integerDocumentEntityEntry.getValue();
                    documentEntity.setRank(integerDocumentEntityEntry.getKey() + 1);
                    documentRepository.saveAndFlush(documentEntity);
                }
            }
            document.setRank(rankId);
        } else {
            document.setRank(documentsByRefObjectUriAndRefObjectId.size() + 2);
        }

        documentRepository.saveAndFlush(document);
        return document;
    }
    private List<DocumentEntity> getDocumentsByRefObjectUriAndRefObjectId(String refObjUri, String refId, ContextInfo contextInfo) throws InvalidParameterException {
        DocumentCriteriaSearchFilter documentCriteriaSearchFilter = new DocumentCriteriaSearchFilter();
        documentCriteriaSearchFilter.setRefObjUri(refObjUri);
        documentCriteriaSearchFilter.setRefId(refId);

        return this.searchDocument(documentCriteriaSearchFilter, contextInfo);
    }

    @Override
    public DocumentEntity changeState(String documentID, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        ValidationUtils.statusPresent(DocumentServiceConstants.DOCUMENT_STATUS, stateKey, errors);

        DocumentEntity documentEntity = this.getDocument(documentID, contextInfo);

        //validate transition
        ValidationUtils.transitionValid(DocumentServiceConstants.DOCUMENT_STATUS_MAP, documentEntity.getState(), stateKey, errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    errors);
        }

        documentEntity.setState(stateKey);
        documentEntity = documentRepository.saveAndFlush(documentEntity);

        return documentEntity;
    }

    @Override
    public ByteArrayResource getByteArrayResourceByDocumentId(String documentId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException {
        DocumentEntity document = this.getDocument(documentId, contextInfo);
        String fileId = document.getFileId();
        byte[] fileContentByFilePathAndFileName = getFileContentByFileId(fileId);
        return new ByteArrayResource(fileContentByFilePathAndFileName);
    }

    @Override
    public ByteArrayResource getByteArrayResourceByFileId(String fileId, ContextInfo contextInfo) throws OperationFailedException {
        List<DocumentEntity> documentEntities = new ArrayList<>();
        try {
            documentEntities = getDocumentsByFileId(fileId, contextInfo);
        } catch (InvalidParameterException e) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + DocumentServiceImpl.class.getSimpleName(), e);
            throw new OperationFailedException("Error fetching document by fileId", e);
            //ADD LOGGER
        }

        DocumentEntity documentByFileId = documentEntities.get(0);
        String documentFileId = documentByFileId.getFileId();
        byte[] fileContentByFilePathAndFileName = getFileContentByFileId(documentFileId);
        return new ByteArrayResource(fileContentByFilePathAndFileName);
    }

    private List<DocumentEntity> getDocumentsByFileId(String fileId, ContextInfo contextInfo) throws InvalidParameterException {
        DocumentCriteriaSearchFilter documentCriteriaSearchFilter = new DocumentCriteriaSearchFilter();
        documentCriteriaSearchFilter.setFileId(fileId);
        List<DocumentEntity> documentEntities;
        documentEntities = this.searchDocument(documentCriteriaSearchFilter, contextInfo);
        return documentEntities;
    }

    private void defaultValueCreateDocument(DocumentEntity documentEntity, MultipartFile file, ContextInfo contextInfo) throws OperationFailedException, DoesNotExistException, InvalidParameterException {

        String fileType = getFileType(file);
        documentEntity.setFileType(fileType);

        documentEntity.setState(DocumentServiceConstants.DOCUMENT_STATUS_ACTIVE);
        documentEntity.setOwner(userService.getPrincipalUser(contextInfo));
        documentEntity.setRank(1);

        contextInfo.setModule(Module.DOCUMENT);
        DocumentCriteriaSearchFilter searchFilter = new DocumentCriteriaSearchFilter();
        searchFilter.setRefObjUri(documentEntity.getRefObjUri());
        searchFilter.setRefId(documentEntity.getRefId());
        List<DocumentEntity> documents = this.searchDocument(searchFilter, Constant.SINGLE_PAGE_SORT_BY_RANK, contextInfo).getContent();
        if (!documents.isEmpty()) {
            documentEntity.setRank(documents.get(0).getRank() + 1);
        }
        contextInfo.setModule(Module.UI);
    }
}