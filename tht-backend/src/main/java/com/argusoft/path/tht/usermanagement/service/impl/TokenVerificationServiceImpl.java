package com.argusoft.path.tht.usermanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.email.service.EmailService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.constant.TokenVerificationConstants;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.enums.TokenTypeEnum;
import com.argusoft.path.tht.usermanagement.repository.TokenVerificationRepository;
import com.argusoft.path.tht.usermanagement.service.TokenVerificationService;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

/**
 * This TokenVerificationServiceImpl contains implementation for
 * TokenVerification service.
 *
 * @author Hardik
 */
@Service
public class TokenVerificationServiceImpl implements TokenVerificationService {

    @Autowired
    private TokenVerificationRepository tokenVerificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private static void checkForValidTokenTypeWithVerifyingInEnum(String tokenType) throws InvalidParameterException {
        boolean validEnum = TokenTypeEnum.isValidKey(tokenType);
        if (!validEnum) {
            throw new InvalidParameterException("token type outside of the provided support => " + tokenType);
        }
    }

    @Override
    public TokenVerificationEntity createTokenVerification(TokenVerificationEntity tokenVerificationEntity, ContextInfo contextInfo) {
        return tokenVerificationRepository.saveAndFlush(tokenVerificationEntity);
    }

    @Override
    public Optional<TokenVerificationEntity> getActiveTokenByIdAndUserIdAndType(String token, String userId, String type, ContextInfo contextInfo) {
        return tokenVerificationRepository.findActiveTokenByIdAndUserAndType(TokenVerificationConstants.TOKEN_STATUS_ACTIVE, token, userId, type);
    }

    @Override
    public List<TokenVerificationEntity> getAllTokenVerificationEntityByTypeAndUser(String type, String userId, ContextInfo contextInfo) {
        return tokenVerificationRepository.findAllTokenVerificationsByTypeAndUser(type, userId);
    }

    @Override
    public TokenVerificationEntity getTokenById(String token, ContextInfo contextInfo) throws DoesNotExistException {
        Optional<TokenVerificationEntity> tokenVerificationById = tokenVerificationRepository.findById(token);
        return tokenVerificationById.orElseThrow(()
                -> new DoesNotExistException("Token VerificationEntity does not exist with token " + token));
    }

    @Override
    public Boolean verifyUserToken(String base64TokenId, String base64EmailId,
            Boolean verifyForgotPasswordTokenOnly, ContextInfo contextInfo)
            throws DoesNotExistException,
            DataValidationErrorException,
            OperationFailedException,
            VersionMismatchException,
            InvalidParameterException {

        String tokenDecodedBase64 = new String(Base64.decodeBase64(base64TokenId));
        String emailDecodedBase64 = new String(Base64.decodeBase64(base64EmailId));

        UserEntity userByEmail = userService.getUserByEmail(emailDecodedBase64, contextInfo);
        TokenVerificationEntity tokenById = this.getTokenById(tokenDecodedBase64, contextInfo);
        Optional<TokenVerificationEntity> activeTokenByIdAndUserId = this.getActiveTokenByIdAndUserIdAndType(tokenDecodedBase64, userByEmail.getId(), tokenById.getType(), contextInfo);

        // set inactive token verification record
        if (activeTokenByIdAndUserId.isPresent()) {
            TokenVerificationEntity tokenVerification = activeTokenByIdAndUserId.get();

            if (TokenTypeEnum.FORGOT_PASSWORD.getKey().equals(tokenVerification.getType()) && (verifyForgotPasswordTokenOnly == null || !verifyForgotPasswordTokenOnly)) {
                throw new OperationFailedException("Can not verify this token currently");
            }

            if (TokenTypeEnum.FORGOT_PASSWORD.getKey().equals(tokenVerification.getType())) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.HOUR, -24);
                Date minus24HoursFromCurrentDate = c.getTime();

                if (tokenVerification.getCreatedAt().before(minus24HoursFromCurrentDate)) {
                    // even that old then can't update
                    throw new OperationFailedException("Link is older than expected, try again!");
                }
            }

            tokenVerification.setState(TokenVerificationConstants.TOKEN_STATUS_INACTIVE);
            this.updateTokenVerificationEntity(tokenVerification.getId(), tokenVerification, contextInfo);

