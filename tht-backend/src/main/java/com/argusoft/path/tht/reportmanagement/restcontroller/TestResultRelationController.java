package com.argusoft.path.tht.reportmanagement.restcontroller;

import com.argusoft.path.tht.reportmanagement.filter.TestResultRelationCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.dto.TestResultRelationInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.TestResultRelationEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestResultRelationMapper;
import com.argusoft.path.tht.reportmanagement.service.TestResultRelationService;
import com.argusoft.path.tht.systemconfiguration.audit.constant.AuditServiceConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DataValidationErrorException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This TestResultRelationServiceRestController maps end points with standard service.
 *
 * @author Hardik
 */

@RestController
@RequestMapping("/testcase-result-relation")
@Api(value = "REST API for Grade services", tags = {"Test Result Relation API"})
public class TestResultRelationController {

    private TestResultRelationService testResultRelationService;
    private TestResultRelationMapper testResultRelationMapper;

    @Autowired
    public void setTestResultRelationService(TestResultRelationService testResultRelationService) {
        this.testResultRelationService = testResultRelationService;
    }

    @Autowired
    public void setTestResultRelationMapper(TestResultRelationMapper testResultRelationMapper) {
        this.testResultRelationMapper = testResultRelationMapper;
    }

    @ApiOperation(value = "Get Objects of Test Result Relation", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Retrieved TestcaseResultRelationService"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were attempting to reach could not be found.")
    })
    @GetMapping("/{testcaseResultId}/{refObjUri}")
    public List<Object> getTestcaseResultRelatedObject(
            @PathVariable("testcaseResultId") String testcaseResultId,
            @PathVariable("refObjUri") String refObjUri,
            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException {
        List<Object> testResultRelationEntitiesFromAuditMapping = testResultRelationService.getTestResultRelationEntitiesFromAuditMapping(testcaseResultId, refObjUri, contextInfo);
        AuditServiceConstant.EntityType entityType = AuditServiceConstant.EntityType.getEntityTypeBasedOnRefObjectUri(refObjUri);
        return entityType.createMapperObject(entityType).modelToDto(testResultRelationEntitiesFromAuditMapping);
    }

    @ApiOperation(value = "Get Objects of Test Result Relation", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully Retrieved TestcaseResultRelationService"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were attempting to reach could not be found.")
    })
    @GetMapping("")
    public Page<TestResultRelationInfo> SearchTestResultRelationInfo(
            TestResultRelationCriteriaSearchFilter testResultRelationCriteriaSearchFilter,
            Pageable pageable,
            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        Page<TestResultRelationEntity> testResultRelationEntities = testResultRelationService.searchTestResultRelation(testResultRelationCriteriaSearchFilter, pageable, contextInfo);
        return testResultRelationMapper.pageEntityToDto(testResultRelationEntities);
    }

}
