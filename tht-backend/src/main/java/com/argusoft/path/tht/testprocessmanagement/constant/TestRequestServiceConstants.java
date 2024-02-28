package com.argusoft.path.tht.testprocessmanagement.constant;

import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Constant for TestRequestService.
 *
 * @author Dhruv
 */
public class TestRequestServiceConstants {

    public static final String TEST_REQUEST_REF_OBJ_URI = TestRequestInfo.class.getName();

    //TestRequest states
    public static final String TEST_REQUEST_STATUS_PENDING = "test.request.status.pending";
    public static final String TEST_REQUEST_STATUS_ACCEPTED = "test.request.status.accepted";
    public static final String TEST_REQUEST_STATUS_REJECTED = "test.request.status.rejected";
    public static final String TEST_REQUEST_STATUS_INPROGRESS = "test.request.status.inprogress";
    public static final String TEST_REQUEST_STATUS_FINISHED = "test.request.status.finished";
    public static final String TEST_REQUEST_STATUS_SKIPPED = "test.request.status.skipped";
    public static final Multimap<String, String> TEST_REQUEST_STATUS_MAP = ArrayListMultimap.create();
    public static List<String> TEST_REQUEST_STATUS = new ArrayList<>();

    static {
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_PENDING);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_ACCEPTED);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_REJECTED);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_INPROGRESS);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_FINISHED);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_SKIPPED);
    }

    static {
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_PENDING, TEST_REQUEST_STATUS_ACCEPTED);
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_PENDING, TEST_REQUEST_STATUS_REJECTED);
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_ACCEPTED, TEST_REQUEST_STATUS_INPROGRESS);
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_INPROGRESS, TEST_REQUEST_STATUS_ACCEPTED);
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_ACCEPTED, TEST_REQUEST_STATUS_FINISHED);
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_ACCEPTED, TEST_REQUEST_STATUS_SKIPPED);
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_INPROGRESS, TEST_REQUEST_STATUS_FINISHED);
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_SKIPPED, TEST_REQUEST_STATUS_ACCEPTED);
        TEST_REQUEST_STATUS_MAP.put(TEST_REQUEST_STATUS_FINISHED, TEST_REQUEST_STATUS_ACCEPTED);
    }

}
