package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.util;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;

@Component
public class EuTestBedCommonUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(EuTestBedCommonUtil.class);



    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void handleURISyntaxException(String testcaseResultId, TestcaseResultService testcaseResultService , URISyntaxException e, ContextInfo contextInfo) throws OperationFailedException {
        try {
            TestcaseResultEntity testcaseResultById = testcaseResultService.getTestcaseResultById(testcaseResultId, contextInfo);
            testcaseResultById.setSuccess(Boolean.FALSE);
            testcaseResultById.setMessage("Internal Error : URL Syntax Exception "+ e.getInput());
            testcaseResultById.setDuration(System.currentTimeMillis() - testcaseResultById.getUpdatedAt().getTime());
            testcaseResultService.updateTestcaseResult(testcaseResultById, contextInfo);

            testcaseResultService.changeState(testcaseResultById.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);

        } catch (DoesNotExistException | InvalidParameterException | DataValidationErrorException |
                 VersionMismatchException ex) {
            LOGGER.error("OperationFailedException occurred while checking session status", e);
            throw new OperationFailedException("Operation Failed : Something Went Wrong While Updating Internal Records", e);
        }
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void handleRestClientException(String testcaseResultId, TestcaseResultService testcaseResultService, RestClientException e, ContextInfo contextInfo) throws OperationFailedException{
        try {
            TestcaseResultEntity testcaseResultById = testcaseResultService.getTestcaseResultById(testcaseResultId, contextInfo);
            testcaseResultById.setSuccess(Boolean.FALSE);

            String errorMessage = "Testbed Communication Error : " + e.getMessage();
            if (errorMessage.length() > 2000) {
                errorMessage = errorMessage.substring(0, 2000);
            }
            testcaseResultById.setMessage(errorMessage);
            testcaseResultById.setDuration(System.currentTimeMillis() - testcaseResultById.getUpdatedAt().getTime());
            testcaseResultService.updateTestcaseResult(testcaseResultById, contextInfo);
            testcaseResultService.changeState(testcaseResultById.getId(), TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_FINISHED, contextInfo);

        } catch (DoesNotExistException | InvalidParameterException | DataValidationErrorException |
                 VersionMismatchException ex) {
            LOGGER.error("OperationFailedException occurred while checking session status", e);
            throw new OperationFailedException("Operation Failed : Something Went Wrong While Updating Internal Records", e);
        }
    }



}
