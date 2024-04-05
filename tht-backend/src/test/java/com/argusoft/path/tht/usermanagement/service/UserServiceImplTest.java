package com.argusoft.path.tht.usermanagement.service;

import com.argusoft.path.tht.TestingHarnessToolTestConfiguration;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.Module;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.filter.RoleSearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.mock.UserServiceMockImpl;
import com.argusoft.path.tht.usermanagement.models.entity.RoleEntity;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceImplTest extends TestingHarnessToolTestConfiguration {

    ContextInfo contextInfo;

    @Autowired
    UserServiceMockImpl userServiceMock;

    @Autowired
    UserService userService;

    @BeforeEach
    @Override
    public void init() {
        super.init();
        userServiceMock.init();
        contextInfo = new ContextInfo();
    }

    @AfterEach
    void after() {
        userServiceMock.clear();
    }


    @Test
    void testRegisterAssessee() throws InvalidParameterException, DoesNotExistException, MessagingException, DataValidationErrorException, OperationFailedException, IOException {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("dummymail1@testmail.com");
        userEntity.setPassword("password");
        userEntity.setName("Dummy");

        UserEntity registerAssessee = this.userService.registerAssessee(userEntity, contextInfo);
        assertEquals("dummymail1@testmail.com", registerAssessee.getEmail());

        //Test 2: with null entity
        Assert.assertThrows(InvalidParameterException.class, () -> {
            this.userService.registerAssessee(null, contextInfo);
        });

    }


    @Test
    void testCreateUser() throws InvalidParameterException, DoesNotExistException, MessagingException, DataValidationErrorException, OperationFailedException, IOException {
        //testcase 1: create new user
        UserEntity userEntity = new UserEntity();
        userEntity.setId("user.001");
        userEntity.setName("dummy user");
        userEntity.setEmail("dummyuser8@testmail.com");
        userEntity.setPassword("password");
        userEntity.setState(UserServiceConstants.USER_STATUS_ACTIVE);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(UserServiceConstants.ROLE_ID_ASSESSEE);
        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleEntity);

        UserEntity createUser = this.userService.createUser(userEntity, contextInfo);
        assertEquals("dummyuser8@testmail.com", createUser.getEmail());

        //testcase 2 : create with same email
        Assert.assertThrows(DataValidationErrorException.class, () -> {
            this.userService.createUser(userEntity, contextInfo);
        });

        //testcase 3: when contextinfo has OAUTH2 Module
        UserEntity userEntity3 = new UserEntity();
        userEntity3.setName("dummy user");
        userEntity3.setEmail("dummyuser9@testmail.com");
        userEntity3.setPassword("password");
        userEntity3.setState(UserServiceConstants.USER_STATUS_ACTIVE);
        roleEntity.setId(UserServiceConstants.ROLE_ID_TESTER);
        userEntity3.getRoles().clear();
        userEntity3.getRoles().add(roleEntity);

        contextInfo.setModule(Module.OAUTH2);
        UserEntity createUser3 = this.userService.createUser(userEntity3, contextInfo);
        assertEquals(userEntity3.getEmail(), createUser3.getEmail());

        //testcase 4:to validate foreign key
        UserEntity userEntity4 = new UserEntity();
        userEntity4.setId("user.002");
        userEntity4.setName("dummy user");
        userEntity4.setEmail("dummyuser002@testmail.com");
        userEntity4.setPassword("password");
        userEntity4.setState(UserServiceConstants.USER_STATUS_ACTIVE);
        roleEntity.setId("DOES_NOT_EXISTS");
        userEntity4.getRoles().clear();
        userEntity4.getRoles().add(roleEntity);

        assertThrows(DataValidationErrorException.class, () -> {
            this.userService.createUser(userEntity4, contextInfo);
        });

        //Test 5: with null entity
        Assert.assertThrows(InvalidParameterException.class, () -> {
            this.userService.createUser(null, contextInfo);
        });
    }




    @Test
    void testGetRoleById(){
        assertThrows(InvalidParameterException.class, () -> {
            userService.getRoleById(null, contextInfo);
        });
    }


    @Test
    void testGetUserByEmail() throws InvalidParameterException, DoesNotExistException {
        //when email is null
        assertThrows(DoesNotExistException.class, () -> {
            userService.getUserByEmail(null, contextInfo);
        });

        //when email is present
        UserEntity findByEmail = this.userService.getUserByEmail("dummyuser1@testmail.com", contextInfo);
        assertEquals("dummyuser1@testmail.com", findByEmail.getEmail());
    }


    @Test
    void testGetUserById() throws InvalidParameterException, DoesNotExistException {
        //when id is null
        assertThrows(InvalidParameterException.class, () -> {
            userService.getUserById(null, contextInfo);
        });

        //when id is present
        UserEntity findById = this.userService.getUserById("user.01", contextInfo);
        assertEquals("user.01", findById.getId());
    }

    @Test
    void testGetUsersByRole() throws InvalidParameterException, DoesNotExistException {
        //check for super user
        List<UserEntity> usersList = this.userService.getUsersByRole("role.admin", contextInfo);
        assertEquals(1, usersList.size());

        //check when no user is present for role tester
        assertThrows(DoesNotExistException.class, () -> {
            userService.getUsersByRole("role.invalid", contextInfo);
        });
    }