            // call back tasks
            if (TokenTypeEnum.FORGOT_PASSWORD.getKey().equals(tokenVerification.getType())) {
                // nothing as of now

            } else if (TokenTypeEnum.VERIFICATION.getKey().equals(tokenVerification.getType())) {
                if (UserServiceConstants.USER_STATUS_VERIFICATION_PENDING.equals(userByEmail.getState())) {
                    if (userByEmail.getRoles().stream().anyMatch(roleEntity -> roleEntity.getId().equals(UserServiceConstants.ROLE_ID_ASSESSEE))) {
                        userByEmail.setState(UserServiceConstants.USER_STATUS_APPROVAL_PENDING);
                    } else {
                        userByEmail.setState(UserServiceConstants.USER_STATUS_ACTIVE);
                    }
                    userByEmail = userService.updateUser(userByEmail, contextInfo);
                }
            }
        }
        return activeTokenByIdAndUserId.isPresent();
    }

    @Override
    public TokenVerificationEntity generateTokenForUserAndSendEmailForType(String userId,
            String tokenType,
            ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            OperationFailedException, MessagingException, IOException {

        checkForValidTokenTypeWithVerifyingInEnum(tokenType);
        UserEntity userById = getUserByIdAndVerifyForEmailExistense(userId, contextInfo);
        if (Objects.equals(tokenType, TokenTypeEnum.VERIFICATION.getKey()) && !Objects.equals(userById.getState(), UserServiceConstants.USER_STATUS_VERIFICATION_PENDING)) {
            throw new InvalidParameterException("User " + userById.getEmail() + " is already verified");
        }
        // inactive all the verification tokens created in past for this user
        List<TokenVerificationEntity> allTokenVerificationEntityByUser
                = this.getAllTokenVerificationEntityByTypeAndUser(tokenType, userById.getId(), contextInfo);
        for (TokenVerificationEntity tokenVerification : allTokenVerificationEntityByUser) {
            tokenVerification.setState(TokenVerificationConstants.TOKEN_STATUS_INACTIVE);
            this.updateTokenVerificationEntity(tokenVerification.getId(), tokenVerification, contextInfo);
        }

        // generate new token for this user
        TokenVerificationEntity tokenVerification = new TokenVerificationEntity();
        tokenVerification.setUserEntity(userById);
        tokenVerification.setState(TokenVerificationConstants.TOKEN_STATUS_ACTIVE);
        tokenVerification.setType(tokenType);
        this.createTokenVerification(tokenVerification, contextInfo);

        // send email
        String encodedBase64TokenVerificationId = new String(Base64.encodeBase64(tokenVerification.getId().getBytes()));
        String emailIdBase64 = new String(Base64.encodeBase64(userById.getEmail().getBytes()));

        if (TokenTypeEnum.VERIFICATION.getKey().equals(tokenVerification.getType())) {
            emailService.verifyEmailMessage(userById.getEmail(), userById.getName(), "https://tht.argusoft.com/email/verify/" + emailIdBase64 + "/" + encodedBase64TokenVerificationId);
        } else if (TokenTypeEnum.FORGOT_PASSWORD.getKey().equals(tokenVerification.getType())) {
            emailService.forgotPasswordMessage(userById.getEmail(), userById.getName(), "https://tht.argusoft.com/reset/cred/" + emailIdBase64 + "/" + encodedBase64TokenVerificationId);
        }
        return tokenVerification;
    }

    private UserEntity getUserByIdAndVerifyForEmailExistense(String userId, ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException, InvalidParameterException {
        UserEntity userById = userService.getUserById(userId, contextInfo);
        // check if user has email id
        if (userById.getEmail() == null) {
            throw new OperationFailedException("Provided user does not have email id");
        }
        return userById;
    }

    @Override
    public TokenVerificationEntity updateTokenVerificationEntity(String tokenId, TokenVerificationEntity tokenVerification, ContextInfo contextInfo) throws DoesNotExistException {
        TokenVerificationEntity tokenById = this.getTokenById(tokenId, contextInfo);
        tokenVerification.setId(tokenById.getId());
        tokenVerification = tokenVerificationRepository.saveAndFlush(tokenVerification);
        return tokenVerification;
    }
}
