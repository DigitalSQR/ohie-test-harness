package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.*;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.facilityregistry.FRF9TestCase1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Service
public class TestcaseExecutionStarter {

    @Autowired
    private TestcaseExecutioner testcaseExecutioner;

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseExecutionStarter.class);

    private CountDownLatch executionCompletionLatch = new CountDownLatch(1);

        @Async
        @Transactional
        public void startExecution(List<TestcaseResultEntity> testcaseResultEntities,
                                   Map<String, IGenericClient> iGenericClientMap,
                                   String refId,
                                   String refObjUri,
                                   ContextInfo contextInfo) throws InvalidParameterException, DoesNotExistException, DataValidationErrorException, OperationFailedException, VersionMismatchException {
            Thread.currentThread().setName(refId + refObjUri + Thread.currentThread().getName());

            try {
                for (TestcaseResultEntity testcaseResult : testcaseResultEntities) {

                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    testcaseExecutioner.markTestcaseResultInProgress(testcaseResult.getId(), contextInfo);
                    testcaseExecutioner.executeTestcase(testcaseResult.getId(), iGenericClientMap, contextInfo);
                    LOGGER.info("It has successfully executed the test case");
                }
                if (Thread.currentThread().isInterrupted()) {
                    LOGGER.info("Interrupt occurred will stopping execution");
                    testcaseExecutioner.changeTestcaseResultsState(testcaseResultEntities, "testcase.result.status.draft", contextInfo);
                }
            } catch (Exception e) {
                LOGGER.error("exception caught while making testcases in progress", e);
            }
        }

}
