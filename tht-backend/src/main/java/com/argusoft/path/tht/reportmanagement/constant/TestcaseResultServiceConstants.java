package com.argusoft.path.tht.reportmanagement.constant;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;

/**
 * Constant for TestcaseResultService.
 *
 * @author Dhruv
 */
public class TestcaseResultServiceConstants {

    public static final String TESTCASE_RESULT_REF_OBJ_URI = TestcaseResultInfo.class.getName();

    //Testcase Result states
    public static final String TESTCASE_RESULT_STATUS_PENDING = "testcase.result.status.pending";
    public static final String TESTCASE_RESULT_STATUS_INPROGRESS = "testcase.result.status.inprogress";
    public static final String TESTCASE_RESULT_STATUS_FINISHED = "testcase.result.status.finished";
}
