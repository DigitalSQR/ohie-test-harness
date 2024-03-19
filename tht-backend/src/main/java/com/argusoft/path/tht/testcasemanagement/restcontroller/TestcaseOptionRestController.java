package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseOptionInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseOptionMapper;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
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

import java.util.Collection;
import java.util.List;

/**
 * This TestcaseOptionServiceRestController maps end points with standard
 * service.
 *
 * @author Dhruv
 */
@RestController
@RequestMapping("/testcase-option")
@Api(value = "REST API for TestcaseOption services", tags = {"TestcaseOption API"})
public class TestcaseOptionRestController {

    private TestcaseOptionService testcaseOptionService;
    private TestcaseOptionMapper testcaseOptionMapper;

    @Autowired
    public void setTestcaseOptionService(TestcaseOptionService testcaseOptionService) {
        this.testcaseOptionService = testcaseOptionService;
    }

    @Autowired
    public void setTestcaseOptionMapper(TestcaseOptionMapper testcaseOptionMapper) {
        this.testcaseOptionMapper = testcaseOptionMapper;
    }

    /**
     * We can expose this API in future if needed. {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Create new TestcaseOption", response = TestcaseOptionInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created TestcaseOption"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PostMapping("")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public TestcaseOptionInfo createTestcaseOption(
            @RequestBody TestcaseOptionInfo testcaseOptionInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            DataValidationErrorException {

        TestcaseOptionEntity testcaseOptionEntity = testcaseOptionMapper.dtoToModel(testcaseOptionInfo);
        testcaseOptionEntity = testcaseOptionService.createTestcaseOption(testcaseOptionEntity, contextInfo);
        return testcaseOptionMapper.modelToDto(testcaseOptionEntity);

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
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public TestcaseOptionInfo updateTestcaseOption(
            @RequestBody TestcaseOptionInfo testcaseOptionInfo,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException,
            VersionMismatchException,
            DataValidationErrorException {

        TestcaseOptionEntity testcaseOptionEntity = testcaseOptionMapper.dtoToModel(testcaseOptionInfo);
        testcaseOptionEntity = testcaseOptionService.updateTestcaseOption(testcaseOptionEntity, contextInfo);
        return testcaseOptionMapper.modelToDto(testcaseOptionEntity);
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
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {

        Page<TestcaseOptionEntity> testcaseOptionEntities = testcaseOptionService.searchTestcaseOptions(testcaseOptionCriteriaSearchFilter, pageable, contextInfo);
        return testcaseOptionMapper.pageEntityToDto(testcaseOptionEntities);
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
            @PathVariable("testcaseOptionId") String testcaseOptionId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException {

        TestcaseOptionEntity testcaseOptionById = testcaseOptionService.getTestcaseOptionById(testcaseOptionId, contextInfo);
        return testcaseOptionMapper.modelToDto(testcaseOptionById);
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
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public List<ValidationResultInfo> validateTestcaseOption(
            @RequestParam(name = "validationTypeKey",
                    required = true) String validationTypeKey,
            @RequestBody(required = true) TestcaseOptionInfo testcaseOptionInfo,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws InvalidParameterException,
            OperationFailedException {
        TestcaseOptionEntity testcaseOptionEntity = testcaseOptionMapper.dtoToModel(testcaseOptionInfo);
        return testcaseOptionService
                .validateTestcaseOption(validationTypeKey, testcaseOptionEntity, contextInfo);
    }

    @ApiOperation(value = "To change status of TestcaseOption", response = DocumentInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated TestcaseOption"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping("/state/{testcaseOptionId}/{changeState}")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public TestcaseOptionInfo updateTestcaseOptionState(@PathVariable("testcaseOptionId") String testcaseOptionId,
                                                        @PathVariable("changeState") String changeState,
                                                        @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        TestcaseOptionEntity testcaseOptionEntity = testcaseOptionService.changeState(testcaseOptionId, changeState, contextInfo);
        return testcaseOptionMapper.modelToDto(testcaseOptionEntity);
    }

    @ApiOperation(value = "Patch method for Test case option", response = TestcaseOptionInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated TestcaseOption"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
    })
    @PatchMapping(value = "/{testcaseOptionId}", consumes = "application/json-patch+json")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin')")
    public TestcaseOptionInfo patchTestcaseOption(@PathVariable("testcaseOptionId") String testcaseOptionId,
                                                  @RequestBody JsonPatch jsonPatch,
                                                  @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {

        // get from database
        TestcaseOptionInfo testcaseOptionById = this.getTestcaseOptionById(testcaseOptionId, contextInfo);

        // apply patch
        TestcaseOptionInfo patchedTestcaseInfo = applyPatchToTestcaseInfo(jsonPatch, testcaseOptionById);

        // update and return
        TestcaseOptionEntity updateEntity = testcaseOptionMapper.dtoToModel(patchedTestcaseInfo);
        updateEntity = testcaseOptionService.updateTestcaseOption(updateEntity, contextInfo);
        return testcaseOptionMapper.modelToDto(updateEntity);
    }

    private TestcaseOptionInfo applyPatchToTestcaseInfo(JsonPatch jsonPatch, TestcaseOptionInfo testcaseOptionInfo) throws OperationFailedException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode patched = jsonPatch.apply(objectMapper.convertValue(testcaseOptionInfo, JsonNode.class));
            return objectMapper.treeToValue(patched, TestcaseOptionInfo.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new OperationFailedException("Caught Exception while processing Json ", e);
        }
    }

    @ApiOperation(value = "Retrieves all status of test case option.", response = Multimap.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/status/mapping")
    public List<String> getStatusMapping(@RequestParam("sourceStatus") String sourceStatus) {
        Collection<String> strings = TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_MAP.get(sourceStatus);
        return strings.parallelStream().toList();
    }
}
