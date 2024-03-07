package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.SpecificationMapper;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.google.common.collect.Multimap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * This SpecificationServiceRestController maps end points with standard service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/specification")
@Api(value = "REST API for Specification services", tags = {"Specification API"})
public class SpecificationRestController {

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private SpecificationMapper specificationMapper;

    /**
     * We can expose this API in future if needed.
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Create new Specification", response = SpecificationInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created Specification"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
        @PostMapping("")
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public SpecificationInfo createSpecification(
            @RequestBody SpecificationInfo specificationInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        SpecificationEntity specificationEntity = specificationMapper.dtoToModel(specificationInfo);
        specificationEntity = specificationService.createSpecification(specificationEntity, contextInfo);
        return specificationMapper.modelToDto(specificationEntity);

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
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public SpecificationInfo updateSpecification(
            @RequestBody SpecificationInfo specificationInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        SpecificationEntity specificationEntity = specificationMapper.dtoToModel(specificationInfo);
        specificationEntity = specificationService.updateSpecification(specificationEntity, contextInfo);
        return specificationMapper.modelToDto(specificationEntity);
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
    public Page<SpecificationInfo> searchSpecifications(
            SpecificationCriteriaSearchFilter specificationSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        Page<SpecificationEntity> specificationEntities = specificationService.searchSpecifications(specificationSearchFilter, pageable, contextInfo);
        return specificationMapper.pageEntityToDto(specificationEntities);
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
    public SpecificationInfo getSpecificationById(
            @PathVariable("specificationId") String specificationId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        SpecificationEntity specificationById = specificationService.getSpecificationById(specificationId, contextInfo);
        return specificationMapper.modelToDto(specificationById);
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
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public List<ValidationResultInfo> validateSpecification(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) SpecificationInfo specificationInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        SpecificationEntity specificationEntity = specificationMapper.dtoToModel(specificationInfo);
        return specificationService
                .validateSpecification(validationTypeKey, specificationEntity, contextInfo);
    }

    @ApiOperation(value = "To change status of Specification", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated Specification"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping("/state/{specificationId}/{changeState}")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public SpecificationInfo updateSpecificationState(@PathVariable("specificationId") String specificationId,
                                                      @PathVariable("changeState") String changeState,
                                                      @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        SpecificationEntity specificationEntity = specificationService.changeState(specificationId, changeState, contextInfo);
        return specificationMapper.modelToDto(specificationEntity);
    }

    @ApiOperation(value = "Retrieves all status of specification.", response = Multimap.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/status/mapping")
    public List<String> getStatusMapping(@RequestParam("sourceStatus") String sourceStatus) throws IOException {
        Collection<String> strings = SpecificationServiceConstants.SPECIFICATION_STATUS_MAP.get(sourceStatus);
        return strings.parallelStream().toList();
    }

}
