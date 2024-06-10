package com.argusoft.path.tht.testcasemanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseVariableInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseVariableEntity;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseVariableMapper;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseVariableService;
import com.google.common.collect.Multimap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This TestcaseVariablesServiceRestController maps end points with standard service.
 *
 * @author Aastha
 */
@RestController
@RequestMapping("/testcase-variable")
@Api(value = "REST API for TestcaseVariable services", tags = {"TestcaseVariables API"})
public class TestcaseVariableRestController {

    private TestcaseVariableService testcaseVariableService;

    private TestcaseVariableMapper testcaseVariablesMapper;


    @Autowired
    public void setTestcaseVariablesService(TestcaseVariableService testcaseVariableService) {
        this.testcaseVariableService = testcaseVariableService;
    }

    @Autowired
    public void setTestcaseVariablesMapper(TestcaseVariableMapper testcaseVariablesMapper) {
        this.testcaseVariablesMapper = testcaseVariablesMapper;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Retrieves all default values of component.", response = Multimap.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/inputs")
    public List<TestcaseVariableInfo> getTestcaseVariablesByComponentId(@RequestParam("componentId") String componentId, @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
        List<TestcaseVariableEntity> testcaseVariablesEntities = testcaseVariableService.getTestcaseVariablesByComponentId(componentId, contextInfo);
        return testcaseVariablesMapper.modelToDto(testcaseVariablesEntities);
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available Testcase variable with supplied id", response = TestcaseVariableInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Testcase"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{testcaseVariablesId}")
    public TestcaseVariableInfo getTestcaseVariablesById(
            @PathVariable("testcaseVariablesId") String testcaseVariablesId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException {

        TestcaseVariableEntity testcaseVariablesById = testcaseVariableService.getTestcaseVariableById(testcaseVariablesId, contextInfo);
        return testcaseVariablesMapper.modelToDto(testcaseVariablesById);
    }
}