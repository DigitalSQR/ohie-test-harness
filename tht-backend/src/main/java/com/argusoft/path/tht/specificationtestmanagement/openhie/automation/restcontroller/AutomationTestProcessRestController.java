/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.specificationtestmanagement.openhie.automation.restcontroller;

import com.argusoft.path.tht.specificationtestmanagement.openhie.automation.service.AutomationTestProcessService;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/start-automation-testing-process")
    public void startAutomationTestingProcess(
            @RequestAttribute("contextInfo") ContextInfo contextInfo) {
        automationTestProcessService.startAutomationTestingProcess(contextInfo);
    }

}
