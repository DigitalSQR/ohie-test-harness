package com.argusoft.path.tht.fileservice.constant;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Constant for DocumentService.
 *
 * @author Hardik
 */

public class DocumentServiceConstants {

    public static final String DOCUMENT_REF_OBJ_URI = DocumentInfo.class.getName();
    //DOCUMENT states
    public static final String DOCUMENT_STATUS_ACTIVE = "document.status.active";
    public static final String DOCUMENT_STATUS_INACTIVE = "document.status.inactive";
    public static final Multimap<String, String> DOCUMENT_STATUS_MAP = ArrayListMultimap.create();
    // Allowed Active Types
    public static final String ALLOWED_ACTIVE_SINGLE_RECORD = "document.allowed.active.single.record";
    public static final String ALLOWED_ACTIVE_MULTI_RECORD = "document.allowed.active.multi.record";
    public static final List<String> DOCUMENT_STATUS = List.of(
            DOCUMENT_STATUS_ACTIVE,
            DOCUMENT_STATUS_INACTIVE
    );

    static {
        DOCUMENT_STATUS_MAP.put(DOCUMENT_STATUS_ACTIVE, DOCUMENT_STATUS_INACTIVE);
        DOCUMENT_STATUS_MAP.put(DOCUMENT_STATUS_INACTIVE, DOCUMENT_STATUS_ACTIVE);
    }

    private DocumentServiceConstants() {
    }

}
