/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.restcontroller;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.service.AutomationTestProcessService;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * This AutomationTestProcessRestController maps end points with standard service.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@RestController
@RequestMapping("/automation-test-process")
@Api(value = "REST API for Automation services", tags = {"Automation API"})
@Metrics(registry = "AutomationTestProcessRestController")
public class AutomationTestProcessRestController {

    @Autowired
    private AutomationTestProcessService automationTestProcessService;

    @ApiOperation(value = "Start automation testing process", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully started automation testing process")
    })
    @Timed(name = "startAutomationTestingProcess")
    @GetMapping("/start-automation-testing-process/{testRequestId}")
    public void startAutomationTestingProcess(
            @PathVariable("testRequestId") String testRequestId,
            @RequestAttribute("contextInfo") ContextInfo contextInfo) throws OperationFailedException, InvalidParameterException {
        automationTestProcessService.startAutomationTestingProcess(testRequestId, contextInfo);
    }

}
