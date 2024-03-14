package com.argusoft.path.tht.reportmanagement.event.listener;

import com.argusoft.path.tht.reportmanagement.event.TestcaseResultAttributeEvent;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapper;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.DoesNotExistException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class TestcaseResultAttributeEventListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseResultAttributeEventListener.class);

    private TestcaseResultService testcaseResultService;
    private SimpMessagingTemplate messagingTemplate;
    private TestcaseResultMapper testcaseResultMapper;

    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }

    @Autowired
    public void setMessagingTemplate(SimpMessagingTemplate simpMessagingTemplate) {
        this.messagingTemplate = simpMessagingTemplate;
    }

    @Autowired
    public void setTestcaseResultMapper(TestcaseResultMapper testcaseResultMapper) {
        this.testcaseResultMapper = testcaseResultMapper;
    }

    @Async
    @TransactionalEventListener
    public void notifyAttributeChange(TestcaseResultAttributeEvent event) {
        String testcaseResultId = (String) event.getSource();
        String destination = "/testcase-result/attribute/" + testcaseResultId;
        TestcaseResultEntity testcaseResultEntity = null;
        try {
            testcaseResultEntity = testcaseResultService.getTestcaseResultById(testcaseResultId, event.getContextInfo());
        } catch (DoesNotExistException | InvalidParameterException e) {
            LOGGER.error("Caught exception while notifying attribute change ", e);
        }
        messagingTemplate.convertAndSend(destination, testcaseResultMapper.modelToDto(testcaseResultEntity));
    }

}
