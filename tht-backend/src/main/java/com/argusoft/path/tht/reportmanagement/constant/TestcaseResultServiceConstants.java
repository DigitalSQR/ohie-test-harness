package com.argusoft.path.tht.reportmanagement.constant;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;

public class TestcaseResultServiceConstants {

    public static final String TESTCASE_RESULT_REF_OBJ_URI = TestcaseResultInfo.class.getName();

    //Testcase Result states
    public static final String TESTCASE_RESULT_STATUS_PENDING = "testcase.result.status.draft";
    public static final String TESTCASE_RESULT_STATUS_INPROGRESS = "testcase.result.status.inprogress";
    public static final String TESTCASE_RESULT_STATUS_PASSED = "testcase.result.status.passed";
    public static final String TESTCASE_RESULT_STATUS_FAILED = "testcase.result.status.failed";
}
