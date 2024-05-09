package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.start.response.StartResponse;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.ExecutionStrategy;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.UpdateSessionAndMarkTestcaseResultInProgress;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.TestSessionStarter;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.VerifyTestcaseSessionStatusAndUpdateAccordingly;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.StatusCallback;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.util.EuTestBedCommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;
import java.util.Map;

public class EuTestBedEnvironmentStrategy implements ExecutionStrategy {

    public static final Logger LOGGER = LoggerFactory.getLogger(EuTestBedEnvironmentStrategy.class);
    private final String testcaseResultId;

    private final TestcaseResultService testcaseResultService;

    private final UpdateSessionAndMarkTestcaseResultInProgress updateSessionAndMarkTestcaseResultInProgress;

    private final VerifyTestcaseSessionStatusAndUpdateAccordingly verifyTestcaseSessionStatusAndUpdateAccordingly;

    private final ContextInfo contextInfo;

    private final TestSessionStarter testSessionStarter;

    private final String testSuiteId;

    private final String sutActorId;

    private final StatusCallback statusCallback;

    private final EuTestBedCommonUtil euTestBedCommonUtil;
    private final Map<String,String> inputParameters;


    public EuTestBedEnvironmentStrategy(String testcaseResultId, TestcaseResultService testcaseResultService,
                                        UpdateSessionAndMarkTestcaseResultInProgress updateSessionAndMarkTestcaseResultInProgress,
                                        VerifyTestcaseSessionStatusAndUpdateAccordingly verifyTestcaseSessionStatusAndUpdateAccordingly,
                                        TestSessionStarter testSessionStarter, String testSuiteId, StatusCallback statusCallback, Map<String, String> inputParameters, EuTestBedCommonUtil euTestBedCommonUtil, String sutActorId, ContextInfo contextInfo) {
        this.testcaseResultId = testcaseResultId;
        this.testcaseResultService = testcaseResultService;
        this.verifyTestcaseSessionStatusAndUpdateAccordingly = verifyTestcaseSessionStatusAndUpdateAccordingly;
        this.contextInfo = contextInfo;
        this.updateSessionAndMarkTestcaseResultInProgress = updateSessionAndMarkTestcaseResultInProgress;
        this.testSessionStarter = testSessionStarter;
        this.testSuiteId = testSuiteId;
        this.statusCallback = statusCallback;
        this.inputParameters = inputParameters;
        this.sutActorId = sutActorId;
        this.euTestBedCommonUtil = euTestBedCommonUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void execute(){
        try {
            try {
                StartResponse startResponse = testSessionStarter.processWithInputParams(testSuiteId, sutActorId, inputParameters ,contextInfo);
                if (startResponse != null && !startResponse.getCreatedSessions().isEmpty()) {
                    String session = startResponse.getCreatedSessions().get(0).getSession();
                    updateSessionAndMarkTestcaseResultInProgress.process(testcaseResultId, session, contextInfo);
                    verifyTestcaseSessionStatusAndUpdateAccordingly.process(session, statusCallback, contextInfo);
                }
            }catch (URISyntaxException uriSyntaxException){
                euTestBedCommonUtil.handleURISyntaxException(testcaseResultId, testcaseResultService, uriSyntaxException, contextInfo);
            }catch (RestClientException restClientException){
                euTestBedCommonUtil.handleRestClientException(testcaseResultId, testcaseResultService, restClientException, contextInfo);
            }
        }catch (Exception e){
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        try {
            TestcaseResultEntity testcaseResultById = testcaseResultService.getTestcaseResultById(testcaseResultId, contextInfo);
            testcaseResultById.setSuccess(Boolean.FALSE);
            testcaseResultById.setMessage(e instanceof OperationFailedException ? e.getMessage() : "Exception Occurred : Something went wrong while executing Testsuite in Testbed.");
            testcaseResultById.setDuration(System.currentTimeMillis() - testcaseResultById.getUpdatedAt().getTime());
            testcaseResultService.updateTestcaseResult(testcaseResultById, contextInfo);
            testcaseResultService.changeState(testcaseResultById.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);
        }catch (Exception ex){
            LOGGER.error("Caught Exception while executing testcase",ex);
        }
    }




}
