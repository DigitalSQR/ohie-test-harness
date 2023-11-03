package com.argusoft.path.tht.systemconfiguration.security.handler;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.security.custom.CustomOAuth2User;
import com.argusoft.path.tht.systemconfiguration.security.filters.Provider;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OnSsoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    private ContextInfo contextInfo;

    public OnSsoAuthenticationSuccessHandler(UserService userService, ContextInfo contextInfo) {
        this.userService = userService;
        this.contextInfo = contextInfo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        try {
            createUserIfNotExists(oauthUser,contextInfo);
        } catch (InvalidParameterException | MissingParameterException | PermissionDeniedException | OperationFailedException | DoesNotExistException | DataValidationErrorException e) {
            // TODO logger
        }
    }

    private void createUserIfNotExists(CustomOAuth2User oauthUser, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException, DoesNotExistException, DataValidationErrorException {

        UserEntity userByEmail = userService.getUserByUsername(oauthUser.getEmail(), contextInfo);
        if(userByEmail==null){
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(oauthUser.getEmail());
            userEntity.setName(oauthUser.getName());
            userEntity.setProvider(Provider.GOOGLE.name());
            userByEmail = userService.createUser(userEntity,contextInfo);
        }
    }


}
