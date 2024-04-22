package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.impl;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.dto.status.response.StatusResponse;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.StatusCallback;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.util.EuTestBedCommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class StatusCallbackHandler implements StatusCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusCallbackHandler.class);

    private TestcaseResultService testcaseResultService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onSuccessStatusReceived(StatusResponse statusResponse, String testSessionId, ContextInfo contextInfo) throws OperationFailedException {
        handleStatusReceived(statusResponse, testSessionId, contextInfo, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onFailureStatusReceived(StatusResponse statusResponse, String testSessionId, ContextInfo contextInfo) throws OperationFailedException {
        handleStatusReceived(statusResponse, testSessionId, contextInfo, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onOutOfDurationTime(String testSessionId, ContextInfo contextInfo) throws OperationFailedException {
        try {
            TestcaseResultEntity testcaseResultById = getTestCaseResultEntityBasedOnTestSessionId(testSessionId, testcaseResultService ,contextInfo);
            testcaseResultById.setSuccess(Boolean.FALSE);
            testcaseResultById.setMessage("There was an error encountered while attempting to retrieve the updated status from the EU TEST BED. Multiple attempts were made.");
            testcaseResultById.setDuration(System.currentTimeMillis() - testcaseResultById.getUpdatedAt().getTime());
            testcaseResultService.updateTestcaseResult(testcaseResultById, contextInfo);
            testcaseResultService.changeState(testcaseResultById.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);
        } catch (Exception e) {
            LOGGER.error("Caught Exception while updating testcase result", e);
            throw new OperationFailedException("Operation Failed : Something Went Wrong While Updating Internal Records", e);
        }
    }

    @Override
    public void onRestClientExceptionReceived(String testSessionId, RestClientException e, ContextInfo contextInfo) {
        LOGGER.error("Caught Rest Client Exception while updating testcase result",e);
    }

    private void handleStatusReceived(StatusResponse statusResponse, String testSessionId, ContextInfo contextInfo, boolean isSuccess) throws OperationFailedException {
        try {
            TestcaseResultEntity testcaseResultById = getTestCaseResultEntityBasedOnTestSessionId(testSessionId, testcaseResultService ,contextInfo);
            testcaseResultById.setSuccess(isSuccess);
            testcaseResultById.setMessage(statusResponse.getSessions().get(0).getMessage());
            testcaseResultById.setDuration(System.currentTimeMillis() - testcaseResultById.getUpdatedAt().getTime());
            testcaseResultService.updateTestcaseResult(testcaseResultById, contextInfo);
            testcaseResultService.changeState(testcaseResultById.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);
        } catch (Exception e) {
            LOGGER.error("Caught Exception while updating testcase result", e);
            throw new OperationFailedException("Operation Failed : Something Went Wrong While Updating Internal Records", e);
        }
    }
    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }

    public TestcaseResultEntity getTestCaseResultEntityBasedOnTestSessionId(String testSessionId, TestcaseResultService testcaseResultService, ContextInfo contextInfo) throws OperationFailedException {
        try {
            TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
            testcaseResultCriteriaSearchFilter.setTestSessionId(testSessionId);
            return testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter, contextInfo).stream().findFirst().orElseThrow();
        }catch (Exception e){
            LOGGER.error("OperationFailedException occurred while checking session status", e);
            throw new OperationFailedException("Operation Failed : Something Went Wrong While Updating Internal Records", e);
        }
    }
}