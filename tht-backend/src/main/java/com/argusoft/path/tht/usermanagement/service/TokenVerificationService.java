package com.argusoft.path.tht.usermanagement.service;

import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;

import java.util.Optional;

public interface TokenVerificationService {

    public TokenVerificationEntity createTokenVerification(TokenVerificationEntity tokenVerificationEntity,
                                                           ContextInfo contextInfo);

    public Optional<TokenVerificationEntity> getActiveTokenByIdAndUserId(String token,
                                                                         String userId,
                                                                         ContextInfo contextInfo);

    public TokenVerificationEntity getTokenById(String tokenId,
                                                ContextInfo contextInfo);


}
