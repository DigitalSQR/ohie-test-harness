package com.argusoft.path.tht.testcasemanagement.constant;

import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Constant for ComponentService.
 *
 * @author Dhruv
 */
public class ComponentServiceConstants {

    public static final String COMPONENT_REF_OBJ_URI = ComponentInfo.class.getName();

    //Component states
    public static final String COMPONENT_STATUS_DRAFT = "component.status.draft";
    public static final String COMPONENT_STATUS_ACTIVE = "component.status.active";

    public static final String COMPONENT_STATUS_INACTIVE = "component.status.inactive";

    //Component Ids
    public static final String COMPONENT_CLIENT_REGISTRY_ID = "component.client.registry";
    public static final String COMPONENT_FACILITY_REGISTRY_ID = "component.facility.registry";

    public static final String COMPONENT_SHARED_HEALTH_RECORD_REGISTRY_ID = "component.shared.health.record.registry";
    public static final String COMPONENT_TERMINOLOGY_SERVICE_ID = "component.terminology.service";
    public static final String COMPONENT_HEALTH_WORKER_REGISTRY_ID = "component.health.worker.registry";
    public static final Multimap<String, String> COMPONENT_STATUS_MAP = ArrayListMultimap.create();
    public static List<String> COMPONENT_STATUS = new ArrayList<>();

    static {
        COMPONENT_STATUS.add(COMPONENT_STATUS_INACTIVE);
        COMPONENT_STATUS.add(COMPONENT_STATUS_ACTIVE);
        COMPONENT_STATUS.add(COMPONENT_STATUS_DRAFT);
    }

    static {
        COMPONENT_STATUS_MAP.put(COMPONENT_STATUS_DRAFT, COMPONENT_STATUS_ACTIVE);
        COMPONENT_STATUS_MAP.put(COMPONENT_STATUS_ACTIVE, COMPONENT_STATUS_INACTIVE);
        COMPONENT_STATUS_MAP.put(COMPONENT_STATUS_INACTIVE, COMPONENT_STATUS_ACTIVE);
    }

}
