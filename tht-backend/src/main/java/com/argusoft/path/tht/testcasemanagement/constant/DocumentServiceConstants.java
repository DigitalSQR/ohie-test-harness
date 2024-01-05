package com.argusoft.path.tht.testcasemanagement.constant;

import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;
import com.argusoft.path.tht.testcasemanagement.models.dto.DocumentInfo;
import com.argusoft.path.tht.testcasemanagement.service.DocumentService;

import java.util.ArrayList;
import java.util.List;

public class DocumentServiceConstants {

    public static final String DOCUMENT_REF_OBJ_URI = DocumentInfo.class.getName();

    //DOCUMENT states
    public static final String DOCUMENT_STATUS_ACTIVE = "document.status.active";
    public static final String DOCUMENT_STATUS_INACTIVE = "document.status.inactive";

    public static List<String> documentStatuses = new ArrayList<>();

    static {
        documentStatuses.add(DOCUMENT_STATUS_ACTIVE);
        documentStatuses.add(DOCUMENT_STATUS_INACTIVE);
    }

    
}
