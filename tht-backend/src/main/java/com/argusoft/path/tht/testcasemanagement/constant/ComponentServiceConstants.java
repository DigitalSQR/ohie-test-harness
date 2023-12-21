package com.argusoft.path.tht.testcasemanagement.constant;

import com.argusoft.path.tht.testcasemanagement.models.dto.ComponentInfo;

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
}
