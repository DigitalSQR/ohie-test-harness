// utils.js

// To Do: add a date-time format parameter as well

import img_icon from "../styles/images/img.png";
import pdf_icon from "../styles/images/pdf.png";
export const formatDate = (dateString) => {
    const options = { year: "numeric", month: "short", day: "numeric" }
    return new Date(dateString).toLocaleDateString(undefined, options)
}

export const paramSerialize = (params) => {
    // Use the custom paramsSerializer function here
    return Object.keys(params)
      .map(key => {
        const value = params[key];
        if (Array.isArray(value)) {
          return `${encodeURIComponent(key)}=${value.map(encodeURIComponent).join(`&${encodeURIComponent(key)}=`)}`;
        }
        return `${encodeURIComponent(key)}=${encodeURIComponent(value)}`;
      })
      .join('&');
};


export const handleErrorResponse = (errorResponse) => {
  if (Array.isArray(errorResponse)) {
    return errorResponse.map((error) => error.message).join(', ');
  } else if (typeof errorResponse === 'object' && errorResponse !== null) {
    return errorResponse.message || 'Unknown error occurred';
  } else {
    return errorResponse || 'Unknown error occurred';
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
    }
  }
