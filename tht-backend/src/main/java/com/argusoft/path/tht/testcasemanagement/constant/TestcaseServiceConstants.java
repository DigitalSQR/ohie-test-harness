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

    //Testcase bulk import
    public static final String SHEET_COLUMN_REFERENCE = "REFERENCE";
    public static final String SHEET_COLUMN_REFERENCE_TYPE_VALUE_COMPONENT = "COMPONENT";
    public static final String SHEET_COLUMN_REFERENCE_TYPE_VALUE_SPECIFICATION = "SPECIFICATION";
    public static final String SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE = "TESTCASE";
    public static final String SHEET_COLUMN_REFERENCE_TYPE_VALUE_TESTCASE_OPTION = "TESTCASE_OPTION";
    public static final String SHEET_COLUMN_REFERENCE_IDENTIFIER = "REFERENCE_IDENTIFIER";
    public static final String SHEET_COLUMN_REFERENCE_TYPE = "REFERENCE_TYPE";
    public static final String SHEET_COLUMN_REQUIRED_RECOMMENDED = "REQUIRED/RECOMMENDED";
    public static final String SHEET_COLUMN_REQUIRED_RECOMMENDED_VALUE_REQUIRED = "REQUIRED";
    public static final String SHEET_COLUMN_REQUIRED_RECOMMENDED_VALUE_RECOMMENDED = "RECOMMENDED";
    public static final String SHEET_COLUMN_FUNCTIONAL_WORKFLOW = "FUNCTIONAL/WORKFLOW";
    public static final String SHEET_COLUMN_FUNCTIONAL_WORKFLOW_VALUE_FUNCTIONAL = "FUNCTIONAL";
    public static final String SHEET_COLUMN_FUNCTIONAL_WORKFLOW_VALUE_WORKFLOW = "WORKFLOW";
    public static final String SHEET_COLUMN_COMPONENT_NAME = "COMPONENT_NAME";
    public static final String SHEET_COLUMN_SPECIFICATION_NAME = "SPECIFICATION_NAME";
    public static final String SHEET_COLUMN_TESTCASE_NAME = "TESTCASE_NAME";
    public static final String SHEET_COLUMN_TESTCASE_OPTION_NAME = "TESTCASE_OPTION_NAME";
    public static final String SHEET_COLUMN_TESTCASE_DESCRIPTION = "DESCRIPTION";
    public static final String SHEET_COLUMN_IS_CORRECT_ANSWER = "IS_CORRECT_ANSWER";
    public static final String SHEET_COLUMN_COMPONENT_DESCRIPTION = "COMPONENT_STATUS";
    public static final String SHEET_COLUMN_SPECIFICATION_DESCRIPTION = "SPECIFICATION_STATUS";
    public static final String SHEET_COLUMN_TESTCASE_STATUS = "TESTCASE_STATUS";
    public static final String SHEET_COLUMN_SINGLE_SELECT_MULTI_SELECT= "SINGLE_SELECT/MULTI_SELECT";
    public static final String SHEET_COLUMN_FAILURE_MESSAGE= "FAILURE_MESSAGE";
    public static final String SHEET_COLUMN_TESTCASE_OPTION_STATUS = "TESTCASE_OPTION_STATUS";
}
