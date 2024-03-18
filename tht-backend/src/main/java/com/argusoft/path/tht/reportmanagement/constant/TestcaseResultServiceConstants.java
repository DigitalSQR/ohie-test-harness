package com.argusoft.path.tht.reportmanagement.constant;

import com.argusoft.path.tht.reportmanagement.models.dto.TestcaseResultInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

/**
 * Constant for TestcaseResultService.
 *
 * @author Dhruv
 */
public class TestcaseResultServiceConstants {

    public static final String TESTCASE_RESULT_REF_OBJ_URI = TestcaseResultInfo.class.getName();
    //Testcase Result states
    public static final String TESTCASE_RESULT_STATUS_SKIP = "testcase.result.status.skip";
    public static final String TESTCASE_RESULT_STATUS_DRAFT = "testcase.result.status.draft";
    public static final String TESTCASE_RESULT_STATUS_PENDING = "testcase.result.status.pending";
    public static final String TESTCASE_RESULT_STATUS_INPROGRESS = "testcase.result.status.inprogress";
    public static final String TESTCASE_RESULT_STATUS_FINISHED = "testcase.result.status.finished";
    public static final String TESTCASE_RESULT_MISSING = "TestcaseResultEntity is missing";
    public static final Multimap<String, String> TESTCASE_RESULT_STATUS_MAP = ArrayListMultimap.create();
    public static final List<String> TESTCASE_RESULT_STATUS = List.of(
            TESTCASE_RESULT_STATUS_SKIP,
            TESTCASE_RESULT_STATUS_DRAFT,
            TESTCASE_RESULT_STATUS_PENDING,
            TESTCASE_RESULT_STATUS_INPROGRESS,
            TESTCASE_RESULT_STATUS_FINISHED
            );

    static {
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_DRAFT, TESTCASE_RESULT_STATUS_DRAFT);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_DRAFT, TESTCASE_RESULT_STATUS_PENDING);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_PENDING, TESTCASE_RESULT_STATUS_INPROGRESS);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_PENDING, TESTCASE_RESULT_STATUS_FINISHED);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_PENDING, TESTCASE_RESULT_STATUS_SKIP);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_PENDING, TESTCASE_RESULT_STATUS_DRAFT);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_SKIP, TESTCASE_RESULT_STATUS_PENDING);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_SKIP, TESTCASE_RESULT_STATUS_DRAFT);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_SKIP, TESTCASE_RESULT_STATUS_INPROGRESS);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_SKIP, TESTCASE_RESULT_STATUS_FINISHED);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_INPROGRESS, TESTCASE_RESULT_STATUS_FINISHED);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_INPROGRESS, TESTCASE_RESULT_STATUS_PENDING);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_INPROGRESS, TESTCASE_RESULT_STATUS_DRAFT);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_INPROGRESS, TESTCASE_RESULT_STATUS_SKIP);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_FINISHED, TESTCASE_RESULT_STATUS_DRAFT);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_FINISHED, TESTCASE_RESULT_STATUS_PENDING);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_FINISHED, TESTCASE_RESULT_STATUS_SKIP);
        TESTCASE_RESULT_STATUS_MAP.put(TESTCASE_RESULT_STATUS_FINISHED, TESTCASE_RESULT_STATUS_FINISHED);
    }

    private TestcaseResultServiceConstants() {
    }
}
