package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.RestControllerUtils;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseOptionCriteriaSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseOptionInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseOptionMapper;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseOptionService;
import com.google.common.collect.Multimap;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
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
    private TestcaseOptionService testcaseOptionService;

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
        testcaseOptionEntity = testcaseOptionService.createTestcaseOption(testcaseOptionEntity, contextInfo);
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
        testcaseOptionEntity = testcaseOptionService.updateTestcaseOption(testcaseOptionEntity, contextInfo);
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
            TestcaseOptionCriteriaSearchFilter testcaseOptionCriteriaSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws OperationFailedException,
            InvalidParameterException {

        TestcaseOptionCriteriaSearchFilter filteredFilter = RestControllerUtils.filterFields(testcaseOptionCriteriaSearchFilter, TestcaseOptionCriteriaSearchFilter.class);
        Page<TestcaseOptionEntity> testcaseOptionEntities = testcaseOptionService.searchTestcaseOptions(filteredFilter, pageable, contextInfo);
        return TestcaseOptionMapper.pageEntityToDto(testcaseOptionEntities);
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
        return TestcaseOptionMapper.modelToDto(testcaseOptionById);
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
    @Transactional
    public TestcaseOptionInfo updateTestcaseOptionState(@PathVariable("testcaseOptionId") String testcaseOptionId,
                                                        @PathVariable("changeState") String changeState,
                                                        @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, OperationFailedException, VersionMismatchException {
        TestcaseOptionEntity testcaseOptionEntity = testcaseOptionService.changeState(testcaseOptionId, changeState, contextInfo);
        return TestcaseOptionMapper.modelToDto(testcaseOptionEntity);
    }

    @ApiOperation(value = "Retrieves all status of test case option.", response = Multimap.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/status/mapping")
    public List<String> getStatusMapping(@RequestParam("sourceStatus") String sourceStatus) throws IOException {
        Collection<String> strings = TestcaseOptionServiceConstants.TESTCASE_OPTION_STATUS_MAP.get(sourceStatus);
        return strings.parallelStream().toList();
    }
}
