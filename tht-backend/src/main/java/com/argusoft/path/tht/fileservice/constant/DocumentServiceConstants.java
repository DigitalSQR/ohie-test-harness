package com.argusoft.path.tht.fileservice.constant;

import com.argusoft.path.tht.fileservice.models.dto.DocumentInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentServiceConstants {

    public static final String DOCUMENT_REF_OBJ_URI = DocumentInfo.class.getName();

    //DOCUMENT states
    public static final String DOCUMENT_STATUS_ACTIVE = "document.status.active";
    public static final String DOCUMENT_STATUS_INACTIVE = "document.status.inactive";

    public static List<String> DOCUMENT_STATUS = new ArrayList<>();

    static {
        DOCUMENT_STATUS.add(DOCUMENT_STATUS_ACTIVE);
        DOCUMENT_STATUS.add(DOCUMENT_STATUS_INACTIVE);
    }

    public static final Multimap<String, String> DOCUMENT_STATUS_MAP = ArrayListMultimap.create();


    static {
        DOCUMENT_STATUS_MAP.put(DOCUMENT_STATUS_ACTIVE, DOCUMENT_STATUS_INACTIVE);
        DOCUMENT_STATUS_MAP.put(DOCUMENT_STATUS_INACTIVE, DOCUMENT_STATUS_ACTIVE);
    }

}
