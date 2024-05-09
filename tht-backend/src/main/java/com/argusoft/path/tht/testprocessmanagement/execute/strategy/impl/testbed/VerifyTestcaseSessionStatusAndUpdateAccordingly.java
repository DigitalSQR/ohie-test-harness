package com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed;

import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.testbed.services.TestSessionManagementRestService;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.StatusCallback;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.impl.testbed.callback.util.EuTestBedCommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URISyntaxException;

@Component
public class VerifyTestcaseSessionStatusAndUpdateAccordingly {

    public static final Logger LOGGER = LoggerFactory.getLogger(VerifyTestcaseSessionStatusAndUpdateAccordingly.class);

    private TestSessionManagementRestService testSessionManagementRestService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void process(String testSessionId, StatusCallback statusCallback ,ContextInfo contextInfo) throws OperationFailedException, URISyntaxException{
        if(StringUtils.hasLength(testSessionId)){
            SessionChecker sessionChecker = new SessionChecker(testSessionId, testSessionManagementRestService,contextInfo);
            sessionChecker.checkSessionStatus(statusCallback);
        }else {
            throw new OperationFailedException("Operation Failed : Test Session Not Found While Checking Status of Session.");
        }
    }



    @Autowired
    public void setTestSessionManagementRestService(TestSessionManagementRestService testSessionManagementRestService) {
        this.testSessionManagementRestService = testSessionManagementRestService;
    }
}
