package com.argusoft.path.tht.testcasemanagement.service.impl;


import com.argusoft.path.tht.fileservice.FileDetails;
import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.fileservice.MultipartFileTypeTesterPredicate;
import com.argusoft.path.tht.fileservice.service.FileService;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testcasemanagement.constant.DocumentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.models.entity.DocumentEntity;
import com.argusoft.path.tht.testcasemanagement.repository.DocumentRepository;
import com.argusoft.path.tht.testcasemanagement.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    FileService fileService;

    @Autowired
    DocumentRepository documentRepository;

    @Override
    public DocumentEntity createDocument(DocumentEntity documentEntity, MultipartFile file,
                                         List<String> validationAllowedTypes, ContextInfo contextInfo) throws OperationFailedException, DataValidationErrorException, InvalidFileTypeException {

        //get FileType
        String fileType = getFileType(file);
        documentEntity.setFileType(fileType);

        //validate documentEntity
        List<ValidationResultInfo> errors = validateDocumentEntity(com.argusoft.path.tht.systemconfiguration.constant.Constant.CREATE_VALIDATION, documentEntity, contextInfo);
        if(ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)){
            throw new DataValidationErrorException("Error(s) occurred validating ",errors);
        }

        //save file
        FileDetails fileDetails = null;
        try {
            fileDetails = storeFileAndGetFileDetails(file, validationAllowedTypes);
        }catch (InvalidFileTypeException e){
            ValidationResultInfo error = new ValidationResultInfo();
            error.setMessage(e.getMessage());
            error.setLevel(ErrorLevel.ERROR);
            error.setStackTrace(Arrays.toString(e.getStackTrace()));
            error.setElement("fileType");
            throw new DataValidationErrorException(e.getMessage(),Collections.singletonList(error));
        }

        //set FileId to DocumentEntity as it is UUID
        documentEntity.setFileId(fileDetails.getFileId());
        documentEntity.setName(fileDetails.getFileName());
        documentEntity.setState(DocumentServiceConstants.DOCUMENT_STATUS_ACTIVE);
        setOrderBasedOnRefObjIdAndUri(documentEntity,contextInfo);

        DocumentEntity document = documentRepository.save(documentEntity);
        return document;
    }

    private void setOrderBasedOnRefObjIdAndUri(DocumentEntity documentEntity,ContextInfo contextInfo) {
        List<DocumentEntity> documentsByRefObjectUriAndRefObjectId = this.getDocumentsByRefObjectUriAndRefObjectId(documentEntity.getRefObjUri(), documentEntity.getRefId(), contextInfo);
        int size = documentsByRefObjectUriAndRefObjectId.size();
        documentEntity.setOrder(size+1);
    }

    private FileDetails storeFileAndGetFileDetails(MultipartFile file, List<String> allowedFileTypes) throws OperationFailedException, InvalidFileTypeException {
        MultipartFileTypeTesterPredicate multipartFileTypeTesterPredicate = new MultipartFileTypeTesterPredicate(allowedFileTypes);
        FileDetails fileDetails = null;
        try {
            fileDetails = FileService.storeFile(file, multipartFileTypeTesterPredicate);
        } catch (IOException e) {
            throw new OperationFailedException("Operation Failed due to IOException",e);
        }
        return fileDetails;
    }


    private List<ValidationResultInfo> validateDocumentEntity(String validationTypeKey,
                                                              DocumentEntity documentEntity,
                                                              ContextInfo contextInfo){
        List<ValidationResultInfo> errors = new ArrayList<>();
        validateRequired(documentEntity,errors);
        switch (validationTypeKey){
            case com.argusoft.path.tht.systemconfiguration.constant.Constant.CREATE_VALIDATION:
                //TODO define and add validation for create
                break;
            case com.argusoft.path.tht.systemconfiguration.constant.Constant.UPDATE_VALIDATION:
                //TODO define and add validation for update
                break;
        }
        return errors;
    }


    private void validateRequired(DocumentEntity documentEntity, List<ValidationResultInfo> errors){
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
    public DocumentEntity getDocument(String documentId, ContextInfo contextInfo) throws DoesNotExistException {
        Optional<DocumentEntity> documentById = documentRepository.findById(documentId);
        return documentById.orElseThrow(() -> new DoesNotExistException("DocumentEntity does not found with id : " + documentId));
    }

    @Override
    public DocumentEntity getDocumentByFileId(String fileId, ContextInfo contextInfo) throws DoesNotExistException {
        Optional<DocumentEntity> documentById = documentRepository.findDocumentByFileId(fileId);
        return documentById.orElseThrow(() -> new DoesNotExistException("DocumentEntity does not found with fileId : " + fileId));
    }

    @Override
    public List<DocumentEntity> getDocumentsByRefObjectUriAndRefObjectId(String refObjectUri, String refObjectId, ContextInfo contextInfo) {
        return documentRepository.findDocumentByRefObjectUriAndId(refObjectUri, refObjectId);
    }

    @Override
    public DocumentEntity changeOrder(String documentId, Integer orderId, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        if(orderId<0){
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setMessage("orderId cannot be lesser than 0");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setElement("orderId");
            errors.add(validationResultInfo);
            throw new DataValidationErrorException("Error(s) occured in validating ",errors);
        }

        DocumentEntity document = this.getDocument(documentId, contextInfo);

        String refObjUri = document.getRefObjUri();
        String refId = document.getRefId();

        List<DocumentEntity> documentsByRefObjectUriAndRefObjectId
                = this.getDocumentsByRefObjectUriAndRefObjectId(refObjUri, refId, contextInfo);


        TreeMap<Integer, DocumentEntity> documentEntityTreeMap = new TreeMap<>();
        for (int i = 1; i <= documentsByRefObjectUriAndRefObjectId.size(); i++) {
            documentEntityTreeMap.put(i,documentsByRefObjectUriAndRefObjectId.get(i--));
        }

        boolean foundTheInBetweenDocumentEntity = false;
        if(documentEntityTreeMap.containsKey(orderId)){
            for (Map.Entry<Integer, DocumentEntity> integerDocumentEntityEntry : documentEntityTreeMap.entrySet()) {
                if(Objects.equals(integerDocumentEntityEntry.getKey(), orderId)){
                    foundTheInBetweenDocumentEntity = true;
                }
                if(foundTheInBetweenDocumentEntity){
                    DocumentEntity documentEntity = integerDocumentEntityEntry.getValue();
                    documentEntity.setOrder(integerDocumentEntityEntry.getKey()+1);
                    documentRepository.save(documentEntity);
                }
            }
            document.setOrder(orderId);
        }
        else {
            document.setOrder(documentsByRefObjectUriAndRefObjectId.size()+2);
        }

        documentRepository.save(document);
        return document;
    }

    @Override
    public DocumentEntity changeState(String documentId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException {

        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        boolean contains = DocumentServiceConstants.documentStatuses.contains(stateKey);
        if(!contains){
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("stateKey");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("provided stateKey is not valid ");
            errors.add(validationResultInfo);
            throw new DataValidationErrorException("Validation Failed due to errors ",errors);
        }

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
    public ByteArrayResource getByteArrayResourceByFileId(String fileId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException {
        DocumentEntity documentByFileId = this.getDocumentByFileId(fileId, contextInfo);
        String documentFileId = documentByFileId.getFileId();
        byte[] fileContentByFilePathAndFileName = getFileContentByFileId(documentFileId);
        return new ByteArrayResource(fileContentByFilePathAndFileName);
    }

    private static byte[] getFileContentByFileId(String fileId) throws OperationFailedException {
        byte[] fileContentByFilePathAndFileName;
        try {
            fileContentByFilePathAndFileName = FileService.getFileContentByFilePathAndFileName(null, fileId);
        } catch (IOException e) {
            throw new OperationFailedException("Exception occurred due to I/O Exception",e);
        }
        return fileContentByFilePathAndFileName;
    }
}
