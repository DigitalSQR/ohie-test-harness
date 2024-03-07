package com.argusoft.path.tht.fileservice.validator;

import com.argusoft.path.tht.fileservice.InvalidFileTypeException;
import com.argusoft.path.tht.fileservice.constant.DocumentTypeConstants;
import com.argusoft.path.tht.fileservice.constant.DocumentUtil;
import com.argusoft.path.tht.fileservice.models.entity.DocumentEntity;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.testprocessmanagement.validator.RefObjectUriAndRefIdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DocumentValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(DocumentValidator.class);

    private static RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidator;

    @Autowired
    public void setRefObjectUriAndRefIdValidator(RefObjectUriAndRefIdValidator refObjectUriAndRefIdValidatorIdValidator) {
        DocumentValidator.refObjectUriAndRefIdValidator = refObjectUriAndRefIdValidatorIdValidator;
    }

    public static void validateDocumentEntity(String validationTypeKey,
                                              DocumentEntity documentEntity,
                                              ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, OperationFailedException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        validateRequired(documentEntity, errors);

        refObjectUriAndRefIdValidator.refObjectUriAndRefIdValidation(documentEntity.getRefObjUri(), documentEntity.getRefId(), contextInfo, errors);

        isDocumentTypeValid(documentEntity, errors);

        switch (validationTypeKey) {
            case Constant.CREATE_VALIDATION:
                //TODO define and add validation for create
                break;
            case Constant.UPDATE_VALIDATION:
                //TODO define and add validation for update
                break;
            default:
                LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + DocumentValidator.class.getSimpleName());
                throw new InvalidParameterException(ValidateConstant.INVALID_VALIDATION_TYPE_KEY);
        }

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            LOGGER.error(ValidateConstant.DATA_VALIDATION_EXCEPTION + DocumentValidator.class.getSimpleName());
            throw new DataValidationErrorException(ValidateConstant.ERRORS, errors);
        }
    }

    public static void validateDocumentRank(Integer rankId) throws DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        if (rankId < 0) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setMessage("rankId cannot be lesser than 0");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setElement("rankId");
            errors.add(validationResultInfo);
            LOGGER.error(ValidateConstant.DATA_VALIDATION_EXCEPTION + DocumentValidator.class.getSimpleName());
            throw new DataValidationErrorException(ValidateConstant.ERRORS, errors);
        }
    }

    private static void validateRequired(DocumentEntity documentEntity, List<ValidationResultInfo> errors) {
        ValidationUtils.validateRequired(documentEntity.getFileType(), "fileType", errors);
        ValidationUtils.validateRequired(documentEntity.getRefId(), "refId", errors);
        ValidationUtils.validateRequired(documentEntity.getRefObjUri(), "refObjUri", errors);
        ValidationUtils.validateRequired(documentEntity.getState(), "state", errors);
        ValidationUtils.validateRequired(documentEntity.getRank(), "rank", errors);
        ValidationUtils.validateRequired(documentEntity.getOwner(), "owner", errors);
        ValidationUtils.validateRequired(documentEntity.getDocumentType(), "documentType", errors);
    }

    public static void setErrorMessageForFileType(InvalidFileTypeException e) throws DataValidationErrorException {
        ValidationResultInfo error = new ValidationResultInfo();
        error.setMessage(e.getMessage());
        error.setLevel(ErrorLevel.ERROR);
        error.setStackTrace(Arrays.toString(e.getStackTrace()));
        error.setElement("fileType");
        LOGGER.error(ValidateConstant.DATA_VALIDATION_EXCEPTION + DocumentValidator.class.getSimpleName());
        throw new DataValidationErrorException(e.getMessage(), Collections.singletonList(error));
    }

    private static void isDocumentTypeValid(DocumentEntity documentEntity, List<ValidationResultInfo> errors) {
        boolean documentTypeValidForEntityRefObjectUri = DocumentUtil.isDocumentTypeValidForEntityRefObjectUri(documentEntity.getDocumentType(), documentEntity.getRefObjUri());
        if (!documentTypeValidForEntityRefObjectUri) {
            String fieldName = "documentType";
            LOGGER.error("Invalid document type for the given refObjUri - %s".formatted(documentEntity.getRefObjUri()));
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " is invalid for the given refObjUri"));
        }
    }

}