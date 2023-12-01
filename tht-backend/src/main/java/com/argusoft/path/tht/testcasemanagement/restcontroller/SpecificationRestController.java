/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.SpecificationMapper;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.codahale.metrics.annotation.Timed;
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
 * This SpecificationServiceRestController maps end points with standard service.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@RestController
@RequestMapping("/specification")
@Api(value = "REST API for Specification services", tags = {"Specification API"})
@Metrics(registry = "SpecificationRestController")
public class SpecificationRestController {

    @Autowired
    private SpecificationService SpecificationService;

    @Autowired
    private SpecificationMapper SpecificationMapper;

    /**
     * We can expose this API in future if needed.
     * {@inheritdoc}
     *
     * @return
     */
//    @ApiOperation(value = "Create new Specification", response = SpecificationInfo.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully created Specification"),
//            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//    })
//    @PostMapping("")
    @Timed(name = "createSpecification")
    public SpecificationInfo createSpecification(
            @RequestBody SpecificationInfo specificationInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException {

        SpecificationEntity specificationEntity = SpecificationMapper.dtoToModel(specificationInfo);
        specificationEntity = SpecificationService.createSpecification(specificationEntity, contextInfo);
        return SpecificationMapper.modelToDto(specificationEntity);

    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Update existing Specification", response = SpecificationInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated Specification"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PutMapping("")
    @Timed(name = "updateSpecification")
    public SpecificationInfo updateSpecification(
            @RequestBody SpecificationInfo specificationInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        SpecificationEntity specificationEntity = SpecificationMapper.dtoToModel(specificationInfo);
        specificationEntity = SpecificationService.updateSpecification(specificationEntity, contextInfo);
        return SpecificationMapper.modelToDto(specificationEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View a page of available filtered Specifications", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("")
    @Timed(name = "searchSpecifications")
    public Page<SpecificationInfo> searchSpecifications(
            @RequestParam(name = "id", required = false) List<String> ids,
            SpecificationSearchFilter specificationSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {

        Page<SpecificationEntity> specificationEntities;
        if (!specificationSearchFilter.isEmpty()
                || !CollectionUtils.isEmpty(ids)) {
            specificationEntities = SpecificationService
                    .searchSpecifications(
                            ids,
                            specificationSearchFilter,
                            pageable,
                            contextInfo);
            return SpecificationMapper.pageEntityToDto(specificationEntities);
        }
        return this.getSpecifications(pageable, contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available Specification with supplied id", response = SpecificationInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Specification"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{specificationId}")
    @Timed(name = "getSpecificationById")
    public SpecificationInfo getSpecificationById(
            @PathVariable("SpecificationId") String specificationId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {

        SpecificationEntity specificationById = SpecificationService.getSpecificationById(specificationId, contextInfo);
        return SpecificationMapper.modelToDto(specificationById);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    public Page<SpecificationInfo> getSpecifications(
            Pageable pageable,
            ContextInfo contextInfo)
            throws OperationFailedException,
            MissingParameterException,
            PermissionDeniedException,
            InvalidParameterException {
        Page<SpecificationEntity> specifications = SpecificationService.getSpecifications(pageable, contextInfo);
        return SpecificationMapper.pageEntityToDto(specifications);
    }

    /**
     * {@inheritdoc}
     */
    @ApiOperation(value = "View a list of validation errors for Specification", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Validation errors"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/validate")
    @Timed(name = "validateSpecification")
    public List<ValidationResultInfo> validateSpecification(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) SpecificationInfo specificationInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException, DoesNotExistException {
        SpecificationEntity specificationEntity = SpecificationMapper.dtoToModel(specificationInfo);
        return SpecificationService
                .validateSpecification(validationTypeKey, specificationEntity, contextInfo);
    }

}
