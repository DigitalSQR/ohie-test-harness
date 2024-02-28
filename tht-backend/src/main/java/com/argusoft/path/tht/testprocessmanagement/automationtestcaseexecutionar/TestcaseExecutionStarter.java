package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultAttributesEntity;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapper;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultAttributesService;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TestcaseExecutionStarter {

    @Autowired
    private TestcaseExecutioner testcaseExecutioner;

    @Autowired
    private TestcaseResultAttributesService testcaseResultAttributesService;

    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private TestcaseResultMapper testcaseResultMapper;

    @Autowired
    SimpMessagingTemplate msgTemplate;

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseExecutionStarter.class);


    @Async
    @Transactional
    public void startExecution(List<TestcaseResultEntity> testcaseResultEntities,
                               String refId,
                               String refObjUri,
                               String testRequestId,
                               Boolean isWorkflow,
                               Boolean isFunctional,
                               Boolean isRequired,
                               Boolean isRecommended,
                               Map<String, IGenericClient> iGenericClientMap,
                               ContextInfo contextInfo) {
        Thread.currentThread().setName(testRequestId + refId + refObjUri + (isWorkflow == null ? "null" : isWorkflow.toString()) + (isFunctional == null ? "null" : isFunctional.toString()) + (isRequired == null ? "null" : isRequired.toString()) + (isRecommended == null ? "null" : isRecommended.toString()));
        try {
            int totalTestcaseResults = testcaseResultEntities.size();

            for (int i = 0; i < totalTestcaseResults; i++) {

                if (Thread.currentThread().isInterrupted()) {

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
                    break;
                }
                testcaseExecutioner.markTestcaseResultInProgress(testcaseResultEntities.get(i).getId(), contextInfo);

                testcaseExecutioner.executeTestcase(testcaseResultEntities.get(i).getId(), iGenericClientMap, contextInfo);
                if (i == totalTestcaseResults - 1) {
                    deleteFinishedExecutionAttributes(testRequestId, contextInfo);
                }

                LOGGER.info("It has successfully executed the test case");
            }
        } catch (Exception e) {
            LOGGER.error("exception caught while stopping test progress", e);
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
