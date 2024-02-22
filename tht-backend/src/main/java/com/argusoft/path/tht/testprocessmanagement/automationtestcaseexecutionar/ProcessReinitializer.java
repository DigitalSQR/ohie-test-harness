package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.InvalidParameterException;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ProcessReinitializer {

    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private TestcaseExecutioner testcaseExecutioner;

    @PostConstruct
    public void init() {
        System.out.println("===================================================");
        try {
            ContextInfo contextInfo = Constant.SUPER_USER_CONTEXT;
            List<TestcaseResultEntity> testcaseResults = searchInProgressTestcaseResults(contextInfo);
            System.out.println("===============================" + testcaseResults.size());
            Set<String> testRequestIdSet = new HashSet<>();
            for (TestcaseResultEntity testcaseResult : testcaseResults) {
                if (testRequestIdSet.contains(testcaseResult.getTestRequest().getId())) {
                    continue;
                }
                System.out.println("===============================" + testcaseResult.getTestRequest().getId());
                testRequestIdSet.add(testcaseResult.getTestRequest().getId());
                reinitInprogressTestRequest(testcaseResult.getTestRequest().getId(), contextInfo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    @Transactional
    public List<TestcaseResultEntity> searchInProgressTestcaseResults(ContextInfo contextInfo) throws Exception {
        TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
        searchFilter.setState(Arrays.asList(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS));
        searchFilter.setRefObjUri(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI);
        return testcaseResultService.searchTestcaseResults(searchFilter, contextInfo);
    }

    @Transactional
    public void reinitInprogressTestRequest(String testRequestId, ContextInfo contextInfo) throws Exception {
        testcaseExecutioner.reinitializeTestingProcess(
                testRequestId,
                TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI,
                testRequestId,
                null,
                null,
                null,
                null,
                null,
                null,
                contextInfo);
    }
}
