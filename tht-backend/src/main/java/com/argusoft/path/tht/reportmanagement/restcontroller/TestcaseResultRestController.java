package com.argusoft.path.tht.reportmanagement.restcontroller;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapper;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
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
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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
    @PatchMapping("/submit/{testcaseResultId}")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin','role.tester')")
    public TestcaseResultInfo submitTestcaseResult(
            @PathVariable("testcaseResultId") String testcaseResultId,
            @RequestParam(value = "selectedTestcaseOptionId",required = true) Set<String> selectedTestcaseOptionIds,
            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        TestcaseResultEntity testcaseResultEntity = testcaseResultService
                .submitTestcaseResult(
                        testcaseResultId,
                        selectedTestcaseOptionIds,
                        contextInfo);
        return testcaseResultMapper.modelToDto(testcaseResultEntity);
    }

    @ApiOperation(value = "Patch Update For TestcaseResultEntity", response = TestcaseResultInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Updated TestcaseResult"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PatchMapping(value = "/{testcaseResultId}", consumes = "application/json-patch+json")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize(value = "hasAnyAuthority('role.admin','role.tester')")
    public TestcaseResultInfo submitTestcaseResult(
            @PathVariable("testcaseResultId") String testcaseResultId,
            @RequestBody JsonPatch jsonPatch,
            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {

        TestcaseResultInfo testcaseResultById = this.getTestcaseResultById(testcaseResultId, contextInfo);

        TestcaseResultInfo testcaseResultPatched = applyPatchToTestcaseInfo(jsonPatch, testcaseResultById);

        // update and return
        TestcaseResultEntity testcaseResultEntity = testcaseResultMapper.dtoToModel(testcaseResultPatched);
        testcaseResultEntity = testcaseResultService.updateTestcaseResult(testcaseResultEntity,contextInfo);
        return testcaseResultMapper.modelToDto(testcaseResultEntity);
    }

    private TestcaseResultInfo applyPatchToTestcaseInfo(JsonPatch jsonPatch, TestcaseResultInfo testcaseResultInfo) throws OperationFailedException{
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode patched = jsonPatch.apply(objectMapper.convertValue(testcaseResultInfo, JsonNode.class));
            return objectMapper.treeToValue(patched, TestcaseResultInfo.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new OperationFailedException("Caught Exception while processing Json ",e);
        }
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


//    @ApiOperation(value = "To change status of TestcaseResult", response = DocumentInfo.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successfully updated TestcaseResult"),
//            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
//            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")
//    })
//    @PatchMapping("/state/{testcaseResultId}/{changeState}")
//    @Transactional
    public TestcaseResultInfo updateTestcaseResultState(@PathVariable("testcaseResultId") String testcaseResultId,
                                                        @PathVariable("changeState") String changeState,
                                                        @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        TestcaseResultEntity testcaseResultEntity = testcaseResultService.changeState(testcaseResultId, changeState, contextInfo);
        return testcaseResultMapper.modelToDto(testcaseResultEntity);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Retrieves a TestcaseResult corresponding to the given filters", response = TestcaseResultInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved TestcaseResult"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/status/{testcaseResultId}")
    public TestcaseResultInfo getTestcaseResultStatus(
            @PathVariable("testcaseResultId") String testcaseResultId,
            @RequestParam(value = "manual", required = false) Boolean isManual,
            @RequestParam(value = "automated", required = false) Boolean isAutomated,
            @RequestParam(value ="required", required = false) Boolean isRequired,
            @RequestParam(value ="recommended", required = false) Boolean isRecommended,
            @RequestParam(value ="workflow", required = false) Boolean isWorkflow,
            @RequestParam(value = "functional", required = false) Boolean isFunctional,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException {

        TestcaseResultEntity testcaseResultById = testcaseResultService.getTestcaseResultStatus(
                testcaseResultId,
                isManual,
                isAutomated,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                contextInfo);
        return testcaseResultMapper.modelToDto(testcaseResultById);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Retrieves classes extending Test case class.", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved classes name"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/sub-classes")
    public List<String> getSubClassesNameForTestCase(){
        return testcaseResultService.getSubClassesNameForTestCase();
    }

    @ApiOperation(value = "Retrieves all status of test case result.", response = Multimap.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/status/mapping")
    public List<String> getStatusMapping(@RequestParam("sourceStatus") String sourceStatus) {
        Collection<String> strings = TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_MAP.get(sourceStatus);
        return strings.parallelStream().toList();
    }

}
