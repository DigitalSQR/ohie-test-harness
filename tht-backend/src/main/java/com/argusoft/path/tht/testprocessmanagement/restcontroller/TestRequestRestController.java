/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestMapper;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
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
 * This TestRequestServiceRestController maps end points with standard service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/test-request")
@Api(value = "REST API for TestRequest services", tags = {"TestRequest API"})
public class TestRequestRestController {

    @Autowired
    private TestRequestService TestRequestService;

    @Autowired
    private TestRequestMapper TestRequestMapper;

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Create new TestRequest", response = TestRequestInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created TestRequest"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("")
    @Transactional
    public TestRequestInfo createTestRequest(
            @RequestBody TestRequestInfo testRequestInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestRequestEntity testRequestEntity = TestRequestMapper.dtoToModel(testRequestInfo);
        testRequestEntity = TestRequestService.createTestRequest(testRequestEntity, contextInfo);
        return TestRequestMapper.modelToDto(testRequestEntity);

    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
//    @ApiOperation(value = "Update existing TestRequest", response = TestRequestInfo.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully updated TestRequest"),
//            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//
//    })
//    @PutMapping("")
    @Transactional
    public TestRequestInfo updateTestRequest(
            @RequestBody TestRequestInfo testRequestInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        TestRequestEntity testRequestEntity = TestRequestMapper.dtoToModel(testRequestInfo);
        testRequestEntity = TestRequestService.updateTestRequest(testRequestEntity, contextInfo);
        return TestRequestMapper.modelToDto(testRequestEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View a page of available filtered TestRequests", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("")
    public Page<TestRequestInfo> searchTestRequests(
            @RequestParam(name = "id", required = false) List<String> ids,
            TestRequestSearchFilter testRequestSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {

        Page<TestRequestEntity> testRequestEntities;
        if (!testRequestSearchFilter.isEmpty()
                || !CollectionUtils.isEmpty(ids)) {
            testRequestEntities = TestRequestService
                    .searchTestRequests(
                            ids,
                            testRequestSearchFilter,
                            pageable,
                            contextInfo);
            return TestRequestMapper.pageEntityToDto(testRequestEntities);
        }
        return this.getTestRequests(pageable, contextInfo);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available TestRequest with supplied id", response = TestRequestInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved TestRequest"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{testRequestId}")
    public TestRequestInfo getTestRequestById(
            @PathVariable("TestRequestId") String testRequestId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        TestRequestEntity testRequestById = TestRequestService.getTestRequestById(testRequestId, contextInfo);
        return TestRequestMapper.modelToDto(testRequestById);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    public Page<TestRequestInfo> getTestRequests(
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Page<TestRequestEntity> testRequests = TestRequestService.getTestRequests(pageable, contextInfo);
        return TestRequestMapper.pageEntityToDto(testRequests);
    }

    /**
     * {@inheritdoc}
     */
    @ApiOperation(value = "View a list of validation errors for TestRequest", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Validation errors"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("/validate")
    public List<ValidationResultInfo> validateTestRequest(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) TestRequestInfo testRequestInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        TestRequestEntity testRequestEntity = TestRequestMapper.dtoToModel(testRequestInfo);
        return TestRequestService
                .validateTestRequest(validationTypeKey, testRequestEntity, contextInfo);
    }

    @ApiOperation(value = "Start automation testing process", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully started automation testing process")
    })
    @PostMapping("/start-automation-testing-process/{testRequestId}")
    @Transactional
    public void startAutomationTestingProcess(
            @PathVariable("testRequestId") String testRequestId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException, DataValidationErrorException {
        TestRequestService.startAutomationTestingProcess(testRequestId, contextInfo);
    }

    @ApiOperation(value = "Start manual testing process", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully started manual testing process")
    })
    @PostMapping("/start-manual-testing-process/{testRequestId}")
    @Transactional
    public void startManualTestingProcess(
            @PathVariable("testRequestId") String testRequestId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException, DataValidationErrorException, DoesNotExistException, VersionMismatchException {
        TestRequestService.startManualTestingProcess(testRequestId, contextInfo);
    }
}