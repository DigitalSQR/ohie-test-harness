package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.event.listener;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultAttributesService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.event.TestcaseExecutionStartEvent;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.event.stopper.AutomationTestingProcessStopper;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.util.TestcaseExecutioner;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.ExecutionStrategy;
import com.argusoft.path.tht.testprocessmanagement.execute.strategy.factory.ExecutionStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TestcaseExecutionerStarterListener
 *
 * @author Bhavi
 */

@Service
public class TestcaseExecutionStarterListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseExecutionStarterListener.class);
    private TestcaseExecutioner testcaseExecutioner;
    private TestcaseResultAttributesService testcaseResultAttributesService;
    private TestcaseResultService testcaseResultService;
    private TestcaseService testcaseService;
    private ExecutionStrategyFactory executionStrategyFactory;

    @Autowired
    private AutomationTestingProcessStopper automationTestingProcessStopper;

    @Autowired
    public void setTestcaseExecutioner(TestcaseExecutioner testcaseExecutioner) {
        this.testcaseExecutioner = testcaseExecutioner;
    }

    @Autowired
    public void setTestcaseResultAttributesService(TestcaseResultAttributesService testcaseResultAttributesService) {
        this.testcaseResultAttributesService = testcaseResultAttributesService;
    }

    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }

    @Autowired
    public void setExecutionStrategyFactory(ExecutionStrategyFactory executionStrategyFactory) {
        this.executionStrategyFactory = executionStrategyFactory;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    @TransactionalEventListener
    public void startExecution(TestcaseExecutionStartEvent event) {
        String testRequestId = (String) event.getSource();
        String refId = event.getRefId();
        String refObjUri = event.getRefObjUri();
        Boolean isWorkflow = event.getWorkflow();
        Boolean isFunctional = event.getFunctional();
        List<String> testcaseResultEntityIds = event.getTestcaseResultEntityIds();
        ContextInfo contextInfo = event.getContextInfo();
        Boolean isRequired = event.getRequired();
        Boolean isRecommended = event.getRecommended();

        Thread.currentThread().setName(testRequestId + refId + refObjUri + (isWorkflow == null ? "null" : isWorkflow.toString()) + (isFunctional == null ? "null" : isFunctional.toString()) + (isRequired == null ? "null" : isRequired.toString()) + (isRecommended == null ? "null" : isRecommended.toString()));

            int totalTestcaseResults = testcaseResultEntityIds.size();
        try {
                for (int i = 0; i < totalTestcaseResults; i++) {

                        if (Thread.currentThread().isInterrupted()) {
                            automationTestingProcessStopper.stopProcess(testRequestId, contextInfo, refObjUri, refId, isRequired, isRecommended, isWorkflow, isFunctional);
                            break;
                        }

                        ExecutionStrategy executionStrategy = executionStrategyFactory.createExecutionStrategyBasedOnTestcaseResultId(testcaseResultEntityIds.get(i), contextInfo);
                        executionStrategy.execute();

                        if (i == totalTestcaseResults - 1) {
                            deleteFinishedExecutionAttributes(testRequestId, contextInfo);
                        }
                        LOGGER.info("It has successfully executed the test case");

                }
        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + TestcaseExecutionStarterListener.class.getSimpleName(), e);
        }

    }

    private void deleteFinishedExecutionAttributes(String testRequestId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DoesNotExistException {
        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
        testcaseResultCriteriaSearchFilter.setRefId(testRequestId);
        List<TestcaseResultEntity> testcaseResultEntity = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter, contextInfo);
        testcaseResultAttributesService.deleteTestcaseResultAttributesEntities(testcaseResultEntity.get(0), contextInfo);
        LOGGER.info("Finished test case request result attribute deleted");
    }

}
