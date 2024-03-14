package com.argusoft.path.tht.usermanagement.validator;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.Module;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.EncryptDecrypt;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.filter.UserSearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.models.dto.UpdatePasswordInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.*;

public class UserValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserValidator.class);


    private UserValidator() {
    }

    public static void validateUpdatePasswordInfoAgainstNullValues(UpdatePasswordInfo updatePasswordInfo) throws DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        ValidationUtils.validateRequired(updatePasswordInfo.getBase64TokenId(), "base64TokenId", errors);
        ValidationUtils.validateRequired(updatePasswordInfo.getBase64UserEmail(), "base64UserEmail", errors);
        ValidationUtils.validateRequired(updatePasswordInfo.getNewPassword(), "newPassword", errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, UserValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    errors);
        }
    }

    public static void validateCreateUpdateUser(UserService userService, String validationTypeKey, UserEntity userEntity, ContextInfo contextInfo) throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntitys
                = validateUser(userService, validationTypeKey,
                userEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, UserValidator.class.getSimpleName());
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    validationResultEntitys);
        }
    }

    //Validate Required
    private static void validateCommonRequired(UserEntity userEntity,
                                               List<ValidationResultInfo> errors) {
        //check the email required
        ValidationUtils
                .validateRequired(userEntity.getEmail(), "email", errors);
        //check the name required
        ValidationUtils
                .validateRequired(userEntity.getName(), "name", errors);
        //check the state required
        ValidationUtils
                .validateRequired(userEntity.getState(), "state", errors);
    }

    private static void validateCommonForeignKey(UserService userService, UserEntity userEntity,
                                                 List<ValidationResultInfo> errors,
                                                 ContextInfo contextInfo) {
        //validate Role foreignKey.
        Set<RoleEntity> roleEntitySet = new HashSet<>();
        userEntity.getRoles().stream().forEach(item -> {
            try {
                roleEntitySet.add(userService.getRoleById(item.getId(), contextInfo));
            } catch (DoesNotExistException | InvalidParameterException
                     | OperationFailedException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + UserValidator.class.getSimpleName(), ex);
                String fieldName = "roles";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
            }
        });
        userEntity.setRoles(roleEntitySet);
    }

    private static void validateCommonUnique(UserService userService, UserEntity userEntity,
                                             String validationTypeKey,
                                             List<ValidationResultInfo> errors,
                                             ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || userEntity.getId() != null)
                && userEntity.getEmail() != null) {
            UserSearchCriteriaFilter searchFilter = new UserSearchCriteriaFilter();
            searchFilter.setEmail(userEntity.getEmail());

            Page<UserEntity> userEntities = userService
                    .searchUsers(
                            searchFilter,
                            Constant.TWO_VALUE_PAGE,
                            contextInfo);

            // if info found with same email and username than and not current id
            boolean flag
                    = userEntities.stream().anyMatch(u -> (validationTypeKey.equals(Constant.CREATE_VALIDATION)
                    || !u.getId().equals(userEntity.getId()))
            );
            if (flag) {
                String fieldName = "Email";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "Given User with same Email already exists."));
            }

        }
    }

    public static List<ValidationResultInfo> validateUser(UserService userService,
                                                          String validationTypeKey,
                                                          UserEntity userEntity,
                                                          ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        trimUser(userEntity);

        // check Common Required
        validateCommonRequired(userEntity,
                errors);

        // check Common ForeignKey
        validateCommonForeignKey(userService,
                userEntity,
                errors,
                contextInfo);

        // check Common Unique
        validateCommonUnique(userService,
                userEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (userEntity.getId() != null) {
                    try {
                        UserEntity originalEntity = userService.getUserById(userEntity.getId(), contextInfo);
                        validateUpdateUser(errors, userEntity, originalEntity, contextInfo);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + UserValidator.class.getSimpleName(), ex);
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR,
                                        ValidateConstant.ID_SUPPLIED + "update" + ValidateConstant.DOES_NOT_EXIST));
                        return errors;
                    }
                }
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateUser(userService, errors, userEntity, contextInfo);
                break;
            default:
                LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, UserValidator.class.getSimpleName());
                throw new InvalidParameterException(ValidateConstant.INVALID_VALIDATION_TYPE_KEY);
        }

        // For : Id
        validateUserEntityId(userEntity,
                errors);
        // For :Name
        validateUserEntityName(userEntity,
                errors);
        // For :Email
        validateUserEntityEmail(userEntity,
                errors);
        // For :Password
        validateUserEntityPassword(userEntity,
                errors);
        // For :Role
        validateUserEntityRoles(userEntity,
                errors);
        // For : CompanyName
        validateUserEntityCompanyName(userEntity, errors);
        return errors;
    }

    //trim all User field
    private static void trimUser(UserEntity userEntity) {
        if (userEntity.getId() != null) {
            userEntity.setId(userEntity.getId().trim());
        }
        if (userEntity.getEmail() != null) {
            userEntity.setEmail(userEntity.getEmail().trim());
        }
    }

    //validate update
    private static void validateUpdateUser(List<ValidationResultInfo> errors,
                                           UserEntity userEntity,
                                           UserEntity originalEntity,
                                           ContextInfo contextInfo) {
        // required validation
        ValidationUtils.validateRequired(userEntity.getId(), "id", errors);
        //check the meta required
        if (userEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + ValidateConstant.MUST_PROVIDED));
        } // check meta version id
        else if (!userEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    ValidateConstant.SOMEONE_UPDATED + "user" + ValidateConstant.REFRESH_COPY));
        }
        // check not updatable fields
        validateNotUpdatable(errors, userEntity, originalEntity, contextInfo);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             UserEntity userEntity,
                                             UserEntity originalEntity,
                                             ContextInfo contextInfo) {

        // state can't be updated
        ValidationUtils.validateNotUpdatable(userEntity.getState(), originalEntity.getState(), "state", errors);
        ValidationUtils.validateNotUpdatable(userEntity.getEmail(), originalEntity.getEmail(), "email", errors);
        if (contextInfo.getModule() != Module.RESET_PASSWORD && contextInfo.getModule() != Module.FORGOT_PASSWORD) {
            userEntity.setPassword(originalEntity.getPassword());
        }
        if (!contextInfo.isAdmin()) {
            ValidationUtils.validateNotUpdatable(userEntity.getRoles(), originalEntity.getRoles(), "role", errors);
        }
    }

    //validate create
    private static void validateCreateUser(UserService userService,
                                           List<ValidationResultInfo> errors,
                                           UserEntity userEntity,
                                           ContextInfo contextInfo) {
        if (userEntity.getId() != null) {
            try {
                userService.getUserById(userEntity.getId(),
                        contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + "create" + ValidateConstant.ALREADY_EXIST));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + UserValidator.class.getSimpleName(), ex);
                // This is ok because created id should be unique
            }
        }
    }

    //Validation For :Id
    private static void validateUserEntityId(UserEntity userEntity,
                                             List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(userEntity.getId(),
                "id",
                0,
                255,
                errors);
    }

    //Validation For :Name
    private static void validateUserEntityName(UserEntity userEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(userEntity.getName(),
                "name",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(userEntity.getName(),
                "name",
                0,
                1000,
                errors);
    }

    //Validation For :Email
    private static void validateUserEntityEmail(UserEntity userEntity,
                                                List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(userEntity.getEmail(),
                "email",
                UserServiceConstants.EMAIL_REGEX,
                "Given email is invalid.",
                errors);
        ValidationUtils.validateLength(userEntity.getEmail(),
                "email",
                0,
                255,
                errors);
    }

    //Validation For :Password
    private static void validateUserEntityPassword(UserEntity userEntity,
                                                   List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(userEntity.getPassword(),
                "password",
                6,
                255,
                errors);
    }

    //Validation For :Roles
    private static void validateUserEntityRoles(UserEntity userEntity,
                                                List<ValidationResultInfo> errors) {
        ValidationUtils.validateCollectionSize(userEntity.getRoles(),
                "roles",
                1,
                null,
                errors);
    }

    //Validation For :Company Name
    private static void validateUserEntityCompanyName(UserEntity userEntity,
                                                      List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(userEntity.getCompanyName(),
                "company name",
                0,
                255,
                errors);
    }

    //validate one admin should active all time
    public static void oneAdminShouldActiveValidation(UserService userService, List<ValidationResultInfo> errors, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        UserSearchCriteriaFilter userSearchCriteriaFilter = new UserSearchCriteriaFilter();
        userSearchCriteriaFilter.setRole(UserServiceConstants.ROLE_ID_ADMIN);
        userSearchCriteriaFilter.setState(Collections.singletonList(UserServiceConstants.USER_STATUS_ACTIVE));
        List<UserEntity> userList = userService.searchUsers(userSearchCriteriaFilter, contextInfo);

        if (userList.size() == 1) {
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("One of the admin must be active");
            validationResultInfo.setElement("state");
            errors.add(validationResultInfo);
        }
    }

    public static void validatePasswords(String oldPassword, String newPassword, String dbPassword, List<ValidationResultInfo> errors) {
        if (newPassword.isEmpty()) {
            errors.add(new ValidationResultInfo("New password", ErrorLevel.ERROR, "New password cannot be empty"));
        }
        if (!EncryptDecrypt.checkRawString(oldPassword, dbPassword)) {
            errors.add(new ValidationResultInfo("Old password", ErrorLevel.ERROR, "Old password is incorrect."));
        }
        if (Objects.equals(oldPassword, newPassword)) {
            errors.add(new ValidationResultInfo("New password", ErrorLevel.ERROR, "Old password can not be usable as new password."));
        }
    }

    public static void validateChangeState(UserEntity userEntity, UserService userService, List<ValidationResultInfo> errors, String stateKey, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {

        //validate for one admin should active all time
        if (stateKey.equals(UserServiceConstants.USER_STATUS_INACTIVE) && userEntity.getRoles().stream().anyMatch(role -> role.getId().equals(UserServiceConstants.ROLE_ID_ADMIN))) {
            UserValidator.oneAdminShouldActiveValidation(userService, errors, contextInfo);
        }
    }

}
