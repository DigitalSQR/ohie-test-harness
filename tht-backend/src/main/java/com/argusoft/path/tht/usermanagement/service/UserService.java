/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.usermanagement.filter.RoleSearchFilter;
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This interface provides contract for User API.
 *
 * @author dhruv
 * @since 2023-09-13
 */
public interface UserService {

    /**
     * .logout in application.
     *
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return UserInfo the User just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    userInfo or contextInfo is not valid
     * @throws MissingParameterException    userInfo or contextInfo is missing or
     *                                      null
     * @throws OperationFailedException     unable to complete request
     * @throws PermissionDeniedException    an authorization failure occurred
     */
    public Boolean logout(ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            MissingParameterException,
            PermissionDeniedException,
            DataValidationErrorException;

    /**
     * Creates a new User.In the user Id, Description, and Meta information may
     * not be set in the supplied userInfo.
     *
     * @param userEntity  user
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return UserInfo the User just created
     * @throws DataValidationErrorException supplied data is invalid
     * @throws InvalidParameterException    userInfo or contextInfo is not valid
     * @throws MissingParameterException    userInfo or contextInfo is missing or
     *                                      null
     * @throws OperationFailedException     unable to complete request
     * @throws PermissionDeniedException    an authorization failure occurred
     */
    public UserEntity createUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            MissingParameterException,
            PermissionDeniedException,
            DataValidationErrorException, DoesNotExistException;

    public UserEntity getUserByEmail(String email, ContextInfo contextInfo) throws DoesNotExistException;

    /**
     * Updates an existing User.
     *
     * @param userEntity  the new data for the User
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return UserInfo the details of User just updated
     * @throws DataValidationErrorException supplied data is invalid
     * @throws DoesNotExistException        user Id not found
     * @throws InvalidParameterException    userInfo or contextInfo is not valid
     * @throws MissingParameterException    userInfo, or contextInfo is missing or
     *                                      null
     * @throws OperationFailedException     unable to complete request
     * @throws PermissionDeniedException    an authorization failure occurred
     * @throws VersionMismatchException     optimistic locking failure or the action
     *                                      was attempted on an out of date version
     */
    public UserEntity updateUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            VersionMismatchException,
            DataValidationErrorException, InvalidParameterException, PermissionDeniedException;

    /**
     * Retrieves a list of Users corresponding to the given User Name.The
     * returned list may be in any order with unique set.
     *
     * @param ids              list of ids to retrieve
     * @param userSearchFilter
     * @param pageable         Contains Index number of the Page, Max size of the single
     *                         page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo      information containing the principalId and locale
     *                         information about the caller of service operation
     * @return a list of User name start with given UserName found
     * @throws InvalidParameterException invalid contextInfo
     * @throws MissingParameterException name or contextInfo is missing or null
     * @throws OperationFailedException  unable to complete request
     * @throws PermissionDeniedException an authorization failure occurred
     */
    public Page<UserEntity> searchUsers(List<String> ids,
                                        UserSearchFilter userSearchFilter,
                                        Pageable pageable,
                                        ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
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
     * @throws MissingParameterException validationTypeKey, userInfo, or
     *                                   contextInfo is missing or null
     * @throws OperationFailedException  unable to complete request
     * @throws PermissionDeniedException an authorization failure occurred
     * @throws DoesNotExistException
     */
    public List<ValidationResultInfo> validateUser(String validationTypeKey,
                                                   UserEntity userEntity,
                                                   ContextInfo contextInfo)
            throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            DoesNotExistException;

    /**
     * Retrieves a User corresponding to the given User Id.
     *
     * @param userId      userId of User to be retrieved
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of User
     * @throws DoesNotExistException     a userId in userIds not found
     * @throws InvalidParameterException invalid contextInfo
     * @throws MissingParameterException userId or contextInfo is missing or
     *                                   null
     * @throws OperationFailedException  unable to complete request
     * @throws PermissionDeniedException an authorization failure occurred
     */
    public UserEntity getUserById(String userId,
                                  ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException;

    /**
     * Retrieves a list of Users.The returned list may be in any order with
     * unique set.
     *
     * @param pageable    Contains Index number of the Page, Max size of the single
     *                    page,Name of the field for sorting and sortDirection sorting direction
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return a list of User
     * @throws InvalidParameterException invalid contextInfo
     * @throws MissingParameterException contextInfo is missing or null
     * @throws OperationFailedException  unable to complete request
     * @throws PermissionDeniedException an authorization failure occurred
     */
    public Page<UserEntity> getUsers(Pageable pageable,
                                     ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException;

    /**
     * get logged in uses detail
     *
     * @param contextInfo information containing the principalId and locale
     *                    information about the caller of service operation
     * @return logged in User
     * @throws OperationFailedException unable to complete request
     * @throws DoesNotExistException
     */
    public UserEntity getPrincipalUser(ContextInfo contextInfo)
            throws OperationFailedException,
            DoesNotExistException;


    public Page<RoleEntity> searchRoles(List<String> ids,
                                        RoleSearchFilter roleSearchFilter,
                                        Pageable pageable,
                                        ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException;

    public RoleEntity getRoleById(String roleId,
                                  ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException;

    public Page<RoleEntity> getRoles(Pageable pageable,
                                     ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException;

    public UserEntity registerAssessee(UserEntity userEntity,
                                       ContextInfo contextInfo)
            throws DoesNotExistException,
            PermissionDeniedException,
            OperationFailedException,
            InvalidParameterException,
            MissingParameterException,
            DataValidationErrorException;

    public void createForgotPasswordRequestAndSendEmail(String userEmail, ContextInfo contextInfo);

}
