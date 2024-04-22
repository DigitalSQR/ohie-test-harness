package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateSessionAndMarkTestcaseResultInProgress {


    private TestcaseResultService testcaseResultService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void process(String testcaseResultId, String sessionId , ContextInfo contextInfo) throws OperationFailedException {
        try {
            TestcaseResultEntity testcaseResultById = testcaseResultService.getTestcaseResultById(testcaseResultId, contextInfo);
            testcaseResultById.setTestSessionId(sessionId);
            testcaseResultService.updateTestcaseResult(testcaseResultById, contextInfo);
            testcaseResultService.changeState(testcaseResultId, TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS, contextInfo);
        }catch (Exception e){
            throw new OperationFailedException("Operation Failed : Something Went Wrong While Updating Internal Records",e);
        }
    }

    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }
}
