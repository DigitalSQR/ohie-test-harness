package com.argusoft.path.tht.reportmanagement.event.listener;

import com.argusoft.path.tht.reportmanagement.event.TestcaseResultStateChangedEvent;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapper;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Service
public class TestcaseResultStateChangedEventListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseResultStateChangedEventListener.class);

    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private TestcaseResultMapper testcaseResultMapper;

    @Async
    @Transactional
    @TransactionalEventListener
    public void notifyStateChangeWebSocket(TestcaseResultStateChangedEvent testcaseResultStateChangedEvent) {
        String testcaseResultId = (String) testcaseResultStateChangedEvent.getSource();
        try {
            notifyTestCaseFinished(testcaseResultId, testcaseResultStateChangedEvent.getManual(), testcaseResultStateChangedEvent.getContextInfo());
        } catch (InvalidParameterException | OperationFailedException | DoesNotExistException e) {
            LOGGER.error("Caught Exception while notifying State Change info to web socket ", e);
        }

    }

    private void notifyTestCaseFinished(String testcaseResultEntityId, Boolean isManual, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DoesNotExistException {
        String destination = "/testcase-result/" + (Objects.equals(isManual, Boolean.TRUE) ? "manual/" : "automated/") + testcaseResultEntityId;
        TestcaseResultEntity testcaseResultEntity = testcaseResultService.getTestcaseResultStatus(testcaseResultEntityId,
                Objects.equals(isManual, Boolean.TRUE) ? true : null,
                Objects.equals(isManual, Boolean.TRUE) ? null : true,
                null,
                null,
                null,
                null,
                contextInfo
        );
        simpMessagingTemplate.convertAndSend(destination, testcaseResultMapper.modelToDto(testcaseResultEntity));
        if (testcaseResultEntity.getParentTestcaseResult() != null) {
            notifyTestCaseFinished(testcaseResultEntity.getParentTestcaseResult().getId(), isManual, contextInfo);
        }
    }

}
