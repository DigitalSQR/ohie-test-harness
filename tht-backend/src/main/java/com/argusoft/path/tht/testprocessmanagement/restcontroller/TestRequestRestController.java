package com.argusoft.path.tht.testprocessmanagement.restcontroller;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.filter.TestRequestCriteriaSearchFilter;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestMapper;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestService;
import com.google.common.collect.Multimap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
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
    private TestRequestService testRequestService;

    @Autowired
    private TestRequestMapper testRequestMapper;

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
            DataValidationErrorException, DoesNotExistException {

        TestRequestEntity testRequestEntity = testRequestMapper.dtoToModel(testRequestInfo);
        testRequestEntity = testRequestService.createTestRequest(testRequestEntity, contextInfo);
        return testRequestMapper.modelToDto(testRequestEntity);

    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Update existing TestRequest", response = TestRequestInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated TestRequest"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PutMapping("")
    @Transactional
    public TestRequestInfo updateTestRequest(
            @RequestBody TestRequestInfo testRequestInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        TestRequestEntity testRequestEntity = testRequestMapper.dtoToModel(testRequestInfo);
        testRequestEntity = testRequestService.updateTestRequest(testRequestEntity, contextInfo);
        return testRequestMapper.modelToDto(testRequestEntity);
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
            TestRequestCriteriaSearchFilter testRequestSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException, DoesNotExistException {

        Page<TestRequestEntity> testRequestEntities = testRequestService.searchTestRequests(testRequestSearchFilter, pageable, contextInfo);
        return testRequestMapper.pageEntityToDto(testRequestEntities);
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
            @PathVariable("testRequestId") String testRequestId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        TestRequestEntity testRequestById = testRequestService.getTestRequestById(testRequestId, contextInfo);
        return testRequestMapper.modelToDto(testRequestById);
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
        TestRequestEntity testRequestEntity = testRequestMapper.dtoToModel(testRequestInfo);
        return testRequestService
                .validateTestRequest(validationTypeKey, testRequestEntity, contextInfo);
    }

    @ApiOperation(value = "Reinitialize automation testing process", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully started automation testing process")
    })
    @PutMapping("/start-testing-process/{testRequestId}")
    public void startTestingProcess(
            @PathVariable("testRequestId") String testRequestId,
            @RequestParam(value = "refObjUri") String refObjUri,
            @RequestParam(value = "refId") String refId,
            @RequestParam(value = "manual", required = false) Boolean isManual,
            @RequestParam(value = "automated", required = false) Boolean isAutomated,
            @RequestParam(value ="required", required = false) Boolean isRequired,
            @RequestParam(value ="recommended", required = false) Boolean isRecommended,
            @RequestParam(value ="workflow", required = false) Boolean isWorkflow,
            @RequestParam(value = "functional", required = false) Boolean isFunctional,
            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        testRequestService.startTestingProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                contextInfo);
    }

    @ApiOperation(value = "Reinitialize automation testing process", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully started automation testing process")
    })
    @PutMapping("/reinitialize-testing-process/{testRequestId}")
    @Transactional
    public void reinitializeTestingProcess(
            @PathVariable("testRequestId") String testRequestId,
            @RequestParam("refObjUri") String refObjUri,
            @RequestParam("refId") String refId,
            @RequestParam(value = "manual", required = false) Boolean isManual,
            @RequestParam(value = "automated", required = false) Boolean isAutomated,
            @RequestParam(value ="required", required = false) Boolean isRequired,
            @RequestParam(value ="recommended", required = false) Boolean isRecommended,
            @RequestParam(value ="workflow", required = false) Boolean isWorkflow,
            @RequestParam(value = "functional", required = false) Boolean isFunctional,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException, InvalidParameterException, DataValidationErrorException {
        testRequestService.reinitializeTestingProcess(
                testRequestId,
                refObjUri,
                refId,
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                contextInfo);
    }

    @ApiOperation(value = "To change status of TestRequest", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated TestRequest"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping("/state/{testRequestId}/{changeState}")
    @Transactional
    public TestRequestInfo updateTestRequestState(@PathVariable("testRequestId") String testRequestId,
                                                  @PathVariable("changeState") String changeState,
                                                  @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        TestRequestEntity testRequestEntity = testRequestService.changeState(testRequestId, changeState, contextInfo);
        return testRequestMapper.modelToDto(testRequestEntity);
    }

    @ApiOperation(value = "Retrieves all status of test request.", response = Multimap.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("status/mapping")
    public List<String> getStatusMapping(@RequestParam("sourceStatus") String sourceStatus) throws IOException {
        Collection<String> strings = TestRequestServiceConstants.TEST_REQUEST_STATUS_MAP.get(sourceStatus);
        return strings.parallelStream().toList();
    }
}
