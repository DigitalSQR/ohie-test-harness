package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.event.stopper;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultAttributesService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.event.listener.TestcaseExecutionStarterListener;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.util.TestcaseExecutioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class AutomationTestingProcessStopper {

    public static final Logger LOGGER = LoggerFactory.getLogger(AutomationTestingProcessStopper.class);

    private TestcaseResultService testcaseResultService;
    private TestcaseResultAttributesService testcaseResultAttributesService;

    private TestcaseExecutioner testcaseExecutioner;


    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void stopProcess(String testRequestId, ContextInfo contextInfo, String refObjUri, String refId, Boolean isRequired, Boolean isRecommended, Boolean isWorkflow, Boolean isFunctional) throws OperationFailedException, InvalidParameterException, DoesNotExistException, DataValidationErrorException, VersionMismatchException {
        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
        testcaseResultCriteriaSearchFilter.setRefId(testRequestId);
        List<TestcaseResultEntity> testcaseResultEntity = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter, contextInfo);
        if (testcaseResultEntity.isEmpty()) {

            LOGGER.error("TestcaseResultEntity list is empty");
            throw new DoesNotExistException("testcaseResultEntity list is empty");

        }

        TestcaseResultEntity testcaseResultTestRequestEntity = testcaseResultEntity.get(0);
        Optional<TestcaseResultAttributesEntity> testcaseResultAttributesEntity = testcaseResultAttributesService.getTestcaseResultAttributes(testcaseResultTestRequestEntity, "reset", contextInfo);

        List<TestcaseResultEntity> results = testcaseExecutioner.fetchTestcaseResultsByInputsForReinitialize(testRequestId,
                refObjUri,
                refId,
                null,
                true,
                isRequired,
                isRecommended,
                isWorkflow,
                isFunctional,
                contextInfo);

        testcaseExecutioner.changeTestcaseResultsState(
                results,
                TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_DRAFT,
                testcaseResultAttributesEntity.isPresent() && testcaseResultAttributesEntity.get().getValue().equals("true"),
                contextInfo);
        if (testcaseResultAttributesEntity.isPresent() && testcaseResultAttributesEntity.get().getValue().equals("true")) {
            deleteFinishedExecutionAttributes(testRequestId, contextInfo);
        }
    }

    private void deleteFinishedExecutionAttributes(String testRequestId, ContextInfo contextInfo) throws InvalidParameterException, OperationFailedException, DoesNotExistException {
        TestcaseResultCriteriaSearchFilter testcaseResultCriteriaSearchFilter = new TestcaseResultCriteriaSearchFilter();
        testcaseResultCriteriaSearchFilter.setRefId(testRequestId);
        List<TestcaseResultEntity> testcaseResultEntity = testcaseResultService.searchTestcaseResults(testcaseResultCriteriaSearchFilter, contextInfo);
        testcaseResultAttributesService.deleteTestcaseResultAttributesEntities(testcaseResultEntity.get(0), contextInfo);
        LOGGER.info("Finished test case request result attribute deleted");
    }

    @Autowired
    public void setTestcaseResultService(TestcaseResultService testcaseResultService) {
        this.testcaseResultService = testcaseResultService;
    }

    @Autowired
    public void setTestcaseResultAttributesService(TestcaseResultAttributesService testcaseResultAttributesService) {
        this.testcaseResultAttributesService = testcaseResultAttributesService;
    }

    @Autowired
    public void setTestcaseExecutioner(TestcaseExecutioner testcaseExecutioner) {
        this.testcaseExecutioner = testcaseExecutioner;
    }
}
