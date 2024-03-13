package com.argusoft.path.tht.usermanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * This interface provides contract for TokenVerification API.
 *
 * @author Hardik
 */
public interface TokenVerificationService {

    /**
     * Create token verification details
     *
     * @param tokenVerificationEntity the token verification data
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return tokenVerification data
     */
    public TokenVerificationEntity createTokenVerification(TokenVerificationEntity tokenVerificationEntity,
            ContextInfo contextInfo);

    /**
     * Retrieve active token verification data by its token, id and type
     *
     * @param token token value
     * @param userId the user id
     * @param type the verification type
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return tokenVerification data
     */
    public Optional<TokenVerificationEntity> getActiveTokenByIdAndUserIdAndType(String token,
            String userId,
            String type, ContextInfo contextInfo);

    /**
     * Retrieve all tokens verification data by its type and user id
     *
     * @param type the verification type
     * @param userId the user id
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return list of TokenVerification data
     */
    public List<TokenVerificationEntity> getAllTokenVerificationEntityByTypeAndUser(String type, String userId, ContextInfo contextInfo);

    /**
     * Retrieve token verification data by token id
     *
     * @param tokenId the token id
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return tokenVerification data
     * @throws DoesNotExistException token verification data does not exist
     */
    public TokenVerificationEntity getTokenById(String tokenId, ContextInfo contextInfo) throws DoesNotExistException;

    /**
     * Verifies user token
     *
     * @param base64TokenId token id of type base64
     * @param base64EmailId email id of user of type base64
     * @param verifyForgotPasswordTokenOnly boolean value to verify forgot
     * password token
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return boolean value based on token verification
     * @throws DoesNotExistException token verification data does not exist
     * @throws DataValidationErrorException supplied data is invalid
     * @throws OperationFailedException unable to complete request
     * @throws VersionMismatchException optimistic locking failure or the action
     * was attempted on an out of date version
     * @throws InvalidParameterException ContextInfo is not valid
     */
    public Boolean verifyUserToken(String base64TokenId, String base64EmailId, Boolean verifyForgotPasswordTokenOnly, ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException, InvalidParameterException;

    /**
     * Generate token for user and send email
     *
     * @param userId id of user
     * @param type type of token
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return token verification data
     * @throws DoesNotExistException token verification data does not exist
     * @throws InvalidParameterException ContextInfo is not valid
     * @throws OperationFailedException unable to complete request
     * @throws MessagingException unable to send mail
     * @throws IOException unalbe to perform IO operation
     */
    public TokenVerificationEntity generateTokenForUserAndSendEmailForType(String userId, String type, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, OperationFailedException, MessagingException, IOException;

    /**
     * Update token verification data
     *
     * @param tokenId id of token to retrieve token verification data
     * @param tokenVerification data to update token verification data
     * @param contextInfo information containing the principalId and locale
     * information about the caller of service operation
     * @return updated token verification data
     * @throws DoesNotExistException token verification data does not exist
     */
    public TokenVerificationEntity updateTokenVerificationEntity(String tokenId, TokenVerificationEntity tokenVerification, ContextInfo contextInfo) throws DoesNotExistException;

}
