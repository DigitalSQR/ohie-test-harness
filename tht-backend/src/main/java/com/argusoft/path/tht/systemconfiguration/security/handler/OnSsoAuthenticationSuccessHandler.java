package com.argusoft.path.tht.systemconfiguration.security.handler;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.security.custom.CustomOauth2User;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OnSsoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();
            oauth2User.getCustomAttributes().put("userName", Constant.SUPER_USER_CONTEXT.getUsername());
            UserEntity loggedInUser = createUserIfNotExists(oauth2User, Constant.SUPER_USER_CONTEXT);

            if (Objects.equals(UserServiceConstants.USER_STATUS_ACTIVE, loggedInUser.getState())) {

                List<GrantedAuthority> authorities
                        = loggedInUser.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getId()))
                        .collect(Collectors.toList());

                ContextInfo contextInfo = new ContextInfo(
                        oauth2User.<String>getAttribute("email"),
                        loggedInUser.getId(),
                        "password",
                        true,
                        true,
                        true,
                        true,
                        authorities);

                OAuth2Request oauth2Request = new OAuth2Request(
                        new HashMap(),
                        "tht",
                        authorities,
                        true,
                        new HashSet(Arrays.asList("write")),
                        new HashSet(Arrays.asList("resource_id")),
                        null,
                        new HashSet(Arrays.asList("code")),
                        new HashMap());

                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(contextInfo, "N/A", authorities);

                OAuth2Authentication auth = new OAuth2Authentication(oauth2Request, authenticationToken);

                OAuth2AccessToken oAuth2AccessToken = defaultTokenServices.createAccessToken(auth);
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("access_token", oAuth2AccessToken.getValue());
                responseMap.put("token_type", oAuth2AccessToken.getTokenType());
                responseMap.put("refresh_token", oAuth2AccessToken.getRefreshToken().getValue());
                responseMap.put("expires_in", oAuth2AccessToken.getExpiresIn());
                responseMap.put("scope", String.join(" ", oAuth2AccessToken.getScope()));

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(responseMap);

                response.setContentType("application/json");
                response.getWriter().write(jsonResponse);
                response.getWriter().flush();
            } else if (Objects.equals(UserServiceConstants.USER_STATUS_VERIFICATION_PENDING, loggedInUser.getState())) {
                //TODO: Add appropriate message.
            } else if (Objects.equals(UserServiceConstants.USER_STATUS_APPROVAL_PENDING, loggedInUser.getState())) {
                //TODO: Add appropriate message.
            } else if (Objects.equals(UserServiceConstants.USER_STATUS_REJECTED, loggedInUser.getState())) {
                //TODO: Add appropriate message.
            } else if (Objects.equals(UserServiceConstants.USER_STATUS_INACTIVE, loggedInUser.getState())) {
                //TODO: Add appropriate message.
            } else {
                //TODO: Add appropriate message.
            }

        } catch (Exception e) {
            //TODO: Add appropriate message.
            response.setStatus(500);
            e.printStackTrace();
        }
    }

    private UserEntity createUserIfNotExists(OAuth2User oauth2User, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException, DoesNotExistException, DataValidationErrorException {
        try {
            return userService.getUserByEmail(oauth2User.<String>getAttribute("email"), contextInfo);
        } catch (DoesNotExistException ex) {
            //If user not exists then create as Assessee.
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(oauth2User.<String>getAttribute("email"));
            userEntity.setName(oauth2User.<String>getAttribute("name"));

            return userService.registerAssessee(userEntity, contextInfo);
        }
    }
}