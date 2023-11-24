package com.argusoft.path.tht.usermanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.constant.TokenVerificationConstants;
import com.argusoft.path.tht.usermanagement.models.entity.TokenVerificationEntity;
import com.argusoft.path.tht.usermanagement.repository.TokenVerificationRepository;
import com.argusoft.path.tht.usermanagement.service.TokenVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenVerificationServiceImpl implements TokenVerificationService {

    @Autowired
    private TokenVerificationRepository tokenVerificationRepository;

    @Override
    public TokenVerificationEntity createTokenVerification(TokenVerificationEntity tokenVerificationEntity, ContextInfo contextInfo) {
        return tokenVerificationRepository.save(tokenVerificationEntity);
    }

    @Override
    public Optional<TokenVerificationEntity> getActiveTokenByIdAndUserId(String token, String userId, ContextInfo contextInfo) {
        return tokenVerificationRepository.findActiveTokenByIdAndUserInfo(TokenVerificationConstants.TOKEN_STATUS_ACTIVE,token, userId);
    }

    public TokenVerificationEntity getTokenById(String token, ContextInfo contextInfo){
        Optional<TokenVerificationEntity> tokenVerificationById = tokenVerificationRepository.findById(token);
        return tokenVerificationById.orElse(null);
    }


}
