package com.argusoft.path.tht.testprocessmanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestValueInfo;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestValueEntity;
import com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestValueMapper;
import com.argusoft.path.tht.testprocessmanagement.service.TestRequestValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This TestcaseVariableServiceRestController maps end points with standard service.
 *
 * @author Aastha
 */
@RestController
@RequestMapping("/test-request-value")
@Api(value = "REST API for TestcaseVariable services", tags = {"TestcaseVariable API"})
public class TestRequestValueRestController {

    private TestRequestValueService testRequestValueService;

    private TestRequestValueMapper testRequestValueMapper;

    @Autowired
    public void setTestRequestValueService(TestRequestValueService testRequestValueService) {
        this.testRequestValueService = testRequestValueService;
    }

    @Autowired
    public void setTestRequestValueMapper(TestRequestValueMapper testRequestValueMapper) {
        this.testRequestValueMapper = testRequestValueMapper;
    }

    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "View available Testcase request value with supplied id", response = TestRequestValueInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved Testcase"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{testRequestValueId}")
    public TestRequestValueInfo getTestRequestValueById(
            @PathVariable("testRequestValueId") String testRequestValueId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException, OperationFailedException {

        TestRequestValueEntity testRequestValueById = testRequestValueService.getTestRequestValueById(testRequestValueId, contextInfo);
        return testRequestValueMapper.modelToDto(testRequestValueById);
    }



    /**
     * {@inheritdoc}
     *
     * @return
     */
    @ApiOperation(value = "Update existing Test Request value", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated Testcase"),
            @ApiResponse(code = 401, message = "You are not authorized to create the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")

    })
    @PutMapping(value = "/updateValue")
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyAuthority('role.tester', 'role.admin')")
    public List<TestRequestValueInfo> updateTestRequestValues(
            @RequestBody List<TestRequestValueInfo> testRequestValueInfos,
            @RequestAttribute(name = "contextInfo") ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, OperationFailedException, DataValidationErrorException, VersionMismatchException {

        List<TestRequestValueEntity> testRequestValueEntities = testRequestValueMapper.dtoToModel(testRequestValueInfos);
        testRequestValueEntities = testRequestValueService.updateTestRequestValues(testRequestValueEntities, contextInfo);
        return testRequestValueMapper.modelToDto(testRequestValueEntities);
    }
}
