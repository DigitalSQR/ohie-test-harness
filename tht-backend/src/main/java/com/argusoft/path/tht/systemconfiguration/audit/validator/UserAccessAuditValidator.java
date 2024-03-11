package com.argusoft.path.tht.systemconfiguration.audit.validator;

import com.argusoft.path.tht.systemconfiguration.audit.entity.UserAccessAuditInfo;
import com.argusoft.path.tht.systemconfiguration.audit.service.UserAccessAuditService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAccessAuditValidator {

    @Autowired
    UserAccessAuditService userAccessAuditService;

    public UserAccessAuditInfo createUserAccessAudit(UserAccessAuditInfo userAccessAuditInfo,
                                                     ContextInfo contextInfo) throws InvalidParameterException, DataValidationErrorException {
        if (userAccessAuditInfo == null) {
            throw new InvalidParameterException("UserAccessAudit is missing");
        }

        List<ValidationResultInfo> validationResultInfos
                = validateUserAccessAudit(Constant.CREATE_VALIDATION,
                userAccessAuditInfo,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultInfos, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultInfos);
        }

        return this.userAccessAuditService.createUserAccessAudit(userAccessAuditInfo, contextInfo);

    }


    public List<ValidationResultInfo> validateUserAccessAudit(String validationTypeKey,
                                                              UserAccessAuditInfo userAccessAuditInfo,
                                                              ContextInfo contextInfo) throws InvalidParameterException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        UserAccessAuditInfo originalInfo = null;
        trimUserAccessAudit(userAccessAuditInfo);

        // check Common Required
        this.validateCommonRequired(userAccessAuditInfo, errors);

        // check Common ForeignKey
        this.validateCommonForeignKey(userAccessAuditInfo, errors, contextInfo);

        // check Common Unique
        this.validateCommonUnique(userAccessAuditInfo,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (userAccessAuditInfo.getId() != null) {
                    try {
                        originalInfo = this.userAccessAuditService
                                .getUserAccessAuditById(userAccessAuditInfo.getId(),
                                        contextInfo);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR,
                                        "The id supplied to the update does not "
                                                + "exists"));
                    }
                }

                if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
                    return errors;
                }

                this.validateUpdateUserAccessAudit(errors,
                        userAccessAuditInfo,
                        originalInfo);
                break;
            case Constant.CREATE_VALIDATION:
//                this.validateCreateUserAccessAudit(errors, userAccessAuditInfo);   if id supplied than error
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        return errors;

    }

    public static void trimUserAccessAudit(UserAccessAuditInfo userAccessAuditInfo){
        if (userAccessAuditInfo.getId() != null) {
            userAccessAuditInfo.setId(userAccessAuditInfo.getId().trim());
        }
        if (userAccessAuditInfo.getUsername() != null) {
            userAccessAuditInfo.setUsername(userAccessAuditInfo.getUsername().trim());
        }
        if (userAccessAuditInfo.getLocation() != null) {
            userAccessAuditInfo.setLocation(userAccessAuditInfo.getLocation().trim());
        }
        if (userAccessAuditInfo.getMessage() != null) {
            userAccessAuditInfo.setMessage(userAccessAuditInfo.getMessage().trim());
        }
        if (userAccessAuditInfo.getIpAddress() != null) {
            userAccessAuditInfo.setIpAddress(userAccessAuditInfo.getIpAddress().trim());
        }
        if (userAccessAuditInfo.getTypeId() != null) {
            userAccessAuditInfo.setTypeId(userAccessAuditInfo.getTypeId().trim());
        }
        if (userAccessAuditInfo.getStateId() != null) {
            userAccessAuditInfo.setStateId(userAccessAuditInfo.getStateId().trim());
        }
    }

    protected void validateCommonRequired(UserAccessAuditInfo userAccessAuditInfo,
                                          List<ValidationResultInfo> errors) {
        //check the timestamp required
        ValidationUtils
                .validateRequired(userAccessAuditInfo.getTimestamp(), "timestamp", errors);
        //check the ipAddress required
        ValidationUtils
                .validateRequired(userAccessAuditInfo.getIpAddress(), "ipAddress", errors);
        //check the typeId required
        ValidationUtils
                .validateRequired(userAccessAuditInfo.getTypeId(), "typeId", errors);
        //check the stateId required
        ValidationUtils
                .validateRequired(userAccessAuditInfo.getStateId(), "stateId", errors);
    }

    protected void validateCommonForeignKey(UserAccessAuditInfo userAccessAuditInfo,
                                            List<ValidationResultInfo> errors,
                                            ContextInfo contextInfo){
        //no validation
    }

    protected void validateCommonUnique(UserAccessAuditInfo userAccessAuditInfo,
                                        String validationTypeKey,
                                        List<ValidationResultInfo> errors,
                                        ContextInfo contextInfo){
        //check for unique fields
    }

    protected void validateUpdateUserAccessAudit(List<ValidationResultInfo> errors,
                                                 UserAccessAuditInfo userAccessAuditInfo,
                                                 UserAccessAuditInfo originalInfo) {
        // required validation
        ValidationUtils.validateRequired(userAccessAuditInfo.getId(), "id", errors);

        // check not updatable fields
//        this.validateNotUpdatable(errors, userAccessAuditInfo, originalInfo);
    }


}
