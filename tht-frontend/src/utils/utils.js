// utils.js

// To Do: add a date-time format parameter as well
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