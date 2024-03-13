package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseMapper;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
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
 * This TestcaseServiceRestController maps end points with standard service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/testcase")
@Api(value = "REST API for Testcase services", tags = {"Testcase API"})
public class TestcaseRestController {

    @Autowired
    private TestcaseService testcaseService;

    @Autowired
    private TestcaseMapper testcaseMapper;

    /**
     * We can expose this API in future if needed. {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Create new Testcase", response = TestcaseInfo.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully created Testcase"),
        @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public TestcaseInfo createTestcase(
            @RequestBody TestcaseInfo testcaseInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseEntity testcaseEntity = testcaseMapper.dtoToModel(testcaseInfo);
        testcaseEntity = testcaseService.createTestcase(testcaseEntity, contextInfo);
        return testcaseMapper.modelToDto(testcaseEntity);

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
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public TestcaseInfo updateTestcase(
            @RequestBody TestcaseInfo testcaseInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws
            OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        TestcaseEntity testcaseEntity = testcaseMapper.dtoToModel(testcaseInfo);
        testcaseEntity = testcaseService.updateTestcase(testcaseEntity, contextInfo);
        return testcaseMapper.modelToDto(testcaseEntity);
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
            TestcaseCriteriaSearchFilter testcaseCriteriaSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {

        Page<TestcaseEntity> testcaseEntities = testcaseService.searchTestcases(testcaseCriteriaSearchFilter, pageable, contextInfo);
        return testcaseMapper.pageEntityToDto(testcaseEntities);
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

        TestcaseEntity testcaseById = testcaseService.getTestcaseById(testcaseId, contextInfo);
        return testcaseMapper.modelToDto(testcaseById);
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
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public List<ValidationResultInfo> validateTestcase(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) TestcaseInfo testcaseInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        TestcaseEntity testcaseEntity = testcaseMapper.dtoToModel(testcaseInfo);
        return testcaseService
                .validateTestcase(validationTypeKey, testcaseEntity, contextInfo);
    }

    @ApiOperation(value = "To change status of Testcase", response = TestcaseEntity.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated Testcase"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping("/state/{testcaseId}/{changeState}")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public TestcaseInfo updateTestcaseState(@PathVariable("testcaseId") String testcaseId,
            @PathVariable("changeState") String changeState,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        TestcaseEntity testcaseEntity = testcaseService.changeState(testcaseId, changeState, contextInfo);
        return testcaseMapper.modelToDto(testcaseEntity);
    }

    @ApiOperation(value = "To apply patch to the Testcase", response = TestcaseInfo.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated Testcase"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping(value = "/{testcaseId}", consumes = "application/json-patch+json")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public TestcaseInfo applyPatchTestcase(@PathVariable("testcaseId") String testcaseId,
            @RequestBody JsonPatch jsonPatch,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        //get from database
        TestcaseInfo testcaseById = this.getTestcaseById(testcaseId, contextInfo);

        //apply patch
        TestcaseInfo testcasePatched = applyPatchToTestcaseInfo(jsonPatch, testcaseById);

        // update and return
        TestcaseEntity entity = testcaseMapper.dtoToModel(testcasePatched);
        entity = testcaseService.updateTestcase(entity, contextInfo);
        return testcaseMapper.modelToDto(entity);
    }

    private TestcaseInfo applyPatchToTestcaseInfo(JsonPatch jsonPatch, TestcaseInfo testcaseEntityById) throws OperationFailedException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode patched = jsonPatch.apply(objectMapper.convertValue(testcaseEntityById, JsonNode.class));
            return objectMapper.treeToValue(patched, TestcaseInfo.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new OperationFailedException("Caught Exception while processing Json ", e);
        }
    }

    @ApiOperation(value = "Retrieves all status of test case.", response = Multimap.class)
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/status/mapping")
    public List<String> getStatusMapping(@RequestParam("sourceStatus") String sourceStatus) throws IOException {
        Collection<String> strings = TestcaseServiceConstants.TESTCASE_STATUS_MAP.get(sourceStatus);
        return strings.parallelStream().toList();
    }
}
