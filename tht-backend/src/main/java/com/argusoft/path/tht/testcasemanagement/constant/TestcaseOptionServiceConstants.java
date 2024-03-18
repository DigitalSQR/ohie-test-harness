package com.argusoft.path.tht.testcasemanagement.constant;

import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseOptionInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Constant for TestcaseOptionService.
 *
 * @author Dhruv
 */
public class TestcaseOptionServiceConstants {
    public static final String TESTCASE_OPTION_REF_OBJ_URI = TestcaseOptionInfo.class.getName();
    //Testcase states
    public static final String TESTCASE_OPTION_STATUS_DRAFT = "testcase.option.status.draft";
    public static final String TESTCASE_OPTION_STATUS_ACTIVE = "testcase.option.status.active";
    public static final String TESTCASE_OPTION_STATUS_INACTIVE = "testcase.option.status.inactive";
    public static final Multimap<String, String> TESTCASE_OPTION_STATUS_MAP = ArrayListMultimap.create();
    public static final List<String> TESTCASE_OPTION_STATUS = List.of(
            TESTCASE_OPTION_STATUS_DRAFT,
            TESTCASE_OPTION_STATUS_ACTIVE,
            TESTCASE_OPTION_STATUS_INACTIVE
            );

    static {
        TESTCASE_OPTION_STATUS_MAP.put(TESTCASE_OPTION_STATUS_DRAFT, TESTCASE_OPTION_STATUS_ACTIVE);
        TESTCASE_OPTION_STATUS_MAP.put(TESTCASE_OPTION_STATUS_ACTIVE, TESTCASE_OPTION_STATUS_INACTIVE);
        TESTCASE_OPTION_STATUS_MAP.put(TESTCASE_OPTION_STATUS_INACTIVE, TESTCASE_OPTION_STATUS_ACTIVE);
    }

    private TestcaseOptionServiceConstants() {
    }
}
