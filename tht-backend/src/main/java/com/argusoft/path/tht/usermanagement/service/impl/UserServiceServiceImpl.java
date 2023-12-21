/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.service.impl;

import com.argusoft.path.tht.emailservice.service.EmailService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.filter.RoleSearchFilter;
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.enums.TokenTypeEnum;
import com.argusoft.path.tht.usermanagement.repository.RoleRepository;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import com.argusoft.path.tht.usermanagement.service.TokenVerificationService;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * This UserServiceServiceImpl contains implementation for User service.
 *
 * @author Dhruv
 */
@Service
public class UserServiceServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenVerificationService tokenVerificationService;

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


    @Override
    public UserEntity getUserByEmail(String email, ContextInfo contextInfo)
            throws DoesNotExistException {
        Optional<UserEntity> userOptional = userRepository.findUserByEmail(email);
        if (!userOptional.isPresent()) {
            throw new DoesNotExistException("User by email :"
                    + email
                    + Constant.NOT_FOUND);
        }
        return userOptional.get();
    }

    @Override
    public UserEntity registerAssessee(UserEntity userEntity, ContextInfo contextInfo)
            throws DoesNotExistException, OperationFailedException, InvalidParameterException, DataValidationErrorException, MissingParameterException {
        if (Objects.equals(contextInfo.getEmail(), Constant.SUPER_USER_CONTEXT.getEmail())) {
            //If method get called on google Oauth2 login then verification of email is not needed.
            userEntity.setState(UserServiceConstants.USER_STATUS_APPROVAL_PENDING);
        } else {
            userEntity.setState(UserServiceConstants.USER_STATUS_VERIFICATION_PENDING);
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(UserServiceConstants.ROLE_ID_ASSESSEE);
        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleEntity);

        userEntity = this.createUser(userEntity, contextInfo);

        //On Email verification pending send mail for the email for verification
        if (Objects.equals(userEntity.getState(), UserServiceConstants.USER_STATUS_VERIFICATION_PENDING)) {
            tokenVerificationService.generateTokenForUserAndSendEmailForType(userEntity.getId(), TokenTypeEnum.VERIFICATION.getKey(), contextInfo);
        }
        return userEntity;
    }


    @Override
    public void createForgotPasswordRequestAndSendEmail(String userEmail, ContextInfo contextInfo) {
        UserEntity userByEmail = null;
        try {
            userByEmail = this.getUserByEmail(userEmail, contextInfo);
            TokenVerificationEntity tokenVerification = tokenVerificationService.generateTokenForUserAndSendEmailForType(userByEmail.getId(), TokenTypeEnum.FORGOT_PASSWORD.getKey(), contextInfo);
        } catch (Exception e) {
            // ignore it, no need to show that they are not exists in DB
            //TODO add log
        }
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public UserEntity createUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {
        List<ValidationResultInfo> validationResultEntitys
                = this.validateUser(Constant.CREATE_VALIDATION,
                userEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred in the validating",
                    validationResultEntitys);
        }
        userEntity = userRepository.save(userEntity);
        return userEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Transactional
    public UserEntity updateUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException, InvalidParameterException {
        List<ValidationResultInfo> validationResultEntitys
                = this.validateUser(Constant.UPDATE_VALIDATION,
                userEntity,
                contextInfo);
        if (ValidationUtils.containsErrors(validationResultEntitys, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    "Error(s) occurred validating",
                    validationResultEntitys);
        }
        userEntity = userRepository.save(userEntity);
        return userEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<UserEntity> searchUsers(
            List<String> ids,
            UserSearchFilter userSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException,
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
            throws OperationFailedException {

        Page<UserEntity> users = userRepository.advanceUserSearch(
                userSearchFilter,
                pageable);
        return users;
    }

    public Page<UserEntity> searchUsersById(
            List<String> ids,
            Pageable pageable) {
        List<UserEntity> users
                = userRepository.findUsersByIds(ids);
        return new PageImpl<>(users,
                pageable,
                users.size());
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public UserEntity getUserById(String userId,
                                  ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {
        if (StringUtils.isEmpty(userId)) {
            throw new InvalidParameterException("userId is missing");
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
     *
     * @return
     */
    @Override
    public Page<UserEntity> getUsers(Pageable pageable,
                                     ContextInfo contextInfo) {
        Page<UserEntity> users = userRepository.findUsers(pageable);
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
            OperationFailedException {
        // VALIDATE
        List<ValidationResultInfo> errors = new ArrayList<>();
        UserEntity originalEntity = null;
        trimUser(userEntity);

        // check Common Required
        this.validateCommonRequired(userEntity, errors);

        // check Common ForeignKey
        this.validateCommonForeignKey(userEntity, errors, contextInfo);

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

    protected void validateCommonForeignKey(UserEntity userEntity,
                                            List<ValidationResultInfo> errors,
                                            ContextInfo contextInfo) {
        //validate Role foreignKey.
        Set<RoleEntity> roleEntitySet = new HashSet<>();
        userEntity.getRoles().stream().forEach(item -> {
            try {
                roleEntitySet.add(this.getRoleById(item.getId(), contextInfo));
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

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public UserEntity getPrincipalUser(ContextInfo contextInfo)
            throws DoesNotExistException {
        Optional<UserEntity> userOptional
                = userRepository.findById(contextInfo.getUsername());
        if (!userOptional.isPresent()) {
            throw new DoesNotExistException("user by id :"
                    + contextInfo.getUsername()
                    + Constant.NOT_FOUND);
        }
        return userOptional.get();
    }

    //validate update
    protected void validateUpdateUser(List<ValidationResultInfo> errors,
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
        this.validateNotUpdatable(errors, userEntity, originalEntity);
    }

    //validate not update
    protected void validateNotUpdatable(List<ValidationResultInfo> errors,
                                        UserEntity userEntity,
                                        UserEntity originalEntity) {
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
            } catch (DoesNotExistException | InvalidParameterException ex) {
                // This is ok because created id should be unique
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
            InvalidParameterException {
        // check unique field
        if ((validationTypeKey.equals(Constant.CREATE_VALIDATION) || userEntity.getId() != null)
                && userEntity.getEmail() != null) {
            UserSearchFilter searchFilter = new UserSearchFilter();
            searchFilter.setEmail(userEntity.getEmail());
            Page<UserEntity> userEntities = this
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

    //Validation For :Id
    protected void validateUserEntityId(UserEntity userEntity,
                                        List<ValidationResultInfo> errors) {
        ValidationUtils.validateNotEmpty(userEntity.getId(), "id", errors);
    }

    //Validation For :Name
    protected void validateUserEntityName(UserEntity userEntity,
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

    //Validation For :Email
    protected void validateUserEntityEmail(UserEntity userEntity,
                                           List<ValidationResultInfo> errors) {
        ValidationUtils.validatePattern(userEntity.getEmail(),
                "email",
                UserServiceConstants.EMAIL_REGEX,
                "Given email is invalid.",
                errors);
    }

    //Validation For :Password
    protected void validateUserEntityPassword(UserEntity userEntity,
                                              List<ValidationResultInfo> errors) {
        ValidationUtils.validateLength(userEntity.getName(),
                "password",
                6,
                255,
                errors);
    }

    //Validation For :Roles
    protected void validateUserEntityRoles(UserEntity userEntity,
                                           List<ValidationResultInfo> errors) {
        ValidationUtils.validateCollectionSize(userEntity.getRoles(),
                "roles",
                1,
                null,
                errors);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<RoleEntity> searchRoles(
            List<String> ids,
            RoleSearchFilter roleSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        if (!CollectionUtils.isEmpty(ids)) {
            return this.searchRolesById(ids, pageable);
        } else {
            return this.searchRoles(roleSearchFilter, pageable);
        }
    }

    public Page<RoleEntity> searchRoles(
            RoleSearchFilter roleSearchFilter,
            Pageable pageable)
            throws OperationFailedException {

        Page<RoleEntity> roles = roleRepository.advanceRoleSearch(
                roleSearchFilter,
                pageable);
        return roles;
    }

    public Page<RoleEntity> searchRolesById(
            List<String> ids,
            Pageable pageable) {
        List<RoleEntity> roles
                = roleRepository.findRolesByIds(ids);
        return new PageImpl<>(roles,
                pageable,
                roles.size());
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public RoleEntity getRoleById(String roleId,
                                  ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            InvalidParameterException {
        if (StringUtils.isEmpty(roleId)) {
            throw new DoesNotExistException("User by id :"
                    + roleId
                    + Constant.NOT_FOUND);
        }
        Optional<RoleEntity> roleOptional
                = roleRepository.findById(roleId);
        if (!roleOptional.isPresent()) {
            throw new DoesNotExistException("User by id :"
                    + roleId
                    + Constant.NOT_FOUND);
        }
        return roleOptional.get();
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<RoleEntity> getRoles(Pageable pageable,
                                     ContextInfo contextInfo) {
        Page<RoleEntity> roles = roleRepository.findRoles(pageable);
        return roles;
    }

    //trim all User field
    protected void trimUser(UserEntity userEntity) {
        if (userEntity.getId() != null) {
            userEntity.setId(userEntity.getId().trim());
        }
        if (userEntity.getEmail() != null) {
            userEntity.setEmail(userEntity.getEmail().trim());
        }
    }
}
