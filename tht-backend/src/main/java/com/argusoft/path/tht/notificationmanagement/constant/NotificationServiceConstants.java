package com.argusoft.path.tht.notificationmanagement.constant;

import com.argusoft.path.tht.notificationmanagement.models.dto.NotificationInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

/**
 * Constant for Notification Service.
 *
 * @author Ali
 */
public class NotificationServiceConstants {

    public static final String NOTIFICATION_REF_OBJ_URI = NotificationInfo.class.getName();

    public static final String NOTIFICATION_STATUS_UNREAD = "notification.status.unread";

    public static final String NOTIFICATION_STATUS_ARCHIVED = "notification.status.archived";

    public static final List<String> NOTIFICATION_STATUS = List.of(
            NOTIFICATION_STATUS_UNREAD,
            NOTIFICATION_STATUS_ARCHIVED
    );

    public static final Multimap<String, String> NOTIFICATION_STATUS_MAP = ArrayListMultimap.create();

    static {
        NOTIFICATION_STATUS_MAP.put(NOTIFICATION_STATUS_UNREAD, NOTIFICATION_STATUS_ARCHIVED);
    }

    private NotificationServiceConstants() {
    }

}
