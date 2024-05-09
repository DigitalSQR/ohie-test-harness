import api from "./configs/axiosConfigs";

export const NotificationAPI = {
  getAllNotifications: function () {
    return api.request({
      url: `notification`,
      method: "GET",
      params: {
        state: "notification.status.unread",
      },
    });
  },
  updateNotificationState: function (notificationId, changeState) {
    return api.request({
      url: `notification/state/${notificationId}/${changeState}`,
      method: "PATCH",
    });
  },
  bulkUpdateNotificationState: function (oldState, newState) {
    return api.request({
      url: `notification/state/bulk/${oldState}/${newState}`,
      method: "PATCH",
    });
  },
};
