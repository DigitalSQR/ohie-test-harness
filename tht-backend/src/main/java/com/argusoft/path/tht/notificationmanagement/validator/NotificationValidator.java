package com.argusoft.path.tht.notificationmanagement.validator;

import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.notificationmanagement.service.NotificationService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Validation methods for Notification
 *
 * @author Ali
 */
public class NotificationValidator {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationValidator.class);

    private NotificationValidator() {
    }

    public static void validateCreateUpdateNotification(String validationTypeKey, NotificationService notificationService, UserService userService, NotificationEntity notificationEntity, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException {
        List<ValidationResultInfo> validationResultEntities = validateNotification(validationTypeKey, notificationEntity, notificationService, userService, contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntities, ErrorLevel.ERROR)) {
            LOGGER.error("{}{}", ValidateConstant.DATA_VALIDATION_EXCEPTION, NotificationValidator.class.getSimpleName());
            throw new DataValidationErrorException(ValidateConstant.ERRORS, validationResultEntities);
        }
    }

    public static List<ValidationResultInfo> validateNotification(
            String validationTypeKey,
            NotificationEntity notificationEntity,
            NotificationService notificationService,
            UserService userService,
            ContextInfo contextInfo)
            throws InvalidParameterException {

        if (!StringUtils.hasLength(validationTypeKey)) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, NotificationValidator.class.getSimpleName());
            throw new InvalidParameterException(ValidateConstant.MISSING_VALIDATION_TYPE_KEY);
        }
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        trimNotification(notificationEntity);

        // check Common Required
        validateCommonRequired(notificationEntity, errors);

        validateCommonForeignKey(notificationEntity, errors, userService, contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (notificationEntity.getId() != null) {
                    try {
                        NotificationEntity originalEntity = notificationService.getNotificationById(notificationEntity.getId(), contextInfo);
                        validateUpdateNotification(errors, notificationEntity, originalEntity);
                    } catch (DoesNotExistException | InvalidParameterException ex) {
                        LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + NotificationValidator.class.getSimpleName(), ex);
                        String fieldName = "id";
                        errors.add(
                                new ValidationResultInfo(fieldName,
                                        ErrorLevel.ERROR, ValidateConstant.ID_SUPPLIED + "update" + ValidateConstant.DOES_NOT_EXIST));
                        return errors;
                    }
                }
                break;
            case Constant.CREATE_VALIDATION:
                validateCreateNotification(errors, notificationEntity, notificationService, contextInfo);
                break;
            default:
                LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, NotificationValidator.class.getSimpleName());
                throw new InvalidParameterException(ValidateConstant.INVALID_VALIDATION_TYPE_KEY);
        }

        // For : Id
        validateNotificationEntityId(notificationEntity, errors);

        return errors;
    }

    private static void validateUpdateNotification(List<ValidationResultInfo> errors,
                                                   NotificationEntity notificationEntity,
                                                   NotificationEntity originalEntity) {
        // required validation
        ValidationUtils.validateRequired(notificationEntity.getId(), "id", errors);
        //check the meta required
        if (notificationEntity.getVersion() == null) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    fieldName + ValidateConstant.MUST_PROVIDED));
        } // check meta version id
        else if (!notificationEntity.getVersion()
                .equals(originalEntity.getVersion())) {
            String fieldName = "meta.version";
            errors.add(new ValidationResultInfo(fieldName,
                    ErrorLevel.ERROR,
                    ValidateConstant.SOMEONE_UPDATED + " Notification " + ValidateConstant.REFRESH_COPY));
        }
        // check not updatable fields
        validateNotUpdatable(errors, notificationEntity, originalEntity);
    }

    //validate create
    private static void validateCreateNotification(
            List<ValidationResultInfo> errors,
            NotificationEntity notificationEntity,
            NotificationService notificationService,
            ContextInfo contextInfo) {
        if (notificationEntity.getId() != null) {
            try {
                notificationService.getNotificationById(notificationEntity.getId(), contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName, ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + "create" + ValidateConstant.ALREADY_EXIST));
            } catch (DoesNotExistException | InvalidParameterException ex) {
                // This is ok because created id should be unique
            }
        }
    }

    private static void trimNotification(NotificationEntity notificationEntity) {
        if (notificationEntity.getMessage() != null) {
            notificationEntity.setMessage(notificationEntity.getMessage().trim());
        }
    }

    //Validate Required
    private static void validateCommonRequired(NotificationEntity notificationEntity, List<ValidationResultInfo> errors) {
        //check for name
        ValidationUtils.validateRequired(notificationEntity.getMessage(), "message", errors);
        //check for receiver
        ValidationUtils.validateRequired(notificationEntity.getReceiver(), "receiver", errors);
    }

    private static void validateNotUpdatable(List<ValidationResultInfo> errors, NotificationEntity notificationEntity, NotificationEntity originalEntity) {
        // state can't be updated
        ValidationUtils.validateNotUpdatable(notificationEntity.getState(), originalEntity.getState(), "state", errors);
        ValidationUtils.validateNotUpdatable(notificationEntity.getReceiver(), originalEntity.getReceiver(), "receiver", errors);
    }

    //Validation For :Id
    private static void validateNotificationEntityId(NotificationEntity notificationEntity, List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(notificationEntity.getId(), "id", 0, 255, errors);
    }

    private static void validateCommonForeignKey(NotificationEntity notificationEntity,
                                                 List<ValidationResultInfo> errors,
                                                 UserService userService,
                                                 ContextInfo contextInfo) {
        if (notificationEntity.getReceiver() != null) {
            try {
                notificationEntity.setReceiver(
                        userService.getUserById(notificationEntity.getReceiver().getId(), contextInfo)
                );
            } catch (DoesNotExistException | InvalidParameterException ex) {
                LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + NotificationValidator.class.getSimpleName(), ex);
                String fieldName = "receiver";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                ValidateConstant.ID_SUPPLIED + fieldName + ValidateConstant.DOES_NOT_EXIST));
            }
        }

    }
}
