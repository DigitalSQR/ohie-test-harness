package com.argusoft.path.tht.notificationmanagement.service;

import com.argusoft.path.tht.notificationmanagement.filter.NotificationCriteriaSearchFilter;
import com.argusoft.path.tht.notificationmanagement.models.entity.NotificationEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface provides contract for Notification API.
 *
 * @author Ali
 */
public interface NotificationService {

    /**
     * Creates a new Notification.
     *
     * @param notificationEntity notification
     * @param contextInfo     information containing the principalId and locale
     *                        information about the caller of service operation
     * @return NotificationEntity the Notification just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    NotificationEntity or contextInfo is not
     *                                      valid
     * @throws OperationFailedException     unable to complete request
     */
    public NotificationEntity createNotification(NotificationEntity notificationEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException;

    /**
     * Updates an existing Notification.
     *
     * @param notificationEntity the new data for the Notification
     * @param contextInfo     information containing the principalId and locale
     *                        information about the caller of service operation
     * @return NotificationEntity the details of Notification just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    Notification or contextInfo is not
     *                                      valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public NotificationEntity updateNotification(NotificationEntity notificationEntity,
                                           ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException;

    /**
     * Retrieves a list of Notifications corresponding to the given notification
     * Name.The returned list may be in any order with unique set.
     *
     * @param notificationCriteriaSearchFilter
     * @param pageable                      Contains Index number of the Page, Max size of the single
     *                                      page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo                   information containing the principalId and locale
     *                                      information about the caller of service operation
     * @return a list of Notifications
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<NotificationEntity> searchNotifications(NotificationCriteriaSearchFilter notificationCriteriaSearchFilter,
                                                        Pageable pageable,
                                                        ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves a list of Notifications corresponding to the given notification
     * Name.The returned list may be in any order with unique set.
     *
     * @param notificationCriteriaSearchFilter
     * @param contextInfo                   information containing the principalId and locale
     *                                      information about the caller of service operation
     * @return a list of notification
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public List<NotificationEntity> searchNotifications(NotificationCriteriaSearchFilter notificationCriteriaSearchFilter,
                                                  ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves a Notification corresponding to the given Notification Id.
     *
     * @param notificationId NotificationId of notification to be retrieved
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a notification
     * @throws DoesNotExistException     a NotificationId in NotificationIds not found
     * @throws InvalidParameterException invalid contextInfo
     */
    public NotificationEntity getNotificationById(String notificationId,
                                            ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;

    /**
     * Change the state of notification
     *
     * @param notificationId NotificationId of notification to be retrieved
     * @param stateKey    state type to which notification state to be changed
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return changed state notification
     * @throws DoesNotExistException        a NotificationId in NotificationIds not found
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    invalid contextInfo
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public NotificationEntity changeState(String notificationId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;

    /**
     * Change the state of notification
     *
     * @param oldStateKey NotificationId of notification to be retrieved
     * @param newStateKey    state type to which notification state to be changed
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return changed state notification
     * @throws DoesNotExistException        a NotificationId in NotificationIds not found
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    invalid contextInfo
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public List<NotificationEntity> bulkChangeState(String oldStateKey, String newStateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException;
    
}
