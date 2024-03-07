package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.filter.TestcaseResultCriteriaSearchFilter;
import com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity;
import com.argusoft.path.tht.reportmanagement.service.TestcaseResultService;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.Module;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.security.model.dto.ContextInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProcessReinitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProcessReinitializer.class);

    @Autowired
    private TestcaseResultService testcaseResultService;

    @Autowired
    private TestcaseExecutioner testcaseExecutioner;

    @PostConstruct
    public void init() {
        ContextInfo contextInfo = Constant.SUPER_USER_CONTEXT;
        contextInfo.setModule(Module.SYSTEM);
        reinitializeInProgress(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI, contextInfo);
        reinitializeInProgress(SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI, contextInfo);
        reinitializeInProgress(ComponentServiceConstants.COMPONENT_REF_OBJ_URI, contextInfo);
        reinitializeInProgress(TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI, contextInfo);
    }

    public void reinitializeInProgress(String refObjUri, ContextInfo contextInfo) {
        List<TestcaseResultEntity> testcaseResults = searchInProgressAndPendingTestcaseResults(refObjUri, contextInfo);
        for (TestcaseResultEntity testcaseResult : testcaseResults) {
            try {
                stopInprogressTestRequest(testcaseResult, contextInfo);
            } catch (Exception ex) {
                LOGGER.error(ValidateConstant.EXCEPTION + ProcessReinitializer.class.getSimpleName(), ex);
                ex.printStackTrace();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public List<TestcaseResultEntity> searchInProgressAndPendingTestcaseResults(String refObjUri, ContextInfo contextInfo) {
        try {
            TestcaseResultCriteriaSearchFilter searchFilter = new TestcaseResultCriteriaSearchFilter();
            searchFilter.setState(List.of(TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_INPROGRESS, TestcaseResultServiceConstants.TESTCASE_RESULT_STATUS_PENDING));
            searchFilter.setAutomated(true);
            searchFilter.setRefObjUri(refObjUri);
            return testcaseResultService.searchTestcaseResults(searchFilter, contextInfo);
        } catch (Exception ex) {
            LOGGER.error(ValidateConstant.EXCEPTION + ProcessReinitializer.class.getSimpleName(), ex);
            return new ArrayList<>();
        }
    }

    public void stopInprogressTestRequest(TestcaseResultEntity testcaseResult, ContextInfo contextInfo) throws Exception {
        testcaseExecutioner.stopTestingProcess(
                testcaseResult.getTestRequest().getId(),
                testcaseResult.getRefObjUri(),
                testcaseResult.getRefId(),
                null,
                true,
                null,
                null,
                null,
                null,
                Boolean.FALSE,
                contextInfo);
    }
}
