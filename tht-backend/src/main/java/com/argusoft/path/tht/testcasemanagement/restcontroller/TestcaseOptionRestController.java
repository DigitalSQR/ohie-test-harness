/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseOptionInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseOptionMapper;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This TestcaseOptionServiceRestController maps end points with standard service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/testcase-option")
@Api(value = "REST API for TestcaseOption services", tags = {"TestcaseOption API"})
public class TestcaseOptionRestController {

    @Autowired
    private TestcaseOptionService TestcaseOptionService;

    @Autowired
    private TestcaseOptionMapper TestcaseOptionMapper;

    /**
     * We can expose this API in future if needed.
     * {@inheritdoc}
     *
     * @return
     */
//    @ApiOperation(value = "Create new TestcaseOption", response = TestcaseOptionInfo.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully created TestcaseOption"),
//            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//    })
//    @PostMapping("")
    @Transactional
    public TestcaseOptionInfo createTestcaseOption(
            @RequestBody TestcaseOptionInfo testcaseOptionInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseOptionEntity testcaseOptionEntity = TestcaseOptionMapper.dtoToModel(testcaseOptionInfo);
        testcaseOptionEntity = TestcaseOptionService.createTestcaseOption(testcaseOptionEntity, contextInfo);
        return TestcaseOptionMapper.modelToDto(testcaseOptionEntity);

    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Update existing TestcaseOption", response = TestcaseOptionInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated TestcaseOption"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PutMapping("")
    @Transactional
    public TestcaseOptionInfo updateTestcaseOption(
            @RequestBody TestcaseOptionInfo testcaseOptionInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        TestcaseOptionEntity testcaseOptionEntity = TestcaseOptionMapper.dtoToModel(testcaseOptionInfo);
        testcaseOptionEntity = TestcaseOptionService.updateTestcaseOption(testcaseOptionEntity, contextInfo);
        return TestcaseOptionMapper.modelToDto(testcaseOptionEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View a page of available filtered TestcaseOptions", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("")
    public Page<TestcaseOptionInfo> searchTestcaseOptions(
            @RequestParam(name = "id", required = false) List<String> ids,
            TestcaseOptionSearchFilter testcaseOptionSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {

        Page<TestcaseOptionEntity> testcaseOptionEntities;
        if (!testcaseOptionSearchFilter.isEmpty()
                || !CollectionUtils.isEmpty(ids)) {
            testcaseOptionEntities = TestcaseOptionService
                    .searchTestcaseOptions(
                            ids,
                            testcaseOptionSearchFilter,
                            pageable,
                            contextInfo);
            return TestcaseOptionMapper.pageEntityToDto(testcaseOptionEntities);
        }
        return this.getTestcaseOptions(pageable, contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available TestcaseOption with supplied id", response = TestcaseOptionInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved TestcaseOption"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{testcaseOptionId}")
    public TestcaseOptionInfo getTestcaseOptionById(
            @PathVariable("TestcaseOptionId") String testcaseOptionId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        TestcaseOptionEntity testcaseOptionById = TestcaseOptionService.getTestcaseOptionById(testcaseOptionId, contextInfo);
        return TestcaseOptionMapper.modelToDto(testcaseOptionById);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    public Page<TestcaseOptionInfo> getTestcaseOptions(
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Page<TestcaseOptionEntity> testcaseOptions = TestcaseOptionService.getTestcaseOptions(pageable, contextInfo);
        return TestcaseOptionMapper.pageEntityToDto(testcaseOptions);
    }

    /**
     * {@inheritdoc}
     */
    @ApiOperation(value = "View a list of validation errors for TestcaseOption", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Validation errors"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/validate")
    public List<ValidationResultInfo> validateTestcaseOption(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) TestcaseOptionInfo testcaseOptionInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        TestcaseOptionEntity testcaseOptionEntity = TestcaseOptionMapper.dtoToModel(testcaseOptionInfo);
        return TestcaseOptionService
                .validateTestcaseOption(validationTypeKey, testcaseOptionEntity, contextInfo);
    }

}