//    @Test
//    void testResetPassword(){
//        //when old password is empty
//        ResetPasswordInfo resetPasswordInfo = new ResetPasswordInfo();
//        resetPasswordInfo.setOldPassword("");
//        resetPasswordInfo.setNewPassword("newpassword");
//
//        ContextInfo contextInfo1 = new ContextInfo("dummyuser4@testmail.com", "dummyuser4@testmail.com", "password",
//                true, true, true, true, null);
//
//        Assert.assertThrows(DataValidationErrorException.class, () -> {
//            userService.resetPassword(resetPasswordInfo, contextInfo1);
//        });
//    }

//    @Test
//    void testForgotPassword(){
//        this.userService.createForgotPasswordRequestAndSendEmail("dummyuser4@testmail.com", contextInfo);
//    }
//
//
//    @Test
//    void testGetPrincipalUser() throws InvalidParameterException, DoesNotExistException, OperationFailedException {
//        UserEntity getPrincipalUser = this.userService.getPrincipalUser(contextInfo);
//    }


    @Test
    void testLogOut() throws OperationFailedException {
        Boolean isLogOut = this.userService.logout(contextInfo);
        assertEquals(Boolean.FALSE, isLogOut);
    }


    @Test
    @Transactional
    void testChangeState() throws InvalidParameterException, DoesNotExistException, MessagingException, DataValidationErrorException, OperationFailedException, IOException, VersionMismatchException {
        //change the state of user
        UserEntity changeState = this.userService.changeState("user.03", "For Testing", UserServiceConstants.USER_STATUS_INACTIVE, contextInfo);
        assertEquals(UserServiceConstants.USER_STATUS_INACTIVE, changeState.getState());

        //change state of admin if only one is present
        Assert.assertThrows(DataValidationErrorException.class, () -> {
            this.userService.changeState("SYSTEM_USER", "For Testing", UserServiceConstants.USER_STATUS_INACTIVE, contextInfo);
        });

    }

    @Test
    void testCreateForgotPasswordAndSendEmail(){
        this.userService.createForgotPasswordRequestAndSendEmail("dummyuser2@testmail.com", contextInfo);
    }

    @Test
    void testResendVerification(){
        this.userService.resendVerification("dummyuser2@testmail.com", contextInfo);
    }


    @Test
    void testSendMailToUserOnChangeState() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        UserEntity userEntity = this.userService.getUserById("user.01", contextInfo);
        //from approval pending to active
        this.userService.sendMailToTheUserOnChangeState(UserServiceConstants.USER_STATUS_APPROVAL_PENDING, null, UserServiceConstants.USER_STATUS_ACTIVE, userEntity,contextInfo);

        //from approval pending  to inactive
        this.userService.sendMailToTheUserOnChangeState(UserServiceConstants.USER_STATUS_APPROVAL_PENDING, "For Testing", UserServiceConstants.USER_STATUS_INACTIVE, userEntity,contextInfo);

        //from verification pending to approval pending
        this.userService.sendMailToTheUserOnChangeState(UserServiceConstants.USER_STATUS_VERIFICATION_PENDING, null, UserServiceConstants.USER_STATUS_APPROVAL_PENDING, userEntity,contextInfo);

        //user status active to inactive
        this.userService.sendMailToTheUserOnChangeState(UserServiceConstants.USER_STATUS_ACTIVE, "For Testing", UserServiceConstants.USER_STATUS_INACTIVE, userEntity,contextInfo);

        //verification pending to active
        this.userService.sendMailToTheUserOnChangeState(UserServiceConstants.USER_STATUS_VERIFICATION_PENDING, null, UserServiceConstants.USER_STATUS_ACTIVE, userEntity,contextInfo);

        //inactive to active
        this.userService.sendMailToTheUserOnChangeState(UserServiceConstants.USER_STATUS_INACTIVE, null, UserServiceConstants.USER_STATUS_ACTIVE, userEntity,contextInfo);
    }


    @Test
    void testValidateUser() throws InvalidParameterException, OperationFailedException, DoesNotExistException {
        //validate if user already present
        UserEntity userEntity = this.userService.getUserById("user.01", contextInfo);
        List<ValidationResultInfo> vris = this.userService.validateUser(Constant.CREATE_VALIDATION, userEntity, contextInfo);
        assertEquals("An account with this email already exists. Please use a different email address.", vris.get(0).getMessage());

        //validate when different validation type key
        UserEntity userEntity2 = this.userService.getUserById("user.01", contextInfo);
        String validationTypeKey = "update.validation.test";
        Assertions.assertThrows(InvalidParameterException.class, () -> {
            this.userService.validateUser(validationTypeKey, userEntity2, contextInfo);
        });
    }


    @Test
    void testSearchRoles() throws InvalidParameterException {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoleEntity> result = this.userService.getRoles(pageable, contextInfo);
        assertEquals(3, result.stream().count());
    }


    @Test
    @Transactional
    void testUpdateUser() throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        //update simple user for name
        UserEntity userEntity = this.userService.getUserById("user.05", contextInfo);
        assertEquals("user.05", userEntity.getId());

        userEntity.setName("update name");

        UserEntity updateUser = this.userService.updateUser(userEntity, contextInfo);
        assertEquals(userEntity.getName(), updateUser.getName());

        //update the role of last admin present to tester
        UserEntity userEntity2 = new UserEntity(this.userService.getUserById("SYSTEM_USER", contextInfo));
        assertEquals("SYSTEM_USER", userEntity2.getId());

        userEntity2.getRoles().clear();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(UserServiceConstants.ROLE_ID_TESTER);
        roleEntity.setName("Tester");
        userEntity2.getRoles().add(roleEntity);
        //new ContextInfo()

        Assert.assertThrows(DataValidationErrorException.class, () -> {
            this.userService.updateUser(userEntity2, contextInfo);
        });

        //update user if id is not present in db
        UserEntity userEntity3 = new UserEntity();
        userEntity3.setId("not.present");
        Assert.assertThrows(DataValidationErrorException.class, () -> {
            this.userService.updateUser(userEntity3, contextInfo);
        });

        //Test : with null entity
        Assert.assertThrows(InvalidParameterException.class, () -> {
            this.userService.registerAssessee(null, contextInfo);
        });

    }


    @Test
    void testSearchRolesWithPage() throws InvalidParameterException, OperationFailedException {
        RoleSearchCriteriaFilter roleSearchCriteriaFilter = new RoleSearchCriteriaFilter();
        roleSearchCriteriaFilter.setName("Admin");
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoleEntity> result = this.userService.searchRoles(roleSearchCriteriaFilter,pageable, contextInfo);

        assertEquals(1, result.stream().count());
    }
}
