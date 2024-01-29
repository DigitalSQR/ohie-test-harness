package com.argusoft.path.tht.testcasemanagement.constant;

import com.argusoft.path.tht.testcasemanagement.models.dto.SpecificationInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Constant for SpecificationService.
 *
 * @author Dhruv
 */
public class SpecificationServiceConstants {

    public static final String SPECIFICATION_REF_OBJ_URI = SpecificationInfo.class.getName();

    //Specification states
    public static final String SPECIFICATION_STATUS_DRAFT = "specification.status.draft";
    public static final String SPECIFICATION_STATUS_ACTIVE = "specification.status.active";
    public static final String SPECIFICATION_STATUS_INACTIVE = "specification.status.inactive";
    public static final Multimap<String, String> SPECIFICATION_STATUS_MAP = ArrayListMultimap.create();
    public static List<String> SPECIFICATION_STATUS = new ArrayList<>();

    static {
        SPECIFICATION_STATUS.add(SPECIFICATION_STATUS_ACTIVE);
        SPECIFICATION_STATUS.add(SPECIFICATION_STATUS_INACTIVE);
        SPECIFICATION_STATUS.add(SPECIFICATION_STATUS_DRAFT);
    }

    static {
        SPECIFICATION_STATUS_MAP.put(SPECIFICATION_STATUS_DRAFT, SPECIFICATION_STATUS_ACTIVE);
        SPECIFICATION_STATUS_MAP.put(SPECIFICATION_STATUS_ACTIVE, SPECIFICATION_STATUS_INACTIVE);
        SPECIFICATION_STATUS_MAP.put(SPECIFICATION_STATUS_INACTIVE, SPECIFICATION_STATUS_ACTIVE);
    }
}
