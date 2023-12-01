package com.argusoft.path.tht.reportmanagement.constant;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;

public class TestcaseResultServiceConstants {

    public static final String TESTCASE_RESULT_REF_OBJ_URI = TestcaseResultInfo.class.getName();

    //Component states
    public static final String TESTCASE_RESULT_STATUS_PENDING = "component.status.draft";
    public static final String TESTCASE_RESULT_STATUS_INPROGRESS = "component.status.active";
    public static final String TESTCASE_RESULT_STATUS_PASSED = "component.status.passed";
    public static final String TESTCASE_RESULT_STATUS_FAILED = "component.status.failed";
}
