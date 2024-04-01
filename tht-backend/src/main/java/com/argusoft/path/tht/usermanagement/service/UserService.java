package com.argusoft.path.tht.usermanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.filter.RoleSearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.filter.UserSearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.models.dto.ResetPasswordInfo;
import com.argusoft.path.tht.usermanagement.models.dto.UpdatePasswordInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * This interface provides contract for User API.
 *
 * @author Dhruv
 */
public interface UserService {

    /**
     * logout in application.
     *
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return UserInfo the User just created
     * @throws OperationFailedException unable to complete request
     */
    public Boolean logout(ContextInfo contextInfo)
            throws OperationFailedException;


    /**
     * send mail to appropriate user on change of state of a user
     *
     * @param oldState state before change
     * @param newState state after change
     * @param userEntity user of which state was changed
     * @param contextInfo information containing the principalId and locale
     *      *                    information about the caller of service operation
     * @throws InvalidParameterException getUsersByRole is not valid
     * @throws DoesNotExistException getUsersByRole is not valid
     */
    public void sendMailToTheUserOnChangeState(String oldState, String newState, UserEntity userEntity, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException;

    /**
     * Creates a new User.In the user Id, Description, and Meta information may
     * not be set in the supplied userInfo.
     *
     * @param userEntity  the user data
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return UserInfo the User just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    userInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     */
    public UserEntity createUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, MessagingException, IOException;

    public UserEntity getUserByEmail(String email, ContextInfo contextInfo)
            throws DoesNotExistException, InvalidParameterException;

    /**
     * Updates an existing User.
     *
     * @param userEntity  the new data for the User
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return UserInfo the details of User just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    userInfo or contextInfo is not valid
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public UserEntity updateUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException,
            InvalidParameterException;

    /**
     * Retrieves a list of Users corresponding to the given User Name.The
     * returned list may be in any order with unique set.
     *
     * @param userSearchFilter
     * @param pageable         Contains Index number of the Page, Max size of the single
     *                         page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return a list of Username start with given UserName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public Page<UserEntity> searchUsers(UserSearchCriteriaFilter userSearchFilter,
                                        Pageable pageable,
                                        ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves a list of Users corresponding to the given User Name.The
     * returned list may be in any order with unique set.
     *
     * @param userSearchFilter
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return a list of username start with given UserName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws OperationFailedException  unable to complete request
     */
    public List<UserEntity> searchUsers(UserSearchCriteriaFilter userSearchFilter,
                                        ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Validates a User.Depending on the value of validationType, this
     * validation could be limited to tests on just the current object and its
     * directly contained sub-objects or expanded to perform all tests related
     * to this object
     *
     * @param validationTypeKey the identifier of the extent of validation
     * @param userEntity        the User information to be tested
     * @param contextInfo       information containing the principalId and locale
     *                          information about the caller of service operation
     * @return Results user performing the validation
     * @throws InvalidParameterException userInfo or contextInfo is not valid
     * @throws OperationFailedException  unable to complete request
     */
    public List<ValidationResultInfo> validateUser(String validationTypeKey,
                                                   UserEntity userEntity,
                                                   ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException;

    /**
     * Retrieves a User corresponding to the given User Id.
     *
     * @param userId      userId of User to be retrieved
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of User
     * @throws DoesNotExistException     when user does not exist for that id
     * @throws InvalidParameterException invalid contextInfo
     */
    public UserEntity getUserById(String userId,
                                  ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException;

    /**
     * get logged in uses detail
     *
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return logged in User
     * @throws OperationFailedException unable to complete request
     * @throws DoesNotExistException    when user does not exist for that id
     */
    public UserEntity getPrincipalUser(ContextInfo contextInfo)
            throws OperationFailedException,
            DoesNotExistException, InvalidParameterException;

    /**
     * Retrieves a list of user roles. The returned list may be in any order
     * with unique set.
     *
     * @param roleSearchFilter * @param pageable Contains Index number of the
     *                         Page, Max size of the single page,Name of the field for sorting and
     *                         sortDirection sorting direction
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return list of role with given search criteria
     * @throws OperationFailedException  unable to complete request
     * @throws InvalidParameterException ContextInfo is not valid
     */
    public Page<RoleEntity> searchRoles(RoleSearchCriteriaFilter roleSearchFilter,
                                        Pageable pageable,
                                        ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves a list of user roles. The returned list may be in any order
     * with unique set.
     *
     * @param roleSearchFilter criteria for search role
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return list of role with given search criteria
     * @throws OperationFailedException  unable to complete request
     * @throws InvalidParameterException ContextInfo is not valid
     */
    public List<RoleEntity> searchRoles(RoleSearchCriteriaFilter roleSearchFilter,
                                        ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves role by roleId.
     *
     * @param roleId      id of role
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return RoleEntity
     * @throws DoesNotExistException     when user does not exist for that id
     * @throws OperationFailedException  unable to complete request
     * @throws InvalidParameterException ContextInfo is not valid
     */
    public RoleEntity getRoleById(String roleId,
                                  ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            InvalidParameterException;

    /**
     * Retrieves a list of user roles. The returned list may be in any order
     * with unique set.
     *
     * @param pageable    Contains Index number of the Page, Max size of the single
     *                    page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return list of role with given search criteria
     * @throws InvalidParameterException ContextInfo is not valid
     */
    public Page<RoleEntity> getRoles(Pageable pageable,
                                     ContextInfo contextInfo)
            throws InvalidParameterException;

    /**
     * Register Assessee
     *
     * @param userEntity  User information to be registered
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return Registered user information
     * @throws DoesNotExistException        when user does not exist for that id
     * @throws OperationFailedException     unable to complete request
     * @throws InvalidParameterException    ContextInfo is not valid
     * @throws DataValidationErrorException supplied data is invalid
     * @throws MessagingException
     * @throws IOException
     */
    public UserEntity registerAssessee(UserEntity userEntity,
                                       ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, MessagingException, IOException;

    /**
     * Create forgot password request and send email for user
     *
     * @param userEmail   User email
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     */
    public void createForgotPasswordRequestAndSendEmail(String userEmail, ContextInfo contextInfo);

    /**
     * Update password for user
     *
     * @param updatePasswordInfo information containing new password
     * @param contextInfo        information containing the principalId and locale
     *                           information about the caller of service operation
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    UpdatePasswordInfo or ContextInfo is
     *                                      not valid
     * @throws DoesNotExistException        when user does not exist for that id
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public void updatePasswordWithVerificationToken(UpdatePasswordInfo updatePasswordInfo, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, DoesNotExistException, OperationFailedException, VersionMismatchException;

    /**
     * Reset password of user.
     *
     * @param resetPasswordInfo information containing old password and new
     *                          password
     * @param contextInfo       ContextInfo
     * @return UserEntity
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    ResetPasswordInfo or ContextInfo is not
     *                                      valid
     * @throws DoesNotExistException        when user does not exist for that id
     * @throws OperationFailedException     unable to complete request
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public UserEntity resetPassword(ResetPasswordInfo resetPasswordInfo, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException;

    /**
     * change state of user with id and giving the expected state
     *
     * @param documentId  id of the user
     * @param stateKey    expected state key
     * @param contextInfo ContextInfo
     * @return UserEntity
     * @throws DoesNotExistException        when user does not exist for that id
     * @throws DataValidationErrorException when validation fails
     */
    public UserEntity changeState(String documentId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException, MessagingException, IOException;

    /**
     * resend verification email
     *
     * @param userEmail   String of the user
     * @param contextInfo ContextInfo
     */
    public void resendVerification(String userEmail, ContextInfo contextInfo);

    /**
     * Revoke access token of user
     *
     * @param clientId String clientId of user
     * @param userName String userName of user
     */
    public void revokeAccessTokenOnStateChange(String clientId, String userName);

    /**
     * get users by role
     *
     * @param role        String role of the users to be retrieved
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return List of users with given role
     * @throws InvalidParameterException
     * @throws DoesNotExistException
     */
    public List<UserEntity> getUsersByRole(String role, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException;
}
