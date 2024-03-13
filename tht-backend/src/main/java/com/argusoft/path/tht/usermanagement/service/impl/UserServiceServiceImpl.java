package com.argusoft.path.tht.usermanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.utils.CommonStateChangeValidator;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.Module;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.email.service.EmailService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.EncryptDecrypt;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.utils.CommonUtil;
import com.argusoft.path.tht.systemconfiguration.utils.ValidationUtils;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.filter.RoleSearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.filter.UserSearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.models.dto.ResetPasswordInfo;
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
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

/**
 * This UserServiceServiceImpl contains implementation for User service.
 *
 * @author Dhruv
 */
@Service
public class UserServiceServiceImpl implements UserService {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserServiceServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    UserService userService;

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
    public Boolean logout(ContextInfo contextInfo)
            throws OperationFailedException {
        return defaultTokenServices.revokeToken(contextInfo.getAccessToken());
    }

    @Override
    public UserEntity getUserByEmail(String email, ContextInfo contextInfo)
            throws DoesNotExistException, InvalidParameterException {
        if(!StringUtils.hasLength(email)) {
            throw new DoesNotExistException("User does not found with email : " + email);
        }
        UserSearchCriteriaFilter userSearchCriteriaFilter = new UserSearchCriteriaFilter();
        userSearchCriteriaFilter.setEmail(email);
        List<UserEntity> userEntities = this.searchUsers(userSearchCriteriaFilter, contextInfo);
        return userEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("User does not found with email : " + email));
    }

    @Override
    public UserEntity registerAssessee(UserEntity userEntity, ContextInfo contextInfo)
            throws DoesNotExistException, OperationFailedException, InvalidParameterException, DataValidationErrorException, MessagingException, IOException {
        defaultValueRegisterAssessee(userEntity);
        userEntity = this.createUser(userEntity, contextInfo);
        return userEntity;
    }

    @Override
    public void createForgotPasswordRequestAndSendEmail(String userEmail, ContextInfo contextInfo) {
        UserEntity userByEmail = null;
        try {
            userByEmail = this.getUserByEmail(userEmail, contextInfo);
            TokenVerificationEntity tokenVerification = tokenVerificationService.generateTokenForUserAndSendEmailForType(userByEmail.getId(), TokenTypeEnum.FORGOT_PASSWORD.getKey(), contextInfo);
        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + UserServiceServiceImpl.class.getSimpleName(), e);
            // ignore it, no need to show that they are not exists in DB
            //TODO add log
        }
    }

    @Override
    public void updatePasswordWithVerificationToken(UpdatePasswordInfo updatePasswordInfo, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, DoesNotExistException, OperationFailedException, VersionMismatchException {

        contextInfo.setModule(Module.FORGOT_PASSWORD);

        //trim values
        updatePasswordInfo.trimObject();

        UserValidator.validateUpdatePasswordInfoAgainstNullValues(updatePasswordInfo);

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
    public UserEntity resetPassword(ResetPasswordInfo resetPasswordInfo, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {

        contextInfo.setModule(Module.RESET_PASSWORD);

        List<ValidationResultInfo> errors = new ArrayList<>();
        UserEntity principalUser = this.getPrincipalUser(contextInfo);
        resetPasswordInfo.trimObject();
        UserValidator.validatePasswords(resetPasswordInfo.getOldPassword(), resetPasswordInfo.getNewPassword(), principalUser.getPassword(), errors);

        if (ValidationUtils.containsErrors(errors, ErrorLevel.ERROR)) {
            throw new DataValidationErrorException(
                    ValidateConstant.ERRORS,
                    errors);
        }

        principalUser.setPassword(resetPasswordInfo.getNewPassword());
        return updateUser(principalUser, contextInfo);
    }

    @Override
    public UserEntity changeState(String userId, String stateKey, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException, MessagingException, IOException {
        List<ValidationResultInfo> errors = new ArrayList<>();

        UserEntity userEntity = this.getUserById(userId, contextInfo);
        String oldState = userEntity.getState();

        UserValidator.validateChangeState(userEntity, this, errors, stateKey, contextInfo);

        CommonStateChangeValidator.validateStateChange(UserServiceConstants.USER_STATUS, UserServiceConstants.USER_STATUS_MAP, userEntity.getState(), stateKey, errors);

        userEntity.setState(stateKey);

        userEntity = this.updateUser(userEntity, contextInfo);

        sendMailToTheUserOnChangeState(oldState, userEntity.getState(), userEntity, contextInfo);

        if (stateKey.equals(UserServiceConstants.USER_STATUS_INACTIVE)) {
            revokeAccessTokenOnStateChange(UserServiceConstants.CLIENT_ID, userEntity.getId());
        }
        return userEntity;
    }

    @Override
    public void resendVerification(String userEmail, ContextInfo contextInfo) {
        UserEntity userByEmail = null;
        try {
            userByEmail = this.getUserByEmail(userEmail, contextInfo);
            TokenVerificationEntity tokenVerification = tokenVerificationService.generateTokenForUserAndSendEmailForType(userByEmail.getId(), TokenTypeEnum.VERIFICATION.getKey(), contextInfo);
        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + UserServiceServiceImpl.class.getSimpleName(), e);
            // ignore it, no need to show that they are not exists in DB
            //TODO add log
        }
    }

    private void sendMailToTheUserOnChangeState(String oldState, String newState, UserEntity userEntity, ContextInfo contextInfo) throws MessagingException, IOException, InvalidParameterException, DoesNotExistException {
        if (UserServiceConstants.USER_STATUS_APPROVAL_PENDING.equals(oldState) && UserServiceConstants.USER_STATUS_ACTIVE.equals(newState)) {
            //message assessee if their account is approved by admin
            emailService.accountApprovedMessage(userEntity.getEmail(), userEntity.getName());
        } else if (UserServiceConstants.USER_STATUS_APPROVAL_PENDING.equals(oldState) && UserServiceConstants.USER_STATUS_INACTIVE.equals(newState)) {
            //message assessee if their account is rejected by admin
            emailService.accountRejectedMessage(userEntity.getEmail(), userEntity.getName());
        } else if (UserServiceConstants.USER_STATUS_VERIFICATION_PENDING.equals(oldState) && UserServiceConstants.USER_STATUS_APPROVAL_PENDING.equals(newState)) {
            //Fetch all admins
            List<UserEntity> admins = userService.getUsersByRole("role.admin", contextInfo);

            //Message all admins stating approval pending
            admins.forEach(admin -> emailService.verifiedAndWaitingForAdminApproval(admin.getEmail(), admin.getName(), userEntity.getEmail()));
        } else if (UserServiceConstants.USER_STATUS_ACTIVE.equals(oldState) && UserServiceConstants.USER_STATUS_INACTIVE.equals(newState)) {
            emailService.accountInactiveMessage(userEntity.getEmail(), userEntity.getName());
        }
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public UserEntity createUser(UserEntity userEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException,
            DoesNotExistException, MessagingException, IOException {

        if (contextInfo.getModule() == Module.OAUTH2) {
            //If method get called on google Oauth2 login then verification of email is not needed.
            userEntity.setState(UserServiceConstants.USER_STATUS_APPROVAL_PENDING);
        } else if (contextInfo.isAdmin()) {
            userEntity.setState(UserServiceConstants.USER_STATUS_ACTIVE);
        } else {
            userEntity.setState(UserServiceConstants.USER_STATUS_VERIFICATION_PENDING);
        }

        UserValidator.validateCreateUpdateUser(this, Constant.CREATE_VALIDATION, userEntity, contextInfo);
        if (StringUtils.hasLength(userEntity.getPassword())) {
            userEntity.setPassword(EncryptDecrypt.hashString(userEntity.getPassword()));
        }
        userEntity = userRepository.saveAndFlush(userEntity);

        //On verification pending state, send mail for the email verification
        if (Objects.equals(userEntity.getState(), UserServiceConstants.USER_STATUS_VERIFICATION_PENDING)) {
            tokenVerificationService.generateTokenForUserAndSendEmailForType(userEntity.getId(), TokenTypeEnum.VERIFICATION.getKey(), contextInfo);
        }
        return userEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public UserEntity updateUser(UserEntity userEntity,
            ContextInfo contextInfo)
            throws OperationFailedException,
            VersionMismatchException,
            DataValidationErrorException, InvalidParameterException {
        UserValidator.validateCreateUpdateUser(this, Constant.UPDATE_VALIDATION, userEntity, contextInfo);
        if (contextInfo.getModule() == Module.RESET_PASSWORD || contextInfo.getModule() == Module.FORGOT_PASSWORD) {
            userEntity.setPassword(EncryptDecrypt.hashString(userEntity.getPassword()));
        }
        userEntity = userRepository.saveAndFlush(userEntity);
        return userEntity;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<UserEntity> searchUsers(
            UserSearchCriteriaFilter userSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws
            InvalidParameterException {
        Specification<UserEntity> userEntitySpecification = userSearchFilter.buildSpecification(pageable, contextInfo);
        return this.userRepository.findAll(userEntitySpecification, CommonUtil.getPageable(pageable));
    }

    @Override
    public List<UserEntity> searchUsers(
            UserSearchCriteriaFilter userSearchFilter,
            ContextInfo contextInfo)
            throws
            InvalidParameterException {
        Specification<UserEntity> userEntitySpecification = userSearchFilter.buildSpecification(contextInfo);
        return this.userRepository.findAll(userEntitySpecification);
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
        if (!StringUtils.hasLength(userId)) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + UserServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("userId is missing");
        }
        UserSearchCriteriaFilter userSearchCriteriaFilter = new UserSearchCriteriaFilter(userId);
        List<UserEntity> userEntities = this.searchUsers(userSearchCriteriaFilter, contextInfo);
        return userEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("User does not found with id : " + userId));
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

        return UserValidator.validateUser(this,
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
    public UserEntity getPrincipalUser(ContextInfo contextInfo)
            throws DoesNotExistException, InvalidParameterException {
        return this.getUserById(contextInfo.getUsername(), contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Override
    public Page<RoleEntity> searchRoles(
            RoleSearchCriteriaFilter roleSearchFilter,
            Pageable pageable,
            ContextInfo contextInfo)
            throws
            InvalidParameterException {
        Specification<RoleEntity> roleEntitySpecification = roleSearchFilter.buildSpecification(contextInfo);
        return roleRepository.findAll(roleEntitySpecification, pageable);
    }

    @Override
    public List<RoleEntity> searchRoles(
            RoleSearchCriteriaFilter roleSearchFilter,
            ContextInfo contextInfo)
            throws
            InvalidParameterException {
        Specification<RoleEntity> roleEntitySpecification = roleSearchFilter.buildSpecification(contextInfo);
        return roleRepository.findAll(roleEntitySpecification);
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
        if (!StringUtils.hasLength(roleId)) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + UserServiceServiceImpl.class.getSimpleName());
            throw new InvalidParameterException("roleId can not be empty");
        }
        RoleSearchCriteriaFilter roleSearchCriteriaFilter = new RoleSearchCriteriaFilter(roleId);
        List<RoleEntity> roleEntities = this.searchRoles(roleSearchCriteriaFilter, contextInfo);
        return roleEntities.stream()
                .findFirst()
                .orElseThrow(() -> new DoesNotExistException("Role does not found with id : " + roleId));
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

    private void defaultValueRegisterAssessee(UserEntity userEntity) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(UserServiceConstants.ROLE_ID_ASSESSEE);
        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleEntity);
    }

    @Override
    public void revokeAccessTokenOnStateChange(String clientId, String userName) {
        Collection<OAuth2AccessToken> accessToken = tokenStore.findTokensByClientIdAndUserName(clientId, userName);
        for (OAuth2AccessToken oAuth2AccessToken : accessToken) {
            defaultTokenServices.revokeToken(oAuth2AccessToken.getValue());
        }
    }

    @Override
    public List<UserEntity> getUsersByRole(String role, ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException {
        UserSearchCriteriaFilter userSearchCriteriaFilter = new UserSearchCriteriaFilter();
        userSearchCriteriaFilter.setRole(role);
        List<UserEntity> userEntities = this.searchUsers(userSearchCriteriaFilter, contextInfo);
        return Optional.ofNullable(userEntities)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new DoesNotExistException("No user found with role : " + role));
    }

}
