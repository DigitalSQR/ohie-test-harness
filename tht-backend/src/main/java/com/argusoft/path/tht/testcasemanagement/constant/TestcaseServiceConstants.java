package com.argusoft.path.tht.testcasemanagement.constant;

import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;

/**
 * Constant for TestcaseService.
 *
 * @author Dhruv
 */
public class TestcaseServiceConstants {

    public static final String TESTCASE_REF_OBJ_URI = TestcaseInfo.class.getName();

    //Testcase states
    public static final String TESTCASE_STATUS_DRAFT = "testcase.status.draft";
    public static final String TESTCASE_STATUS_ACTIVE = "testcase.status.active";
    public static final String TESTCASE_STATUS_INACTIVE = "testcase.status.inactive";
}
