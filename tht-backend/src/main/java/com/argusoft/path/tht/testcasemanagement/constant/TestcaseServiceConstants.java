package com.argusoft.path.tht.testcasemanagement.constant;

import com.argusoft.path.tht.testcasemanagement.models.dto.TestcaseInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Constant for TestcaseService.
 *
 * @author Dhruv
 */
public class TestcaseServiceConstants {
    public static final String TESTCASE_REF_OBJ_URI = TestcaseInfo.class.getName();
    public static final String PACKAGE_NAME = "com.argusoft.path.tht";
    //Testcase states
    public static final String TESTCASE_STATUS_DRAFT = "testcase.status.draft";
    public static final String TESTCASE_STATUS_ACTIVE = "testcase.status.active";
    public static final String TESTCASE_STATUS_INACTIVE = "testcase.status.inactive";


    //Testcase run environments
    public static final String TESTCASE_RUN_ENVIRONMENT_JAVA_THT = "testcase.run.environment.java.tht";
    public static final String TESTCASE_RUN_ENVIRONMENT_EU_TESTBED = "testcase.run.environment.eu.testbed";

    public static final Multimap<String, String> TESTCASE_STATUS_MAP = ArrayListMultimap.create();
    public static final List<String> TESTCASE_STATUS = List.of(
            TESTCASE_STATUS_DRAFT,
            TESTCASE_STATUS_ACTIVE,
            TESTCASE_STATUS_INACTIVE
            );

    static {
        TESTCASE_STATUS_MAP.put(TESTCASE_STATUS_DRAFT, TESTCASE_STATUS_ACTIVE);
        TESTCASE_STATUS_MAP.put(TESTCASE_STATUS_ACTIVE, TESTCASE_STATUS_INACTIVE);
        TESTCASE_STATUS_MAP.put(TESTCASE_STATUS_INACTIVE, TESTCASE_STATUS_ACTIVE);
    }

    public static final List<String> TESTCASE_RUN_ENVIRONMENTS = List.of(
            TESTCASE_RUN_ENVIRONMENT_JAVA_THT,
            TESTCASE_RUN_ENVIRONMENT_EU_TESTBED
    );

    private TestcaseServiceConstants() {
    }

    public enum QuestionType {
        SINGLE_SELECT,
        MULTI_SELECT
    }
}
