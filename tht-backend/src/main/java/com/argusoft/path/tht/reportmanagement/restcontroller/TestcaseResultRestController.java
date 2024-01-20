/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.reportmanagement.restcontroller;

import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapper;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
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
 * This TestcaseResultServiceRestController maps end points with standard service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/testcase-result")
@Api(value = "REST API for TestcaseResult services", tags = {"TestcaseResult API"})
public class TestcaseResultRestController {

    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private TestcaseResultMapper testcaseResultMapper;

    /**
     * We can expose this API in future if needed.
     * {@inheritdoc}
     *
     * @return
     */
//    @ApiOperation(value = "Create new TestcaseResult", response = TestcaseResultInfo.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully created TestcaseResult"),
//            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//    })
//    @PostMapping("")
    @Transactional
    public TestcaseResultInfo createTestcaseResult(
            @RequestBody TestcaseResultInfo TestcaseResultInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException, DoesNotExistException, VersionMismatchException {

        TestcaseResultEntity TestcaseResultEntity = testcaseResultMapper.dtoToModel(TestcaseResultInfo);
        TestcaseResultEntity = testcaseResultService.createTestcaseResult(TestcaseResultEntity, contextInfo);
        return testcaseResultMapper.modelToDto(TestcaseResultEntity);

    }

    /**
     * We can expose this API in future if needed.
     * {@inheritdoc}
     *
     * @return
     */
//    @ApiOperation(value = "Update existing TestcaseResult", response = TestcaseResultInfo.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully updated TestcaseResult"),
//            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//
//    })
//    @PutMapping("")
    @Transactional
    public TestcaseResultInfo updateTestcaseResult(
            @RequestBody TestcaseResultInfo TestcaseResultInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        TestcaseResultEntity TestcaseResultEntity = testcaseResultMapper.dtoToModel(TestcaseResultInfo);
        TestcaseResultEntity = testcaseResultService.updateTestcaseResult(TestcaseResultEntity, contextInfo);
        return testcaseResultMapper.modelToDto(TestcaseResultEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View a page of available filtered TestcaseResults", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("")
    public Page<TestcaseResultInfo> searchTestcaseResults(
            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {
        Page<TestcaseResultEntity> testcaseResultEntities = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter, pageable, contextInfo);
        return testcaseResultMapper.pageEntityToDto(testcaseResultEntities);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Submit manual TestcaseResults", response = Page.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Submitted TestcaseResult"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PatchMapping("/submit/{testcaseResultId}/{selectedTestcaseOptionId}")
    public TestcaseResultInfo submitTestcaseResult(
            @PathVariable("testcaseResultId") String testcaseResultId,
            @PathVariable("selectedTestcaseOptionId") String selectedTestcaseOptionId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        TestcaseResultEntity testcaseResultEntity = testcaseResultService
                .submitTestcaseResult(
                        testcaseResultId,
                        selectedTestcaseOptionId,
                        contextInfo);
        return testcaseResultMapper.modelToDto(testcaseResultEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available TestcaseResult with supplied id", response = TestcaseResultInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved TestcaseResult"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{testcaseResultId}")
    public TestcaseResultInfo getTestcaseResultById(
            @PathVariable("testcaseResultId") String testcaseResultId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        TestcaseResultEntity testcaseResultById = testcaseResultService.getTestcaseResultById(testcaseResultId, contextInfo);
        return testcaseResultMapper.modelToDto(testcaseResultById);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    public Page<TestcaseResultInfo> getTestcaseResults(
            Pageable pageable,
            ContextInfo contextInfo)
            throws InvalidParameterException {
        Page<TestcaseResultEntity> TestcaseResults = testcaseResultService.getTestcaseResults(pageable, contextInfo);
        return testcaseResultMapper.pageEntityToDto(TestcaseResults);
    }

    /**
     * We can expose this API in future if needed.
     * {@inheritdoc}
     */
//    @ApiOperation(value = "View a list of validation errors for TestcaseResult", response = List.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully retrieved Validation errors"),
//            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//    })
//    @PostMapping("/validate")
    public List<ValidationResultInfo> validateTestcaseResult(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) TestcaseResultInfo TestcaseResultInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException, DataValidationErrorException {
        TestcaseResultEntity TestcaseResultEntity = testcaseResultMapper.dtoToModel(TestcaseResultInfo);
        return testcaseResultService
                .validateTestcaseResult(validationTypeKey, TestcaseResultEntity, contextInfo);
    }

}
