package com.argusoft.path.tht.fileservice.constant;

import java.util.Set;

/**
 * @author Hardik
 */

public enum UserEntityDocumentTypes implements EntityDocumentTypeEnum {
    USER_PROFILE_PICTURE("document.type.user.profilePicture", Set.of(FileType.IMAGE_JPEG, FileType.IMAGE_PNG), DocumentServiceConstants.ALLOWED_ACTIVE_SINGLE_RECORD);

    private final String key;

    private final Set<FileType> allowedFileTypes;

    private final String allowedActiveType;

    UserEntityDocumentTypes(String key, Set<FileType> allowedFileTypes, String allowedActiveType) {
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
        return this.allowedActiveType;
    }

}
