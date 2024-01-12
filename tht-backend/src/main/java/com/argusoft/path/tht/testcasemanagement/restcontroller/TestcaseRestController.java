/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseMapper;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
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
 * This TestcaseServiceRestController maps end points with standard service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/testcase")
@Api(value = "REST API for Testcase services", tags = {"Testcase API"})
public class TestcaseRestController {

    @Autowired
    private TestcaseService TestcaseService;

    @Autowired
    private TestcaseMapper TestcaseMapper;

    /**
     * We can expose this API in future if needed.
     * {@inheritdoc}
     *
     * @return
     */
//    @ApiOperation(value = "Create new Testcase", response = TestcaseInfo.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully created Testcase"),
//            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//    })
//    @PostMapping("")
    @Transactional
    public TestcaseInfo createTestcase(
            @RequestBody TestcaseInfo testcaseInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseEntity testcaseEntity = TestcaseMapper.dtoToModel(testcaseInfo);
        testcaseEntity = TestcaseService.createTestcase(testcaseEntity, contextInfo);
        return TestcaseMapper.modelToDto(testcaseEntity);

    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Update existing Testcase", response = TestcaseInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated Testcase"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PutMapping("")
    @Transactional
    public TestcaseInfo updateTestcase(
            @RequestBody TestcaseInfo testcaseInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws
            OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        TestcaseEntity testcaseEntity = TestcaseMapper.dtoToModel(testcaseInfo);
        testcaseEntity = TestcaseService.updateTestcase(testcaseEntity, contextInfo);
        return TestcaseMapper.modelToDto(testcaseEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View a page of available filtered Testcases", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("")
    public Page<TestcaseInfo> searchTestcases(
            @RequestParam(name = "id", required = false) List<String> ids,
            TestcaseSearchFilter testcaseSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {

        Page<TestcaseEntity> testcaseEntities;
        if (!testcaseSearchFilter.isEmpty()
                || !CollectionUtils.isEmpty(ids)) {
            testcaseEntities = TestcaseService
                    .searchTestcases(
                            ids,
                            testcaseSearchFilter,
                            pageable,
                            contextInfo);
            return TestcaseMapper.pageEntityToDto(testcaseEntities);
        }
        return this.getTestcases(pageable, contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available Testcase with supplied id", response = TestcaseInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Testcase"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{testcaseId}")
    public TestcaseInfo getTestcaseById(
            @PathVariable("testcaseId") String testcaseId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        TestcaseEntity testcaseById = TestcaseService.getTestcaseById(testcaseId, contextInfo);
        return TestcaseMapper.modelToDto(testcaseById);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    public Page<TestcaseInfo> getTestcases(
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Page<TestcaseEntity> testcases = TestcaseService.getTestcases(pageable, contextInfo);
        return TestcaseMapper.pageEntityToDto(testcases);
    }

    /**
     * {@inheritdoc}
     */
    @ApiOperation(value = "View a list of validation errors for Testcase", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Validation errors"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/validate")
    public List<ValidationResultInfo> validateTestcase(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) TestcaseInfo testcaseInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        TestcaseEntity testcaseEntity = TestcaseMapper.dtoToModel(testcaseInfo);
        return TestcaseService
                .validateTestcase(validationTypeKey, testcaseEntity, contextInfo);
    }

}
