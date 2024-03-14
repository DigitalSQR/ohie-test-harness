package com.argusoft.path.tht.fileservice.constant;

import java.util.Set;

/**
 * @author Hardik
 */

public enum TestcaseResultEntityDocumentTypes implements EntityDocumentTypeEnum {

    TESTCASE_RESULT_EVIDENCE("document.type.testcaseresult.evidence", Set.of(FileType.IMAGE_JPEG, FileType.IMAGE_PNG, FileType.APPLICATION_PDF), DocumentServiceConstants.ALLOWED_ACTIVE_MULTI_RECORD);

    private final String key;

    private final Set<FileType> allowedFileTypes;

    private final String allowedActiveType;

    TestcaseResultEntityDocumentTypes(String key, Set<FileType> allowedFileTypes, String allowedActiveType) {
        this.key = key;
        this.allowedFileTypes = allowedFileTypes;
        this.allowedActiveType = allowedActiveType;
    }

    @Override
    public Set<FileType> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getAllowedActiveType() {
        return allowedActiveType;
    }
}
