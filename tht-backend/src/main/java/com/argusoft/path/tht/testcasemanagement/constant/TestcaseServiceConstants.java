package com.argusoft.path.tht.testcasemanagement.constant;

import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<String> TESTCASE_STATUS = new ArrayList<>();

    static {
        TESTCASE_STATUS.add(TESTCASE_STATUS_DRAFT);
        TESTCASE_STATUS.add(TESTCASE_STATUS_ACTIVE);
        TESTCASE_STATUS.add(TESTCASE_STATUS_INACTIVE);
    }

    public static final Multimap<String, String> TESTCASE_STATUS_MAP = ArrayListMultimap.create();

    static {
        TESTCASE_STATUS_MAP.put(TESTCASE_STATUS_DRAFT, TESTCASE_STATUS_ACTIVE);
        TESTCASE_STATUS_MAP.put(TESTCASE_STATUS_ACTIVE, TESTCASE_STATUS_INACTIVE);
        TESTCASE_STATUS_MAP.put(TESTCASE_STATUS_INACTIVE, TESTCASE_STATUS_ACTIVE);
    }
}
