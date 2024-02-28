package com.argusoft.path.tht.reportmanagement.service.impl;

import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapper;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestcaseresultCallbackService {

    @Autowired
    private SimpMessagingTemplate msgTemplate;

    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private TestcaseResultMapper testcaseResultMapper;

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseresultCallbackService.class);

    @Async
    @Transactional
    public void notifyTestCaseFinished(String testcaseResultEntityId, Boolean isAutomated, Boolean isManual, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DoesNotExistException {
        TestcaseResultEntity testcaseResultEntity = testcaseResultService.getTestcaseResultById(testcaseResultEntityId, contextInfo);
        String destination = "/testcase-result/" + testcaseResultEntity.getId();
        testcaseResultEntity = testcaseResultService.fetchTestcaseResultStatusByInputs(
                isManual,
                isAutomated,
                null,
                null,
                null,
                null,
                testcaseResultEntity.getId(),
                contextInfo
        );
        msgTemplate.convertAndSend(destination, testcaseResultMapper.modelToDto(testcaseResultEntity));
        if (testcaseResultEntity.getParentTestcaseResult() != null) {
            notifyTestCaseFinished(testcaseResultEntity.getParentTestcaseResult().getId(), isAutomated, isManual, contextInfo);
        }
    }
}
