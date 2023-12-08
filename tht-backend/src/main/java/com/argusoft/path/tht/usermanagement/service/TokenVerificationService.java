package com.argusoft.path.tht.usermanagement.service;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;

import java.util.List;
import java.util.Optional;

public interface TokenVerificationService {

    public TokenVerificationEntity createTokenVerification(TokenVerificationEntity tokenVerificationEntity,
                                                           ContextInfo contextInfo);

    public Optional<TokenVerificationEntity> getActiveTokenByIdAndUserIdAndType(String token,
                                                                                String userId,
                                                                                String type, ContextInfo contextInfo);

    public List<TokenVerificationEntity> getAllTokenVerificationEntityByTypeAndUser(String type,String userId, ContextInfo contextInfo);

    public TokenVerificationEntity getTokenById(String tokenId, ContextInfo contextInfo) throws DoesNotExistException;


    public Boolean verifyUserToken(String base64TokenId, String base64EmailId, ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, OperationFailedException, MissingParameterException, VersionMismatchException, InvalidParameterException, PermissionDeniedException;
    

    public TokenVerificationEntity generateTokenForUserAndSendEmailForType(String userId, String type ,ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, OperationFailedException, MissingParameterException, PermissionDeniedException;

    public TokenVerificationEntity updateTokenVerificationEntity(String tokenId, TokenVerificationEntity tokenVerification, ContextInfo contextInfo) throws DoesNotExistException;


}