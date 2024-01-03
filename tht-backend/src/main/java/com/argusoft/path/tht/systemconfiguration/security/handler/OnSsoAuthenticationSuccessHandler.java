package com.argusoft.path.tht.systemconfiguration.security.handler;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.security.custom.CustomOauth2User;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OnSsoAuthenticationSuccessHandler to handle authorization and token.
 *
 * @author Dhruv
 */
@Component
public class OnSsoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    @Autowired
    private UserService userService;

    @Value("${frontend.google.success}")
    private String successCallbackEndUrl;

    @Override
    @Transactional
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

               /* ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(responseMap);

                response.setContentType("application/json");
                response.getWriter().write(jsonResponse);
                response.getWriter().flush();*/

                String s = appendParamsToUrl(successCallbackEndUrl, responseMap);
                response.sendRedirect(s);
            } else if (Objects.equals(UserServiceConstants.USER_STATUS_VERIFICATION_PENDING, loggedInUser.getState())) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Pending email verification.");

                String s = appendParamsToUrl(successCallbackEndUrl, responseMap);
                response.sendRedirect(s);
            } else if (Objects.equals(UserServiceConstants.USER_STATUS_APPROVAL_PENDING, loggedInUser.getState())) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Pending admin approval.");

                String s = appendParamsToUrl(successCallbackEndUrl, responseMap);
                response.sendRedirect(s);
            } else if (Objects.equals(UserServiceConstants.USER_STATUS_REJECTED, loggedInUser.getState())) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Admin approval has been rejected.");

                String s = appendParamsToUrl(successCallbackEndUrl, responseMap);
                response.sendRedirect(s);
            } else {
                //Only left UserServiceConstants.USER_STATUS_INACTIVE,
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "User is inactive.");

                String s = appendParamsToUrl(successCallbackEndUrl, responseMap);
                response.sendRedirect(s);
            }
        } catch (Exception e) {
            //TODO: Add appropriate message.
            response.setStatus(500);
            e.printStackTrace();
        }
    }

    public static String appendParamsToUrl(String baseUrl, Map<String, Object> parameters) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        // Check if the base URL already contains a query parameter
        boolean hasQuery = baseUrl.contains("?");

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            try {
                // Encode the parameter names and values to handle special characters
                String encodedKey = URLEncoder.encode(entry.getKey(), "UTF-8");
                String encodedValue = URLEncoder.encode(entry.getValue().toString(), "UTF-8");

                // Append the key-value pair to the URL
                if (!hasQuery) {
                    urlBuilder.append("?");
                    hasQuery = true;
                } else {
                    urlBuilder.append("&");
                }

                urlBuilder.append(encodedKey)
                        .append("=")
                        .append(encodedValue);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(); // Handle encoding exception as needed
            }
        }

        return urlBuilder.toString();
    }

    private UserEntity createUserIfNotExists(OAuth2User oauth2User, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DoesNotExistException, DataValidationErrorException {
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