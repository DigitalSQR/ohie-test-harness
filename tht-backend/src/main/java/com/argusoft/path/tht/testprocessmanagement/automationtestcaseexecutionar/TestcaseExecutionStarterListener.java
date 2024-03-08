package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TestcaseExecutionStarterListener {

    @Autowired
    private TestcaseExecutioner testcaseExecutioner;

    @Autowired
    private TestcaseResultAttributesService testcaseResultAttributesService;

    @Autowired
    private TestcaseResultService testcaseResultService;

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseExecutionStarterListener.class);

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
        Map<String, IGenericClient> iGenericClientMap = event.getiGenericClientMap();

        Thread.currentThread().setName(testRequestId + refId + refObjUri + (isWorkflow == null ? "null" : isWorkflow.toString()) + (isFunctional == null ? "null" : isFunctional.toString()) + (isRequired == null ? "null" : isRequired.toString()) + (isRecommended == null ? "null" : isRecommended.toString()));
        try {
            int totalTestcaseResults = testcaseResultEntityIds.size();

            for (int i = 0; i < totalTestcaseResults; i++) {

                if (Thread.currentThread().isInterrupted()) {
                    stopProcess(testRequestId, contextInfo, refObjUri, refId, isRequired, isRecommended, isWorkflow, isFunctional);
                    break;
                }
                testcaseExecutioner.markTestcaseResultInProgress(testcaseResultEntityIds.get(i), contextInfo);

                testcaseExecutioner.executeTestcase(testcaseResultEntityIds.get(i), iGenericClientMap, contextInfo);
                if (i == totalTestcaseResults - 1) {
                    deleteFinishedExecutionAttributes(testRequestId, contextInfo);
                }
                LOGGER.info("It has successfully executed the test case");
            }
        } catch (Exception e) {
            LOGGER.error(ValidateConstant.EXCEPTION + TestcaseExecutionStarterListener.class.getSimpleName(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
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

}
