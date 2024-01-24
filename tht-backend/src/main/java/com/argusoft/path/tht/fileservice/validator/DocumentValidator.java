package com.argusoft.path.tht.fileservice.validator;

import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.fileservice.constant.DocumentServiceConstants;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.testprocessmanagement.validator.RefObjectUriAndRefIdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DocumentValidator {

    private static RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidator;

    @Autowired
    public void setRefObjectUriAndRefIdValidator(RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidatorIdValidator) {
        DocumentValidator.refObjectUriAndRefIdValidator = refObjectUriAndRefIdValidatorIdValidator;
    }

    public static void validateDocumentEntity(String validationTypeKey,
                                              DocumentEntity documentEntity,
                                              ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, OperationFailedException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        refObjectUriAndRefIdValidator.refObjectUriAndRefIdValidation(documentEntity.getRefObjUri(), documentEntity.getRefId(), contextInfo, errors);

        validateRequired(documentEntity, errors);
        switch (validationTypeKey) {
            case Constant.CREATE_VALIDATION:
                //TODO define and add validation for create
                break;
            case Constant.UPDATE_VALIDATION:
                //TODO define and add validation for update
                break;
        }

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException("Error(s) occurred validating ", errors);
        }
    }

    public static void validateDocumentStateKey(String stateKey) throws DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        boolean contains = DocumentServiceConstants.documentStatuses.contains(stateKey);
        if (!contains) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("stateKey");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("provided stateKey is not valid ");
            errors.add(validationResultInfo);
            throw new DataValidationErrorException("Validation Failed due to errors ", errors);
        }
    }

    public static void validateDocumentOrder(Integer orderId) throws DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        if (orderId < 0) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setMessage("orderId cannot be lesser than 0");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setElement("orderId");
            errors.add(validationResultInfo);
            throw new DataValidationErrorException("Error(s) occured in validating ", errors);
        }
    }

    private static void validateRequired(DocumentEntity documentEntity, List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(documentEntity.getFileType(), "fileType", errors);
        ValidationUtils.validateRequired(documentEntity.getRefId(), "refId", errors);
        ValidationUtils.validateRequired(documentEntity.getRefObjUri(), "refObjUri", errors);
    }


    public static void setErrorMessageForFileType(InvalidFileTypeException e) throws DataValidationErrorException {
        ValidationResultInfo error = new ValidationResultInfo();
        error.setMessage(e.getMessage());
        error.setLevel(ErrorLevel.ERROR);
        error.setStackTrace(Arrays.toString(e.getStackTrace()));
        error.setElement("fileType");
        throw new DataValidationErrorException(e.getMessage(), Collections.singletonList(error));
    }
}