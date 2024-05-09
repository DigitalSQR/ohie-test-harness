package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.event.listener.TestcaseExecutionStarterListener;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.util.TestcaseExecutioner;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.ExecutionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public class JavaInBuiltEnvironmentStrategy implements ExecutionStrategy {

    private final String testcaseResultId;

    private final Map<String, IGenericClient> iGenericClientMap;

    private final TestcaseExecutioner testcaseExecutioner;

    private final ContextInfo contextInfo;

    public static final Logger LOGGER = LoggerFactory.getLogger(JavaInBuiltEnvironmentStrategy.class);

    public JavaInBuiltEnvironmentStrategy(String testcaseResultId, Map<String, IGenericClient> iGenericClientMap, TestcaseExecutioner testcaseExecutioner, ContextInfo contextInfo) {
        this.testcaseResultId = testcaseResultId;
        this.iGenericClientMap = iGenericClientMap;
        this.testcaseExecutioner = testcaseExecutioner;
        this.contextInfo = contextInfo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void execute(){
        try {
            testcaseExecutioner.markTestcaseResultInProgress(testcaseResultId, contextInfo);
            testcaseExecutioner.executeTestcase(testcaseResultId, iGenericClientMap, contextInfo);
        }catch (Exception e){
            LOGGER.error("caught Exception while executing the JavaInBuiltEnvironmentExecution Testcase ",e);
        }
    }
}
