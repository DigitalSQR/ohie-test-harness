package com.argusoft.path.tht.reportmanagement.restcontroller;

import com.argusoft.path.tht.reportmanagement.models.dto.GradeInfo;
import com.argusoft.path.tht.reportmanagement.models.entity.GradeEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.GradeMapper;
import com.argusoft.path.tht.reportmanagement.service.GradeService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grade")
@Api(value = "REST API for Grade services", tags = {"Grade API"})
public class GradeRestController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private GradeMapper gradeMapper;

    @ApiOperation(value = "View a list of available grade")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved list"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/all")
    public List<GradeInfo> getGrades(
            @RequestAttribute("contextInfo") ContextInfo contextInfo) {
        List<GradeEntity> allGrades = gradeService.getAllGrades(contextInfo);
        return gradeMapper.modelToDto(allGrades);
    }

    @ApiOperation(value = "View available grade with supplied id", response = GradeInfo.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved grade"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/{gradeId}")
    public GradeInfo getGrade(@PathVariable("gradeId") String gradeId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws DoesNotExistException, OperationFailedException {
        GradeEntity gradeById = gradeService.getGradeById(gradeId, contextInfo);
        return gradeMapper.modelToDto(gradeById);
    }

}
