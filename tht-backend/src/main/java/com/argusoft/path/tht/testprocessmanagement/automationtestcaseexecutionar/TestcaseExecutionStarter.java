package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class TestcaseExecutionStarter {

    @Autowired
    private TestcaseExecutioner testcaseExecutioner;

    public static final Logger LOGGER = LoggerFactory.getLogger(TestcaseExecutionStarter.class);


    @Async
    @Transactional
    public void startExecution(List<TestcaseResultEntity> testcaseResultEntities,
                               Map<String, IGenericClient> iGenericClientMap,
                               ContextInfo contextInfo) {
        for (TestcaseResultEntity testcaseResult : testcaseResultEntities) {
            try {
                testcaseExecutioner.markTestcaseResultInProgress(testcaseResult.getId(), contextInfo);
                testcaseExecutioner.executeTestcase(testcaseResult.getId(), iGenericClientMap, contextInfo);
            }catch (Exception e){
                LOGGER.error("exception caught while making testcases in progress",e);
            }
        }
    }

}
