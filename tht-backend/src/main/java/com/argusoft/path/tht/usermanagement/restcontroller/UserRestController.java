/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.usermanagement.restcontroller;

import com.argusoft.path.tht.emailservice.service.EmailService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.mapper.UserMapper;
import com.codahale.metrics.annotation.Timed;
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.service.UserService;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This userServiceRestController maps end points with standard service.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@RestController
@RequestMapping("/user")
@Api(value = "REST API for User services", tags = {"User API"})
@Metrics(registry = "UserRestController")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;


    /**
     * {@inheritdoc}
     */
    @ApiOperation(value = "login  user", response = Boolean.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully logout user")
    })
    @PostMapping("/logout")
    @Timed(name = "logout")
    public Boolean logout(@RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            MissingParameterException,
            PermissionDeniedException,
            DataValidationErrorException {
        return userService.logout(contextInfo);
    }


    /**
     * {@inheritdoc}
     * @return
     */
    @ApiOperation(value = "Register new user", response = UserInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully user registered"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/register")
    @Timed(name = "registerUser")
    public UserInfo registerUser(
            @RequestBody UserInfo userInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {

        UserEntity userEntity = userMapper.dtoToModel(userInfo);
        userEntity = userService.registerUser(userEntity, contextInfo);
        return userMapper.modelToDto(userEntity);
    }


    /*@GetMapping("/verify/{base64TokenId}")
    public boolean verifyUser(@PathVariable("base64TokenId") String base64TokenId, @RequestAttribute(name = "contextInfo") ContextInfo contextInfo){


    }*/



    /**
     * {@inheritdoc}
     * @return
     */
    @ApiOperation(value = "Create new user", response = UserInfo.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully created user"),
        @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("")
    @Timed(name = "createUser")
    public UserInfo createUser(
            @RequestBody UserInfo userInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {

        UserEntity userEntity = userMapper.dtoToModel(userInfo);
        userEntity = userService.createUser(userEntity, contextInfo);
        return userMapper.modelToDto(userEntity);

    }

    /**
     * {@inheritdoc}
     * @return
     */
    @ApiOperation(value = "Update existing user", response = UserInfo.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated user"),
        @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PutMapping("")
    @Timed(name = "updateUser")
    public UserInfo updateUser(
            @RequestBody UserInfo userInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        UserEntity userEntity = userMapper.dtoToModel(userInfo);
        userEntity =  userService.updateUser(userEntity, contextInfo);
        return userMapper.modelToDto(userEntity);
    }

    /**
     * {@inheritdoc}
     * @return
     */
    @ApiOperation(value = "View a page of available filtered users", response = Page.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved page"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("")
    @Timed(name = "searchUsers")
    public Page<UserInfo> searchUsers(
            @RequestParam(name = "id", required = false) List<String> ids,
            UserSearchFilter userSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {

        Page<UserEntity> userEntities;
        if (!userSearchFilter.isEmpty()
                || !CollectionUtils.isEmpty(ids)) {
            userEntities = userService
                    .searchUsers(
                            ids,
                            userSearchFilter,
                            pageable,
                            contextInfo);
            return userMapper.pageEntityToDto(userEntities);
        }
        return this.getUsers(pageable, contextInfo);
    }

    /**
     * {@inheritdoc}
     * @return
     */
    @ApiOperation(value = "View available user with supplied id", response = UserInfo.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved user"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{userId}")
    @Timed(name = "getUserById")
    public UserInfo getUserById(
            @PathVariable("userId") String userId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {

        UserEntity userById = userService.getUserById(userId, contextInfo);
        return userMapper.modelToDto(userById);
    }

    /**
     * {@inheritdoc}
     * @return
     */
    public Page<UserInfo> getUsers(
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        Page<UserEntity> users = userService.getUsers(pageable, contextInfo);
        return userMapper.pageEntityToDto(users);
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
    @Timed(name = "validateUser")
    public List<ValidationResultInfo> validateUser(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) UserInfo userInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException, DoesNotExistException {
        return userService
                .validateUser(validationTypeKey, new UserEntity(), contextInfo);
    }
    
    /**
     * {@inheritdoc}
     * @return
     */
    @ApiOperation(value = "View loggedIn user's data", response = UserInfo.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved loggedIn user"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @GetMapping("/principal")
    @Timed(name = "getPrincipalUser")
    public UserInfo getPrincipalUser(@RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException, DoesNotExistException {
        UserEntity principalUser = userService
                .getPrincipalUser(contextInfo);
        return userMapper.modelToDto(principalUser);
    }

}
