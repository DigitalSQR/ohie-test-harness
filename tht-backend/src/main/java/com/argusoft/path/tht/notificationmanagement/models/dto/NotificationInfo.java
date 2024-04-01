package com.argusoft.path.tht.notificationmanagement.models.dto;

import com.argusoft.path.tht.systemconfiguration.models.dto.IdStateMetaInfo;
import io.swagger.annotations.ApiModelProperty;

/**
 * This info is for Notification DTO that contains all the Notification
 * model's data.
 *
 * @author Ali
 */
public class NotificationInfo extends IdStateMetaInfo {

    @ApiModelProperty(notes = "The message for the Notification",
            allowEmptyValue = false,
            example = "message",
            dataType = "String",
            required = true)
    private String message;

    @ApiModelProperty(notes = "The testerId of the TestcaseResult",
            allowEmptyValue = false,
            example = "receiverId",
            dataType = "String",
            required = true)
    private String receiverId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
