package com.argusoft.path.tht.fileservice.constant;

import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;

import java.util.Map;
import java.util.Set;

/**
 * Constant for DocumentType.
 *
 * @author Dhruv
 */

public class DocumentTypeConstants {

    public static final String DOCUMENT_TYPE_PROFILE_PICTURE = "document.type.user.profilePicture";
    public static final String DOCUMENT_TYPE_EVIDENCE = "document.type.testcaseresult.evidence";
    public static final String DOCUMENT_TYPE_QUESTION = "document.type.testcase.question";
    public static final Map<String, Set<String>> validDocumentTypes;
    public static final Set<String> docTypeForUser = Set.of(
            DOCUMENT_TYPE_PROFILE_PICTURE
    );
    public static final Set<String> docTypeForTestCaseResult = Set.of(
            DOCUMENT_TYPE_EVIDENCE
    );
    public static final Set<String> docTypeForTestCase = Set.of(
            DOCUMENT_TYPE_QUESTION
    );

    static {
        validDocumentTypes = Map.ofEntries(
                Map.entry(UserServiceConstants.USER_REF_OBJ_URI, docTypeForUser),
                Map.entry(TestcaseResultServiceConstants.TESTCASE_RESULT_REF_OBJ_URI, docTypeForTestCaseResult),
                Map.entry(TestcaseServiceConstants.TESTCASE_REF_OBJ_URI, docTypeForTestCase)
        );
    }

    private DocumentTypeConstants() {
    }

}
