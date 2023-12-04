/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.argusoft.path.tht.testprocessmanagement.service.impl;

import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestcaseExecutionar;
import com.argusoft.path.tht.testprocessmanagement.service.AutomationTestProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This AutomationServiceImpl contains implementation for Automation Test Process service.
 *
 * @author dhruv
 * @since 2023-09-13
 */
@Service
public class AutomationTestProcessServiceImpl implements AutomationTestProcessService {

    @Autowired
    private TestcaseExecutionar testcaseExecutionar;

    public void startAutomationTestingProcess(String testRequestId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException {
        testcaseExecutionar.executeAutomationTestingByTestRequest(testRequestId, contextInfo);
    }

}
