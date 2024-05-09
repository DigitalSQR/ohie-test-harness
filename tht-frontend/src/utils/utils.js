// utils.js

// To Do: add a date-time format parameter as well

import { notification } from "antd";
import img_icon from "../styles/images/img.png";
import pdf_icon from "../styles/images/pdf.png";
import video_icon from "../styles/images/video-icon.png";
import moment from "moment";
import {
  ROLE_ID_ADMIN,
  ROLE_ID_ASSESSEE,
  ROLE_ID_TESTER,
} from "../constants/role_constants";
export const formatDate = (dateString) => {
  const options = { year: "numeric", month: "short", day: "numeric" };
  return new Date(dateString).toLocaleDateString(undefined, options);
};

export const paramSerialize = (params) => {
  // Use the custom paramsSerializer function here
  return Object.keys(params)
    .map((key) => {
      const value = params[key];
      if (Array.isArray(value)) {
        return `${encodeURIComponent(key)}=${value
          .map(encodeURIComponent)
          .join(`&${encodeURIComponent(key)}=`)}`;
      }
      return `${encodeURIComponent(key)}=${encodeURIComponent(value)}`;
    })
    .join("&");
};

export const handleErrorResponse = (errorResponse) => {
  if (Array.isArray(errorResponse)) {
    return errorResponse.map((error) => error.message).join(", ");
  } else if (typeof errorResponse === "object" && errorResponse !== null) {
    return errorResponse.message || "Unknown error occurred";
  } else {
    return errorResponse || "Unknown error occurred";
  }
};

export const fileTypeIcon = (fileType) => {
  switch (fileType) {
    case "application/pdf":
      return pdf_icon;
    case "image/png":
      return img_icon;
    case "image/jpeg":
      return img_icon;
    case "video/quicktime":
      return video_icon;
    case "video/mp4":
      return video_icon;
  }
};

export const stateSerializer = (input) => {
  if (typeof input === "string") {
    return input;
  } else if (Array.isArray(input)) {
    const mappedArray = input.map((value, index) => ({
      id: index + 1,
      value: value,
    }));
    const resultArray = mappedArray.map((obj) => obj.value);
    return resultArray;
  } else {
    notification.error({
      className:"notificationError",
      message: "The state is neither an array nor a string",
      placement: "bottomRight",
    });
  }
};

export const getHighestPriorityRole = (user) => {
  if (user.roleIds.includes(ROLE_ID_ADMIN)) {
    return "ADMIN";
  } else if (user.roleIds.includes(ROLE_ID_TESTER)) {
    return "TESTER";
  } else {
    return "ASSESSEE";
  }
};

export function objectToFormData(obj, formData = null, parentKey = '') {
  if (!formData) {
      formData = new FormData();
  }

  for (let key in obj) {
      if (Object.hasOwnProperty.call(obj, key)) {
          const value = obj[key];
          const finalKey = parentKey ? `${parentKey}.${key}` : key;

          if (typeof value === 'string' && moment(value, moment.ISO_8601, true).isValid()) {
              formData.append(finalKey, new Date(value));
          } else if (typeof value === 'object' && !(value instanceof File)) {
              objectToFormData(value, formData, finalKey);
          } else {
              formData.append(finalKey, value);
          }
      }
  }

  return formData;
}


