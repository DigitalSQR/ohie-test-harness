package com.argusoft.path.tht.usermanagement.validator;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.models.dto.UpdatePasswordInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.springframework.data.domain.Page;

import java.util.*;

public class UserValidator {

    public static void validateUpdatePasswordInfoAgainstNullValues(UpdatePasswordInfo updatePasswordInfo) throws DataValidationErrorException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        ValidationUtils.validateRequired(updatePasswordInfo.getBase64TokenId(), "base64TokenId", errors);
        ValidationUtils.validateRequired(updatePasswordInfo.getBase64UserEmail(), "base64UserEmail", errors);
        ValidationUtils.validateRequired(updatePasswordInfo.getNewPassword(), "newPassword", errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
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
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntitys);
        }
    }

    //Validate Required
    private static void validateCommonRequired(UserEntity userEntity,
                                               List<ValidationResultInfo> errors) {
        //check the email required
        ValidationUtils
                .validateRequired(userEntity.getEmail(), "email", errors);
    }

    private static void validateCommonForeignKey(UserService userService, UserEntity userEntity,
                                                 List<ValidationResultInfo> errors,
                                                 ContextInfo contextInfo) {
        //validate Role foreignKey.
        Set<RoleEntity> roleEntitySet = new HashSet<>();
        userEntity.getRoles().stream().forEach(item -> {
            try {
                roleEntitySet.add(userService.getRoleById(item.getId(), contextInfo));
            } catch (DoesNotExistException | InvalidParameterException |
                     OperationFailedException ex) {
                String fieldName = "roles";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied for the role does not exists"));
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
            UserSearchFilter searchFilter = new UserSearchFilter();
            searchFilter.setEmail(userEntity.getEmail());
            Page<UserEntity> userEntities = userService
                    .searchUsers(
                            null,
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
        UserEntity originalEntity = null;
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
                        originalEntity = userService
                                .getUserById(userEntity.getId(),
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

                validateUpdateUser(errors,
                        userEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateUser(userService, errors, userEntity, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
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
                                           UserEntity originalEntity) {
        // required validation
        ValidationUtils.validateRequired(userEntity.getId(), "id", errors);
        //check the meta required
        if (userEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + " must be provided"));
        }
        // check meta version id
        else if (!userEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    "someone else has updated the user since you"
                            + " started updating, you might want to"
                            + " refresh your copy."));
        }
        // check not updatable fields
        validateNotUpdatable(errors, userEntity, originalEntity);
    }

    //validate not update
    private static void validateNotUpdatable(List<ValidationResultInfo> errors,
                                             UserEntity userEntity,
                                             UserEntity originalEntity) {
        //email can't be update
        ValidationUtils.validateNotUpdatable(userEntity.getEmail(), originalEntity.getEmail(), "email", errors);
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
                                "The id supplied to the create already exists"));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                // This is ok because created id should be unique
            }
        }
    }

    //Validation For :Id
    private static void validateUserEntityId(UserEntity userEntity,
                                             List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(userEntity.getId(), "id", errors);
    }

    //Validation For :Name
    private static void validateUserEntityName(UserEntity userEntity,
                                               List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(userEntity.getName(),
                "userName",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(userEntity.getName(),
                "userName",
                3,
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
}
