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
import com.argusoft.path.tht.usermanagement.filter.UserSearchFilter;
import com.argusoft.path.tht.usermanagement.models.dto.UserInfo;
import com.argusoft.path.tht.usermanagement.models.entity.UserEntity;
import com.argusoft.path.tht.usermanagement.models.mapper.UserMapper;
import com.argusoft.path.tht.usermanagement.service.UserService;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "REST API for User services", description = "User API")
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
    @Operation(summary = "Logout", description = "Logout user and expire token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logout user")
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
     *
     * @return
     */
    @Operation(summary = "Register Assessee", description = "Register a new Assessee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully user registered"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to create the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/register")
    @Timed(name = "registerAssessee")
    public UserInfo registerAssessee(
            @RequestBody UserInfo userInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {

        UserEntity userEntity = userMapper.dtoToModel(userInfo);
        userEntity = userService.registerAssessee(userEntity, contextInfo);
        return userMapper.modelToDto(userEntity);
    }


    /*@GetMapping("/verify/{base64TokenId}")
    public boolean verifyUser(@PathVariable("base64TokenId") String base64TokenId, @RequestAttribute(name = "contextInfo") ContextInfo contextInfo){


    }*/


    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Operation(summary = "Create User", description = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to create the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden")
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
     *
     * @return
     */
    @Operation(summary = "Update User", description = "Update existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to create the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden")

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
        userEntity = userService.updateUser(userEntity, contextInfo);
        return userMapper.modelToDto(userEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @Operation(summary = "Search Users", description = "View a page of available filtered users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved page"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
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
     *
     * @return
     */
    @Operation(summary = "Fetch User by ID", description = "View available user with supplied id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
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
     *
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
    @Operation(summary = "Validate user", description = "View a list of validation errors for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Validation errors"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden")
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
     *
     * @return
     */
    @Operation(summary = "Fetch Principal User", description = "View loggedIn user's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved loggedIn user"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden")
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
