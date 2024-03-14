package com.argusoft.path.tht.fileservice.constant;

import java.util.Set;

/**
 * EntityDocumentTypeEnum
 *
 * @author Hardik
 */

public interface EntityDocumentTypeEnum {

    public Set<FileType> getAllowedFileTypes();

    public String getKey();

    public String getAllowedActiveType();

}
