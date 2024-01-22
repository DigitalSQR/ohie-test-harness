/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.ComponentMapper;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This ComponentServiceRestController maps end points with standard service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/component")
@Api(value = "REST API for Component services", tags = {"Component API"})
public class ComponentRestController {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentMapper componentMapper;

    /**
     * We can expose this API in future if needed.
     * {@inheritdoc}
     *
     * @return
     */
//    @ApiOperation(value = "Create new Component", response = ComponentInfo.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully created Component"),
//            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//    })
//    @PostMapping("")
    @Transactional
    public ComponentInfo createComponent(
            @RequestBody ComponentInfo componentInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        ComponentEntity componentEntity = componentMapper.dtoToModel(componentInfo);
        componentEntity = componentService.createComponent(componentEntity, contextInfo);
        return componentMapper.modelToDto(componentEntity);

    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Update existing Component", response = ComponentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated Component"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PutMapping("")
    @Transactional
    public ComponentInfo updateComponent(
            @RequestBody ComponentInfo componentInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        ComponentEntity componentEntity = componentMapper.dtoToModel(componentInfo);
        componentEntity = componentService.updateComponent(componentEntity, contextInfo);
        return componentMapper.modelToDto(componentEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View a page of available filtered Components", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("")
    public Page<ComponentInfo> searchComponents(
            ComponentCriteriaSearchFilter componentCriteriaSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {

        Page<ComponentEntity> componentEntities = componentService.searchComponents(componentCriteriaSearchFilter, pageable, contextInfo);
        return componentMapper.pageEntityToDto(componentEntities);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available Component with supplied id", response = ComponentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Component"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{componentId}")
    public ComponentInfo getComponentById(
            @PathVariable("componentId") String componentId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        ComponentEntity componentById = componentService.getComponentById(componentId, contextInfo);
        return componentMapper.modelToDto(componentById);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    public Page<ComponentInfo> getComponents(
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Page<ComponentEntity> components = componentService.getComponents(pageable, contextInfo);
        return componentMapper.pageEntityToDto(components);
    }

    /**
     * {@inheritdoc}
     */
    @ApiOperation(value = "View a list of validation errors for Component", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Validation errors"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/validate")
    public List<ValidationResultInfo> validateComponent(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) ComponentInfo componentInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        ComponentEntity componentEntity = componentMapper.dtoToModel(componentInfo);
        return componentService
                .validateComponent(validationTypeKey, componentEntity, contextInfo);
    }

}
