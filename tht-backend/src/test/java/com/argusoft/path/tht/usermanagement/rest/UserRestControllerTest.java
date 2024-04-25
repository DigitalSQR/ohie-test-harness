package com.argusoft.path.tht.usermanagement.rest;

import com.argusoft.path.tht.TestingHarnessToolRestTestConfiguration;
import com.argusoft.path.tht.notification.mock.NotificationServiceMockImpl;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.usermanagement.mock.UserServiceMockImpl;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.dto.ResetPasswordInfo;
import com.argusoft.path.tht.usermanagement.models.dto.RoleInfo;
import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.*;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class UserRestControllerTest extends TestingHarnessToolRestTestConfiguration {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    UserServiceMockImpl userServiceMock;

    @Autowired
    NotificationServiceMockImpl notificationServiceMock;


    @BeforeEach
    @Override
    public void init() {
        super.init();
        userServiceMock.init();
        super.login("noreplytestharnesstool@gmail.com",
                "password",
                webTestClient);
    }

    @AfterEach
    void after() {
        notificationServiceMock.clear();
    }


    @Test
    void registerAssess(){
        UserInfo userInfo = new UserInfo();
        userInfo.setId("user.10");
        userInfo.setName("Dummy User10");
        userInfo.setEmail("dummyuser10@testmail.com");
        userInfo.setPassword("password");



        UserInfo createUser = this.webTestClient
                .post()
                .uri("/user/register") // Assuming "/register" is the endpoint for registering a user
                .body(BodyInserters.fromValue(userInfo)) // Assuming userInfo is the object containing user information
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class) // Assuming UserInfo is the class representing user information
                .returnResult()
                .getResponseBody();

        assertEquals("user.10", createUser.getId());
    }


    @Test
    void testForgetPassword(){
        ValidationResultInfo vris = this.webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/forgot/password")
                        .queryParam("userEmail", "dummyuser1@testmail.com")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("You will receive email if already registered !", vris.getMessage());
    }


