package com.argusoft.path.tht.systemconfiguration.security.handler;

import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class OnSsoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

            List authorities = new ArrayList();
            authorities.add(new SimpleGrantedAuthority("GOOGLE_LOGIN"));

            UserEntity loggedInUser = createUserIfNotExists(oauth2User, Constant.SUPER_USER_CONTEXT);
            oauth2User.getAttributes().put("userName", Constant.SUPER_USER_CONTEXT.getUsername());

            ContextInfo contextInfo = new ContextInfo(
                    oauth2User.<String>getAttribute("email"),
                    loggedInUser.getId(),
                    "N/A",
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
            System.out.println(oAuth2AccessToken.getValue());
            System.out.println(oAuth2AccessToken.getExpiresIn());
            System.out.println(oAuth2AccessToken.getRefreshToken().getValue());
            System.out.println(oAuth2AccessToken.getScope());
            System.out.println(oAuth2AccessToken.getTokenType());
//            response.sendRedirect("http://localhost:8081/api/user");
            response.getWriter().write(oAuth2AccessToken.getValue());
            response.getWriter().flush();
        } catch (Exception e) {
            response.setStatus(500);
            e.printStackTrace();
        }
    }

    private UserEntity createUserIfNotExists(OAuth2User oauth2User, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException, DoesNotExistException, DataValidationErrorException {
        UserSearchFilter searchFilter = new UserSearchFilter();
        searchFilter.setEmail(oauth2User.<String>getAttribute("email"));
        Page<UserEntity> usersPage = null;

        usersPage = userService.searchUsers(
                new ArrayList<>(),
                searchFilter,
                Constant.SINGLE_VALUE_PAGE,
                contextInfo);

        if (usersPage.getTotalElements() == 0) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(oauth2User.<String>getAttribute("email"));
            userEntity.setName(oauth2User.<String>getAttribute("name"));
            return userService.createUser(userEntity,contextInfo);
        } else {
            return usersPage.getContent().get(0);
        }
    }
}