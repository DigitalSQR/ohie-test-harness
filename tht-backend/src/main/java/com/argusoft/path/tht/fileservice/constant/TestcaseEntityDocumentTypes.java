package com.argusoft.path.tht.fileservice.constant;

import java.util.Set;

/**
 *
 * @author Hardik
 */

public enum TestcaseEntityDocumentTypes implements EntityDocumentTypeEnum {

    TESTCASE_REFERENCE_IMAGE("document.type.testcase.question", Set.of(FileType.IMAGE_JPEG, FileType.IMAGE_PNG), DocumentServiceConstants.ALLOWED_ACTIVE_MULTI_RECORD);

    private final String key;

    private final Set<FileType> allowedFileTypes;

    private final String allowedActiveType;

    TestcaseEntityDocumentTypes(String key, Set<FileType> allowedFileTypes, String allowedActiveType) {
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
