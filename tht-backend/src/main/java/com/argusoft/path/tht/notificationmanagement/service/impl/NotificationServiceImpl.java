package com.argusoft.path.tht.notificationmanagement.service.impl;

import com.argusoft.path.tht.notificationmanagement.constant.NotificationServiceConstants;
import com.argusoft.path.tht.notificationmanagement.filter.NotificationCriteriaSearchFilter;
import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.notificationmanagement.repository.NotificationRepository;
import com.argusoft.path.tht.notificationmanagement.service.NotificationService;
import com.argusoft.path.tht.notificationmanagement.validator.NotificationValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.CommonStateChangeValidator;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This NotificationServiceImpl contains implementation for Notification
 * service.
 *
 * @author Ali
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    NotificationRepository notificationRepository;

    UserService userService;

    @Autowired
    public void setNotificationRepository(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public NotificationEntity createNotification(NotificationEntity notificationEntity,
                                                 ContextInfo contextInfo)
            throws
            InvalidParameterException, DataValidationErrorException {
        if (notificationEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, NotificationServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("notificationEntity is missing");
        }
        defaultValueCreateNotification(notificationEntity);
        NotificationValidator.validateCreateUpdateNotification(Constant.CREATE_VALIDATION, this, userService, notificationEntity, contextInfo);
        notificationEntity = notificationRepository.saveAndFlush(notificationEntity);
        return notificationEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public NotificationEntity updateNotification(NotificationEntity notificationEntity,
                                                 ContextInfo contextInfo)
            throws InvalidParameterException, DataValidationErrorException {
        if (notificationEntity == null) {
            LOGGER.error("{}{}", ValidateConstant.INVALID_PARAM_EXCEPTION, NotificationServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("notificationEntity is missing");
        }
        NotificationValidator.validateCreateUpdateNotification(Constant.UPDATE_VALIDATION, this, userService, notificationEntity, contextInfo);
        notificationEntity = notificationRepository.saveAndFlush(notificationEntity);
        return notificationEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<NotificationEntity> searchNotifications(
            NotificationCriteriaSearchFilter notificationCriteriaSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<NotificationEntity> notificationEntitySpecification = notificationCriteriaSearchFilter.buildSpecification(contextInfo);
        return this.notificationRepository.findAll(notificationEntitySpecification, pageable);
    }

    @Override
    public List<NotificationEntity> searchNotifications(
            NotificationCriteriaSearchFilter notificationCriteriaSearchFilter,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Specification<NotificationEntity> notificationEntitySpecification = notificationCriteriaSearchFilter.buildSpecification(contextInfo);
        return this.notificationRepository.findAll(notificationEntitySpecification);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public NotificationEntity getNotificationById(String notificationId,
                                                  ContextInfo contextInfo)
            throws DoesNotExistException, InvalidParameterException {
        if (!StringUtils.hasLength(notificationId)) {
            throw new InvalidParameterException("notificationId is missing");
        }
        NotificationCriteriaSearchFilter notificationCriteriaSearchFilter = new NotificationCriteriaSearchFilter(notificationId);
        List<NotificationEntity> notificationEntities = this.searchNotifications(notificationCriteriaSearchFilter, contextInfo);
        return notificationEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("Notification does not found with id : " + notificationId));
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public NotificationEntity changeState(String notificationId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();
        NotificationEntity notificationEntity = this.getNotificationById(notificationId, contextInfo);
        CommonStateChangeValidator.validateStateChange(NotificationServiceConstants.NOTIFICATION_STATUS, NotificationServiceConstants.NOTIFICATION_STATUS_MAP, notificationEntity.getState(), stateKey, errors);
        notificationEntity.setState(stateKey);
        notificationEntity = notificationRepository.saveAndFlush(notificationEntity);
        return notificationEntity;
    }

    private void defaultValueCreateNotification(NotificationEntity notificationEntity) {
        notificationEntity.setId(UUID.randomUUID().toString());
        notificationEntity.setState(NotificationServiceConstants.NOTIFICATION_STATUS_UNREAD);
    }

}
