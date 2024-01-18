package com.argusoft.path.tht.testprocessmanagement.constant;

import com.argusoft.path.tht.testprocessmanagement.models.dto.TestRequestInfo;

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
    public static final String TEST_REQUEST_STATUS_PENDING = "component.status.pending";
    public static final String TEST_REQUEST_STATUS_ACCEPTED = "component.status.accepted";
    public static final String TEST_REQUEST_STATUS_REJECTED = "component.status.rejected";
    public static final String TEST_REQUEST_STATUS_INPROGRESS = "component.status.inprogress";
    public static final String TEST_REQUEST_STATUS_FINISHED = "component.status.finished";
    public static final String TEST_REQUEST_STATUS_SKIPPED = "component.status.skipped";
    public static List<String> TEST_REQUEST_STATUS = new ArrayList<>();

    static {
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_PENDING);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_ACCEPTED);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_REJECTED);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_INPROGRESS);
        TEST_REQUEST_STATUS.add(TEST_REQUEST_STATUS_FINISHED);
    }
}