//    @Test
//    void testUpdatePassword(){
//        UpdatePasswordInfo updatePasswordInfo = new UpdatePasswordInfo();
//        updatePasswordInfo.setNewPassword("newpassword");
//        updatePasswordInfo.setBase64TokenId("YThiMmRiZjctMzBmYy00NDgzLWFkNjQtMDQ5NjQ2OTRkNzRl");
//        updatePasswordInfo.setBase64UserEmail("cm9oaXQuY2hpbnUuc29uaUBnbWFpbC5jb20=");
//
//        ValidationResultInfo vris = this.webTestClient
//                .post()
//                .uri("/user/update/password/") // Assuming "/register" is the endpoint for registering a user
//                .body(BodyInserters.fromValue(updatePasswordInfo)) // Assuming userInfo is the object containing user information
//                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
//                .header(ACCEPT, APPLICATION_JSON_VALUE)
//                .exchange()
//                .expectStatus()
//                .isEqualTo(OK)
//                .expectBody(ValidationResultInfo.class) // Assuming UserInfo is the class representing user information
//                .returnResult()
//                .getResponseBody();
//
//        assertEquals("Password Updated Successfully!", vris.getMessage());
//
//    }


    @Test
    void testUpdateUser(){
        UserInfo userInfo = this.webTestClient
                .get()
                .uri("/user/{userId}", "SYSTEM_USER")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class)
                .returnResult()
                .getResponseBody();
        assertEquals("SYSTEM_USER", userInfo.getId());

        userInfo.setName("Authenticate");

        UserInfo updateUser = this.webTestClient
                .put()
                .uri("/user")
                .body(BodyInserters.fromValue(userInfo))
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class)
                .returnResult()
                .getResponseBody();
        assertEquals("Authenticate", updateUser.getName());
    }

    @Test
    void testGetUserByIdr(){
        UserInfo userInfo = this.webTestClient
                .get()
                .uri("/user/{userId}", "user.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class)
                .returnResult()
                .getResponseBody();
        assertEquals("user.02", userInfo.getId());
    }

    @Test
    void testStatusMapping(){
        List strings = this.webTestClient
                .get()
                .uri("/user/status/mapping?sourceStatus=user.status.active")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(List.class)
                .returnResult()
                .getResponseBody();

        assertEquals("user.status.inactive", strings.get(0));
    }

    @Test
    void testChangeState(){
        UserInfo userInfo = this.webTestClient
                .get()
                .uri("/user/{userId}", "user.02")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class)
                .returnResult()
                .getResponseBody();
        assertEquals(UserServiceConstants.USER_STATUS_ACTIVE, userInfo.getState());

        Map<String,String> message = new HashMap<>();
        message.put("message","For Testing");

        UserInfo changeState = this.webTestClient
                .patch()
                .uri("/user/state/{userId}/{changeState}", "user.02","user.status.inactive")
                .body(BodyInserters.fromValue(message))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class)
                .returnResult()
                .getResponseBody();
        assertEquals(UserServiceConstants.USER_STATUS_INACTIVE, changeState.getState());
    }

    @Test
    void testValidateUser(){
        UserEntity userEntity = new UserEntity();
        userEntity.setId("user.03");
        userEntity.setName("Dummy User3");
        userEntity.setEmail("dummy@testmail.com");
        userEntity.setPassword("password");
        userEntity.setState(UserServiceConstants.USER_STATUS_ACTIVE);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(UserServiceConstants.ROLE_ID_ASSESSEE);
        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleEntity);

        List<ValidationResultInfo> errors = this.webTestClient
                .post()
                .uri(uriBuilder
                        -> uriBuilder
                        .path("/user/validate")
                        .queryParam("validationTypeKey", Constant.CREATE_VALIDATION)
                        .build())
                .body(BodyInserters.fromValue(userEntity))
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBodyList(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals(2, errors.size());
        assertEquals("The id supplied for the create already exists", errors.get(0).getMessage());
    }

    @Test
    @Transactional
    void testCreateUser(){
        UserInfo userInfo = new UserInfo();
        userInfo.setId("user.11");
        userInfo.setName("Dummy User11");
        userInfo.setEmail("dummyuser11@testmail.com");
        userInfo.setPassword("password");
        Set<String> roleIds = new HashSet<>();
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setId(UserServiceConstants.ROLE_ID_ASSESSEE);
        roleIds.add(UserServiceConstants.ROLE_ID_ASSESSEE);
        userInfo.setRoleIds(roleIds);

        UserInfo createUser = this.webTestClient
                .post()
                .uri("/user") // Assuming "/register" is the endpoint for registering a user
                .body(BodyInserters.fromValue(userInfo)) // Assuming userInfo is the object containing user information
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class) // Assuming UserInfo is the class representing user information
                .returnResult()
                .getResponseBody();

        assertEquals("user.11", createUser.getId());
    }

    @Test
    void testRestPassword() {
        ResetPasswordInfo resetPasswordInfo = new ResetPasswordInfo();
        resetPasswordInfo.setOldPassword("password");
        resetPasswordInfo.setNewPassword("newpassword");
        UserInfo userInfo = this.webTestClient
                .patch()
                .uri("/user/reset/password") // Assuming "/register" is the endpoint for registering a user
                .body(BodyInserters.fromValue(resetPasswordInfo)) // Assuming userInfo is the object containing user information
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class) // Assuming UserInfo is the class representing user information
                .returnResult()
                .getResponseBody();

        resetPasswordInfo.setOldPassword("newpassword");
        resetPasswordInfo.setNewPassword("password");
        userInfo = this.webTestClient
                .patch()
                .uri("/user/reset/password") // Assuming "/register" is the endpoint for registering a user
                .body(BodyInserters.fromValue(resetPasswordInfo)) // Assuming userInfo is the object containing user information
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class) // Assuming UserInfo is the class representing user information
                .returnResult()
                .getResponseBody();
    }

    @Test
    void testPrincipalUser(){
        UserInfo userInfo = this.webTestClient
                .get()
                .uri("/user/principal")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(UserInfo.class)
                .returnResult()
                .getResponseBody();
        assertEquals("SYSTEM_USER", userInfo.getId());
    }

    @Test
    void testLogOut(){
        this.webTestClient
                .post()
                .uri("/user/logout")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }


    @Test
    void testSearchUser(){
        this.webTestClient
                .get()
                .uri("/user") // Assuming the endpoint is mapped to "/"
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo("9");
    }


    @Test
    void testResendVerification(){
        ValidationResultInfo vris = this.webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/user/resend/verification")
                        .queryParam("userEmail", "dummyuser1@testmail.com")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();

        assertEquals("You will receive email on you registered email !", vris.getMessage());
    }


    @Test
    void testVerifyUser(){
       ValidationResultInfo vris = this.webTestClient
                .post()
                .uri("/user/verify/{base64UserEmail}/{base64TokenId}", "ZHVtbXl1c2VyNUB0ZXN0bWFpbC5jb20=","NzgzNDBmMDQtMTczMC00YzY2LThjNjQtODU1YzExZTk1NWRk")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + super.tokenMap.get("access_token")).exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(ValidationResultInfo.class)
                .returnResult()
                .getResponseBody();

       assertEquals(ErrorLevel.ERROR, vris.getLevel());
    }

}
