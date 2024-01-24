package com.argusoft.path.tht.fileservice.service.impl;


import com.argusoft.path.tht.fileservice.FileDetails;
import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.fileservice.MultipartFileTypeTesterPredicate;
import com.argusoft.path.tht.fileservice.service.FileService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.fileservice.constant.DocumentServiceConstants;
import com.argusoft.path.tht.fileservice.filter.DocumentCriteriaSearchFilter;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.fileservice.repository.DocumentRepository;
import com.argusoft.path.tht.fileservice.service.DocumentService;
import com.argusoft.path.tht.fileservice.validator.DocumentValidator;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    FileService fileService;

    @Autowired
    UserService userService;

    @Autowired
    DocumentRepository documentRepository;

    private static byte[] getFileContentByFileId(String fileId) throws OperationFailedException {
        byte[] fileContentByFilePathAndFileName;
        try {
            fileContentByFilePathAndFileName = FileService.getFileContentByFilePathAndFileName(null, fileId);
        } catch (IOException e) {
            throw new OperationFailedException("Exception occurred due to I/O Exception", e);
        }
        return fileContentByFilePathAndFileName;
    }

    @Override
    public DocumentEntity createDocument(DocumentEntity documentEntity, MultipartFile file,
                                         List<String> validationAllowedTypes, ContextInfo contextInfo) throws OperationFailedException, DataValidationErrorException, InvalidFileTypeException, DoesNotExistException, InvalidParameterException {

        //get FileType
        String fileType = getFileType(file);
        documentEntity.setFileType(fileType);

        //validate documentEntity
        DocumentValidator.validateDocumentEntity(Constant.CREATE_VALIDATION, documentEntity, contextInfo);

        //save file
        FileDetails fileDetails = null;
        try {
            fileDetails = storeFileAndGetFileDetails(file, validationAllowedTypes);
        } catch (InvalidFileTypeException e) {
            DocumentValidator.setErrorMessageForFileType(e);
        }

        //set FileId to DocumentEntity as it is UUID
        documentEntity.setFileId(fileDetails.getFileId());
        documentEntity.setName(fileDetails.getFileName());
        documentEntity.setState(DocumentServiceConstants.DOCUMENT_STATUS_ACTIVE);

        UserEntity user = null;
        try {
            user = userService.getPrincipalUser(contextInfo);
        } catch (InvalidParameterException e) {
            throw new OperationFailedException("InvalidParameterException while fetching principal User while saving document ",e);
        }
        documentEntity.setOwner(user);

        try {
            setOrderBasedOnRefObjIdAndUri(documentEntity, contextInfo);
        } catch (InvalidParameterException e) {
            throw new OperationFailedException("InvalidParameterException while saving document ",e);
        }

        DocumentEntity document = documentRepository.save(documentEntity);
        return document;
    }

    private void setOrderBasedOnRefObjIdAndUri(DocumentEntity documentEntity, ContextInfo contextInfo) throws InvalidParameterException {
        List<DocumentEntity> documentsByRefObjectUriAndRefObjectId = this.getDocumentsByRefObjectUriAndRefObjectId(documentEntity.getRefId(), documentEntity.getRefObjUri(), contextInfo);
        int size = documentsByRefObjectUriAndRefObjectId.size();
        documentEntity.setOrder(size + 1);
    }

    private FileDetails storeFileAndGetFileDetails(MultipartFile file, List<String> allowedFileTypes) throws OperationFailedException, InvalidFileTypeException {
        MultipartFileTypeTesterPredicate multipartFileTypeTesterPredicate = new MultipartFileTypeTesterPredicate(allowedFileTypes);
        FileDetails fileDetails = null;
        try {
            fileDetails = FileService.storeFile(file, multipartFileTypeTesterPredicate);
        } catch (IOException e) {
            throw new OperationFailedException("Operation Failed due to IOException", e);
        }
        return fileDetails;
    }

    private void validateRequired(DocumentEntity documentEntity, List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(documentEntity.getFileType(), "fileType", errors);
        ValidationUtils.validateRequired(documentEntity.getRefId(), "refId", errors);
        ValidationUtils.validateRequired(documentEntity.getRefObjUri(), "refObjUri", errors);
    }

    private String getFileType(MultipartFile file) throws OperationFailedException {
        try {
            return FileService.detectInputStreamTypeWithTika(file.getInputStream());
        } catch (IOException e) {
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
            throw new OperationFailedException("InvalidParameterException while getting document ",e);
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
    public DocumentEntity changeOrder(String documentId, Integer orderId, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, OperationFailedException {

        DocumentValidator.validateDocumentOrder(orderId);
        DocumentEntity document = this.getDocument(documentId, contextInfo);

        String refObjUri = document.getRefObjUri();
        String refId = document.getRefId();

        List<DocumentEntity> documentsByRefObjectUriAndRefObjectId = null;
        try {
            documentsByRefObjectUriAndRefObjectId = getDocumentsByRefObjectUriAndRefObjectId(refObjUri, refId, contextInfo);
        } catch (InvalidParameterException e) {
            //TODO add logger
        }

        TreeMap<Integer, DocumentEntity> documentEntityTreeMap = new TreeMap<>();
        for (int i = 1; i <= documentsByRefObjectUriAndRefObjectId.size(); i++) {
            documentEntityTreeMap.put(i, documentsByRefObjectUriAndRefObjectId.get(i--));
        }

        boolean foundTheInBetweenDocumentEntity = false;
        if (documentEntityTreeMap.containsKey(orderId)) {
            for (Map.Entry<Integer, DocumentEntity> integerDocumentEntityEntry : documentEntityTreeMap.entrySet()) {
                if (Objects.equals(integerDocumentEntityEntry.getKey(), orderId)) {
                    foundTheInBetweenDocumentEntity = true;
                }
                if (foundTheInBetweenDocumentEntity) {
                    DocumentEntity documentEntity = integerDocumentEntityEntry.getValue();
                    documentEntity.setOrder(integerDocumentEntityEntry.getKey() + 1);
                    documentRepository.save(documentEntity);
                }
            }
            document.setOrder(orderId);
        } else {
            document.setOrder(documentsByRefObjectUriAndRefObjectId.size() + 2);
        }

        documentRepository.save(document);
        return document;
    }

    private List<DocumentEntity> getDocumentsByRefObjectUriAndRefObjectId(String refObjUri, String refId, ContextInfo contextInfo) throws InvalidParameterException {
        DocumentCriteriaSearchFilter documentCriteriaSearchFilter = new DocumentCriteriaSearchFilter();
        documentCriteriaSearchFilter.setRefObjUri(refObjUri);
        documentCriteriaSearchFilter.setRefId(refId);

        return this.searchDocument(documentCriteriaSearchFilter, contextInfo);
    }

    @Override
    public DocumentEntity changeState(String documentId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, OperationFailedException {

        DocumentValidator.validateDocumentStateKey(stateKey);

        DocumentEntity document = this.getDocument(documentId, contextInfo);
        document.setState(stateKey);
        documentRepository.save(document);
        return document;
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
}