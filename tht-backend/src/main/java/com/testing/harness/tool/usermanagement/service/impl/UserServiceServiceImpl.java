/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testing.harness.tool.usermanagement.service.impl;

import com.testing.harness.tool.systemconfiguration.constant.Constant;
import com.testing.harness.tool.systemconfiguration.constant.ErrorLevel;
import com.testing.harness.tool.systemconfiguration.exceptioncontroller.exception.*;
import com.testing.harness.tool.systemconfiguration.models.dto.ContextInfo;
import com.testing.harness.tool.systemconfiguration.models.dto.ValidationResultInfo;
import com.testing.harness.tool.systemconfiguration.utils.ValidationUtils;
import com.testing.harness.tool.usermanagement.filter.UserSearchFilter;
import com.testing.harness.tool.usermanagement.models.entity.UserEntity;
import com.testing.harness.tool.usermanagement.repository.UserRepository;
import com.testing.harness.tool.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This UserServiceDaoImpl contains DAO implementation for User service.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Service
public class UserServiceServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    /**
     * {@inheritdoc}
     */
    @Override
    public Boolean logout(ContextInfo contextInfo)
            throws OperationFailedException {
        return defaultTokenServices.revokeToken(contextInfo.getAccessToken());
    }

    /**
     * {@inheritdoc}
     * @return
     */
    @Override
    @Transactional
    public UserEntity createUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {
        List<ValidationResultInfo> validationResultEntitys
                = this.validateUser(Constant.CREATE_VALIDATION,
                userEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntitys);
        }
        if (StringUtils.isEmpty(userEntity.getId())) {
            userEntity.setId(UUID.randomUUID().toString());
        }
        userEntity = userRepository.save(userEntity);
        return userEntity;
    }

    /**
     * {@inheritdoc}
     * @return
     */
    @Override
    @Transactional
    public UserEntity updateUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntitys
                = this.validateUser(Constant.UPDATE_VALIDATION,
                userEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
        Optional<UserEntity> userOptional
                = userRepository.findById(userEntity.getId());
        if (!userOptional.isPresent()) {
            throw new DoesNotExistException("User not found");
        }
        if (userEntity.getVersion() == null) {
            throw new VersionMismatchException("Version misMatch");
        }
        userEntity = userRepository.save(userEntity);
        return userEntity;
    }

    /**
     * {@inheritdoc}
     * @return
     */
    @Override
    public Page<UserEntity> searchUsers(
            List<String> ids,
            UserSearchFilter userSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        if (!CollectionUtils.isEmpty(ids)) {
            return this.searchUsersById(ids, pageable);
        } else {
            return this.searchUsers(userSearchFilter, pageable);
        }
    }

    public Page<UserEntity> searchUsers(
            UserSearchFilter userSearchFilter,
            Pageable pageable)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        Page<UserEntity> users = userRepository.advanceUserSearch(
            userSearchFilter,
            pageable);
        return users;
    }

    public Page<UserEntity> searchUsersById(
            List<String> ids,
            Pageable pageable)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        List<UserEntity> users
                = userRepository.findUsersByIds(ids);
        return new PageImpl<>(users,
                pageable,
                users.size());
    }

    /**
     * {@inheritdoc}
     * @return
     */
    @Override
    public UserEntity getUserById(String userId,
                                  ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        if (StringUtils.isEmpty(userId)) {
            throw new DoesNotExistException("User by id :"
                    + userId
                    + Constant.NOT_FOUND);
        }
        Optional<UserEntity> userOptional
                = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new DoesNotExistException("User by id :"
                    + userId
                    + Constant.NOT_FOUND);
        }
        return userOptional.get();
    }

    /**
     * {@inheritdoc}
     * @return
     */
    @Override
    public Page<UserEntity> getUsers(Pageable pageable,
                                     ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        Page<UserEntity> users  = userRepository.findUsers(pageable);
        return users;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public List<ValidationResultInfo> validateUser(
            String validationTypeKey,
            UserEntity userEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException, DoesNotExistException {
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        UserEntity originalEntity = null;
        trimUser(userEntity);

        // check Common Required
        this.validateCommonRequired(userEntity, errors);

        // check Common Unique
        this.validateCommonUnique(userEntity,
                validationTypeKey,
                errors,
                contextInfo);

        switch (validationTypeKey) {
            case Constant.UPDATE_VALIDATION:
                // get the info
                if (userEntity.getId() != null) {
                    try {
                        originalEntity = this
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

                this.validateUpdateUser(errors,
                        userEntity,
                        originalEntity);
                break;
            case Constant.CREATE_VALIDATION:
                this.validateCreateUser(errors, userEntity, contextInfo);
                break;
            default:
                throw new InvalidParameterException("Invalid validationTypeKey");
        }

        // For : Id
        validateUserEntityId(userEntity,
                errors);
        // For :Name
        validateUserEntityame(userEntity,
                errors);
        // For :UserName
        validateUserEntityUserName(userEntity,
                errors);

        return errors;
    }

    /**
     * {@inheritdoc}
     * @return
     */
    @Override
    public UserEntity getPrincipalUser(ContextInfo contextInfo)
            throws OperationFailedException,
            DoesNotExistException {
        Optional<UserEntity> userOptional
                = userRepository.findByUserName(contextInfo.getUsername());
        if (!userOptional.isPresent()) {
            throw new DoesNotExistException("user by email :"
                    + contextInfo.getEmail()
                    + Constant.NOT_FOUND);
        }
        return userOptional.get();
    }

    //validate update
    protected void validateUpdateUser(List<ValidationResultInfo> errors,
                                      UserEntity userEntity,
                                      UserEntity originalEntity)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException, DoesNotExistException {
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
        this.validateNotUpdatable(errors, userEntity, originalEntity);
    }

    //validate not update
    protected void validateNotUpdatable(List<ValidationResultInfo> errors,
                                        UserEntity userEntity,
                                        UserEntity originalEntity)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        //email can't be update
        ValidationUtils.validateNotUpdatable(userEntity.getEmail(), originalEntity.getEmail(), "email", errors);
    }

    //validate create
    protected void validateCreateUser(
            List<ValidationResultInfo> errors,
            UserEntity userEntity,
            ContextInfo contextInfo) {
        if (userEntity.getId() != null) {
            try {
                this.getUserById(userEntity.getId(),
                        contextInfo);
                // if info found with same id than
                String fieldName = "id";
                errors.add(
                        new ValidationResultInfo(fieldName,
                                ErrorLevel.ERROR,
                                "The id supplied to the create already exists"));
            } catch (DoesNotExistException | InvalidParameterException | MissingParameterException | OperationFailedException | PermissionDeniedException ex) {
                // This is ok becuase created id should be unique
            }
        }
    }

    //Validate Required
    protected void validateCommonRequired(UserEntity userEntity,
                                          List<ValidationResultInfo> errors) {
        //check the email required
        ValidationUtils
                .validateRequired(userEntity.getEmail(), "email", errors);
    }

    //Validate Common Unique
    protected void validateCommonUnique(UserEntity userEntity,
                                        String validationTypeKey,
                                        List<ValidationResultInfo> errors,
                                        ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || userEntity.getId() != null)
                && userEntity.getEmail() != null) {
            UserSearchFilter searchFilter = new UserSearchFilter();
            searchFilter.setEmail(userEntity.getEmail());
            Page<UserEntity> userEntitys = this
                    .searchUsers(
                    null,
                    searchFilter,
                    Constant.TWO_VALUE_PAGE,
                    contextInfo);

            // if info found with same email and username than and not current id
            boolean flag
                    = userEntitys.stream().anyMatch(u -> (validationTypeKey.equals(Constant.CREATE_VALIDATION)
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

    //Validation For :Id
    protected void validateUserEntityId(UserEntity userEntity,
                                        List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(userEntity.getId(), "id", errors);
    }

    //Validation For :Name
    protected void validateUserEntityame(UserEntity userEntity,
                                              List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(userEntity.getName(),
                "userName",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(userEntity.getName(),
                "userName",
                3,
                255,
                errors);
    }

    //Validation For :UserName
    protected void validateUserEntityUserName(UserEntity userEntity,
                                          List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(userEntity.getUserName(),
                "userName",
                Constant.ALLOWED_CHARS_IN_NAMES,
                "Only alphanumeric and " + Constant.ALLOWED_CHARS_IN_NAMES + " are allowed.",
                errors);
        ValidationUtils.validateLength(userEntity.getUserName(),
                "userName",
                3,
                255,
                errors);
    }

    //trim all User field
    protected void trimUser(UserEntity userEntity) {
        if (userEntity.getId() != null) {
            userEntity.setId(userEntity.getId().trim());
        }
        if (userEntity.getEmail() != null) {
            userEntity.setEmail(userEntity.getEmail().trim());
        }
        if (userEntity.getUserName() != null) {
            userEntity.setUserName(userEntity.getUserName().trim());
        }
    }
}
