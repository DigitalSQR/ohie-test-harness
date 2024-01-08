/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.service.impl;

import com.argusoft.path.tht.emailservice.service.EmailService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.filter.RoleSearchFilter;
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.models.dto.UpdatePasswordInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.enums.TokenTypeEnum;
import com.argusoft.path.tht.usermanagement.repository.RoleRepository;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import com.argusoft.path.tht.usermanagement.service.TokenVerificationService;
import com.argusoft.path.tht.usermanagement.service.UserService;
import com.argusoft.path.tht.usermanagement.validator.UserValidator;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This UserServiceServiceImpl contains implementation for User service.
 *
 * @author Dhruv
 */
@Service
@Metrics(registry = "UserServiceServiceImpl")
public class UserServiceServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private TokenVerificationService tokenVerificationService;

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    @Autowired
    private EmailService emailService;

    /**
     * {@inheritdoc}
     */
    @Override
    @Timed(name = "logout")
    public Boolean logout(ContextInfo contextInfo)
            throws OperationFailedException {
        return defaultTokenServices.revokeToken(contextInfo.getAccessToken());
    }

    @Override
    @Timed(name = "getUserByEmail")
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
    @Timed(name = "registerAssessee")
    public UserEntity registerAssessee(UserEntity userEntity, ContextInfo contextInfo)
            throws DoesNotExistException, OperationFailedException, InvalidParameterException, DataValidationErrorException {
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
    @Timed(name = "createForgotPasswordRequestAndSendEmail")
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

    @Override
    @Timed(name = "updatePasswordWithVerificationToken")
    public void updatePasswordWithVerificationToken(UpdatePasswordInfo updatePasswordInfo, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, DoesNotExistException, OperationFailedException, VersionMismatchException {

        //trim values
        updatePasswordInfo.trimObject();

        UserValidator.validateUpdatePasswordInfoAgainstNullValues(updatePasswordInfo);

        UserValidator.validateThatOldAndNewPasswordIsDifferent(updatePasswordInfo);

        Boolean isTokenVerified = tokenVerificationService
                .verifyUserToken(updatePasswordInfo.getBase64TokenId(), updatePasswordInfo.getBase64UserEmail(), true, contextInfo);

        // update user with new password
        if (isTokenVerified) {
            String userEmail = new String(Base64.decodeBase64(updatePasswordInfo.getBase64UserEmail()));
            UserEntity userByEmail = this.getUserByEmail(userEmail, contextInfo);
            userByEmail.setPassword(updatePasswordInfo.getNewPassword());
            UserEntity userEntity = this.updateUser(userByEmail, contextInfo);
        }
    }

    @Override
    @Timed(name = "changeState")
    public UserEntity changeState(String userId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        //validate given stateKey
        boolean contains = UserServiceConstants.userStates.contains(stateKey);
        if(!contains){
            ValidationResultInfo validationResultInfo = new ValidationResultInfo();
            validationResultInfo.setElement("stateKey");
            validationResultInfo.setLevel(ErrorLevel.ERROR);
            validationResultInfo.setMessage("provided stateKey is not valid ");
            errors.add(validationResultInfo);
            throw new DataValidationErrorException("Validation Failed due to errors ",errors);
        }

        UserEntity userEntity = this.getUserById(userId, contextInfo);
        String oldState = userEntity.getState();
        userEntity.setState(stateKey);
        userEntity = this.updateUser(userEntity,contextInfo);
        sendMailToTheUserOnChangeState(oldState,userEntity.getState(), userEntity);
        return userEntity;
    }

    private void sendMailToTheUserOnChangeState(String oldState ,String newState, UserEntity userEntity) {
        if(UserServiceConstants.USER_STATUS_APPROVAL_PENDING.equals(oldState) && UserServiceConstants.USER_STATUS_ACTIVE.equals(newState)){
            emailService.sendSimpleMessage(userEntity.getEmail(), "Account Request Approved!", "Your Account Request Has Been Approved Successfully!");
        }
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "createUser")
    public UserEntity createUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {
        UserValidator.validateResultEntitys(this, Constant.CREATE_VALIDATION, userEntity, contextInfo);
        userEntity = userRepository.save(userEntity);
        return userEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "updateUser")
    public UserEntity updateUser(UserEntity userEntity,
                                 ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException, InvalidParameterException {
        UserValidator.validateResultEntitys(this, Constant.UPDATE_VALIDATION, userEntity, contextInfo);
        userEntity = userRepository.save(userEntity);
        return userEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchUsers")
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
    @Timed(name = "getUserById")
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
    @Timed(name = "getUsers")
    public Page<UserEntity> getUsers(Pageable pageable,
                                     ContextInfo contextInfo) {
        Page<UserEntity> users = userRepository.findUsers(pageable);
        return users;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    @Timed(name = "validateUser")
    public List<ValidationResultInfo> validateUser(
            String validationTypeKey,
            UserEntity userEntity,
            ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {

        return UserValidator.validateCreateUpdateUser(this,
                validationTypeKey,
                userEntity,
                contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "getPrincipalUser")
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

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    @Timed(name = "searchRoles")
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
    @Timed(name = "getRoleById")
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
    @Timed(name = "getRoles")
    public Page<RoleEntity> getRoles(Pageable pageable,
                                     ContextInfo contextInfo) {
        Page<RoleEntity> roles = roleRepository.findRoles(pageable);
        return roles;
    }

}
