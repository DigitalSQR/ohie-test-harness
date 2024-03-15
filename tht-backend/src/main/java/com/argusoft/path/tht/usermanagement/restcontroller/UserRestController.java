package com.argusoft.path.tht.usermanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.email.service.EmailService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.filter.UserSearchCriteriaFilter;
import com.argusoft.path.tht.usermanagement.models.dto.ResetPasswordInfo;
import com.argusoft.path.tht.usermanagement.models.dto.UpdatePasswordInfo;
import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.mapper.UserMapper;
import com.argusoft.path.tht.usermanagement.service.TokenVerificationService;
import com.argusoft.path.tht.usermanagement.service.UserService;
import com.google.common.collect.Multimap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * This userServiceRestController maps end points with standard service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/user")
@Api(value = "REST API for User services", tags = {"User API"})
public class UserRestController {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

    private UserService userService;
    private TokenVerificationService tokenVerificationService;
    private UserMapper userMapper;
    private EmailService emailService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTokenVerificationService(TokenVerificationService tokenVerificationService) {
        this.tokenVerificationService = tokenVerificationService;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * {@inheritdoc}
     */
    @ApiOperation(value = "login  user", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully logout user")
    })
    @PostMapping("/logout")
    @Transactional(rollbackFor = Exception.class)
    public Boolean logout(@RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException {
        return userService.logout(contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Register new user", response = UserInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully user registered"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class)
    public UserInfo registerAssessee(
            @RequestBody UserInfo userInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, MessagingException, IOException {

        UserEntity userEntity = userMapper.dtoToModel(userInfo);
        userEntity = userService.registerAssessee(userEntity, contextInfo);
        return userMapper.modelToDto(userEntity);
    }

    @PostMapping("/verify/{base64UserEmail}/{base64TokenId}")
    @Transactional(rollbackFor = Exception.class)
    public ValidationResultInfo verifyUser(@PathVariable("base64UserEmail") String base64UserEmail,
                                           @PathVariable("base64TokenId") String base64TokenId,
                                           @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws DataValidationErrorException,
            OperationFailedException,
            VersionMismatchException,
            InvalidParameterException {

        ValidationResultInfo vris = new ValidationResultInfo();
        Boolean isVerified = false;
        try {
            isVerified = tokenVerificationService.verifyUserToken(base64TokenId, base64UserEmail, false, contextInfo);
        } catch (DoesNotExistException e) {
            LOGGER.error(ValidateConstant.DOES_NOT_EXIST_EXCEPTION + UserRestController.class.getSimpleName(), e);
            // return false as it is
        }
        vris.setLevel(Boolean.TRUE.equals(isVerified) ? ErrorLevel.OK : ErrorLevel.ERROR);
        return vris;
    }

    @PostMapping("/update/password/")
    @Transactional(rollbackFor = Exception.class)
    public ValidationResultInfo updatePassword(@RequestBody UpdatePasswordInfo updatePasswordInfo,
                                               @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException, DoesNotExistException,
            DataValidationErrorException, OperationFailedException,
            VersionMismatchException {
        ValidationResultInfo validationResultInfo = new ValidationResultInfo();
        userService.updatePasswordWithVerificationToken(updatePasswordInfo, contextInfo);
        validationResultInfo.setMessage("Password Updated Successfully!");
        validationResultInfo.setLevel(ErrorLevel.OK);
        return validationResultInfo;
    }

    @GetMapping("/forgot/password")
    public ValidationResultInfo forgotPasswordRequest(@RequestParam("userEmail") String userEmail,
                                                      @RequestAttribute(name = "contextInfo") ContextInfo contextInfo) {
        userService.createForgotPasswordRequestAndSendEmail(userEmail, contextInfo);
        ValidationResultInfo vris = new ValidationResultInfo();
        vris.setMessage("You will receive email if already registered !");
        return vris;
    }

    @PostMapping("/resend/verification")
    public ValidationResultInfo resendVerification(@RequestParam("userEmail") String userEmail,
                                                   @RequestAttribute(name = "contextInfo") ContextInfo contextInfo) {
        userService.resendVerification(userEmail, contextInfo);
        ValidationResultInfo vris = new ValidationResultInfo();
        vris.setMessage("You will receive email on you registered email !");
        return vris;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Create new user", response = UserInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created user"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAuthority('role.admin')")
    public UserInfo createUser(
            @RequestBody UserInfo userInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, MessagingException, IOException {

        UserEntity userEntity = userMapper.dtoToModel(userInfo);
        userEntity = userService.createUser(userEntity, contextInfo);
        return userMapper.modelToDto(userEntity);

    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Update existing user", response = UserInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated user"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PutMapping("")
    @Transactional(rollbackFor = Exception.class)
    public UserInfo updateUser(
            @RequestBody UserInfo userInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws
            OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        UserEntity userEntity = userMapper.dtoToModel(userInfo);
        userEntity = userService.updateUser(userEntity, contextInfo);
        return userMapper.modelToDto(userEntity);
    }

    @ApiOperation(value = "To change state of User", response = UserInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping("/state/{userId}/{changeState}")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public UserInfo updateUserState(@PathVariable("userId") String userId,
                                    @PathVariable("changeState") String changeState,
                                    @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException, MessagingException, IOException {
        UserEntity userEntity = userService.changeState(userId, changeState, contextInfo);
        return userMapper.modelToDto(userEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View a page of available filtered users", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were attempting to reach could not be found.")
    })
    @GetMapping("")
    public Page<UserInfo> searchUsers(
            UserSearchCriteriaFilter userSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        Page<UserEntity> userEntities = this.userService.searchUsers(userSearchFilter, pageable, contextInfo);
        return userMapper.pageEntityToDto(userEntities);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available user with supplied id", response = UserInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were attempting to reach could not be found.")
    })
    @GetMapping("/{userId}")
    public UserInfo getUserById(
            @PathVariable("userId") String userId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        UserEntity userById = userService.getUserById(userId, contextInfo);
        return userMapper.modelToDto(userById);
    }

    /**
     * {@inheritdoc}
     */
    @ApiOperation(value = "View a list of validation errors for user", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Validation errors"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/validate")
    public List<ValidationResultInfo> validateUser(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) UserInfo userInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        UserEntity userEntity = userMapper.dtoToModel(userInfo);
        return userService
                .validateUser(validationTypeKey, userEntity, contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View loggedIn user's data", response = UserInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved loggedIn user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/principal")
    public UserInfo getPrincipalUser(@RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException, DoesNotExistException {
        UserEntity principalUser = null;
        try {
            principalUser = userService
                    .getPrincipalUser(contextInfo);
        } catch (InvalidParameterException e) {
            LOGGER.error(ValidateConstant.INVALID_PARAM_EXCEPTION + UserRestController.class.getSimpleName(), e);
            throw new OperationFailedException("InvalidParameterException while fetching principal User ", e);
        }
        return userMapper.modelToDto(principalUser);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Reset password for user", response = UserInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully reset the password"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping("/reset/password")
    public UserInfo resetPassword(@RequestBody ResetPasswordInfo resetPasswordInfo,
                                  @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        return userMapper.modelToDto(userService.resetPassword(resetPasswordInfo, contextInfo));
    }

    @ApiOperation(value = "Retrieves all status for user.", response = Multimap.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were attempting to reach could not be found.")
    })
    @GetMapping("/status/mapping")
    public List<String> getStatusMapping(@RequestParam("sourceStatus") String sourceStatus) {
        Collection<String> strings = UserServiceConstants.USER_STATUS_MAP.get(sourceStatus);
        return strings.parallelStream().toList();
    }

}
