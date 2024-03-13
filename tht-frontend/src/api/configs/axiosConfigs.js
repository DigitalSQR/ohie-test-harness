import axios from "axios";
import { notification } from "antd";
import {
  refreshTokenSuccess,
  refreshTokenFailure,
} from "../../reducers/authReducer";
import { persistor, store } from "../../store/store";
const api = axios.create({
  baseURL: process.env.REACT_APP_HOST || "http://192.1.200.226:8081/api"
});
const defaultToken = `Basic dGh0OnRodC1wYXRo`;
api.defaults.headers.common["Authorization"] = defaultToken;

export const setDefaultToken = () => {
  api.defaults.headers.common["Authorization"] = defaultToken;
};

export const clearAuthInfo = () => {
  setDefaultToken();
  persistor
    .purge()
    .then(() => {
      console.log("Local storage purged successfully");
    })
    .catch((error) => {
      console.error("Error purging local storage:", error);
    });
};

// Function to set the authentication token in the request headers
export const setAuthToken = (token) => {
  if (token) {
    api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common["Authorization"];
  }
};
let isRefreshing = false;
let refreshPromise = null;

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const { config, response } = error;
    // Check if the error is due to an expired token
    if (response && response.status === 401 && response.data.error === "invalid_token") {
      if (!isRefreshing) {
        isRefreshing = true;

        // Create a promise to await the token refresh
        refreshPromise = new Promise((resolve, reject) => {
          const refresh_token = store.getState().authSlice.refresh_token; // Use the correct reducer name
          const isKeepMeLogin = store.getState().authSlice.isKeepLogin;
          if (refresh_token != null && isKeepMeLogin === true) {
            const refreshTokenModel = {
              refresh_token: refresh_token + "",
              grant_type: "refresh_token",
            };
            setDefaultToken();
            api.request({
              url: `/oauth/token`,
              method: "POST",
              data: new URLSearchParams(refreshTokenModel),
            })
              .then((response) => {
                store.dispatch(refreshTokenSuccess(response.data));
                setAuthToken(response.data.access_token);
                error.config.headers["Authorization"] = `Bearer ${response.data.access_token}`;
                resolve(response.data.access_token);
              })
              .catch((refreshError) => {
                store.dispatch(refreshTokenFailure());
                window.location.href = "/login";
                reject(refreshError);
              })
              .finally(() => {
                isRefreshing = false;
                refreshPromise = null;
              });
          } else {
            store.dispatch(refreshTokenFailure());
            window.location.href = "/login";
            reject(error);
          }
        });
      }

      return refreshPromise.then((token) => {
        config.headers["Authorization"] = `Bearer ${token}`;
        return api.request(config);
      }).catch((error) => {       
        return Promise.reject(error);
      });
    }else if(response.status > 500){
      notification.error({
        description: "Oops! Something went wrong",
        placement: "bottomRight",
      });
    }else if(response.status == 404){
      console.log(response);
      notification.error({
        description: response.data.error_description? response.data.error_description:(response.data.message ? response.data.message:response.data.error),
        placement: "bottomRight",
      });
    }else if(response.status >= 400){
      if(response.data.length && response.data.length > 0){
        response.data.forEach((error, index) => {
          notification.error({
            description: error.message ,
            placement: "bottomRight",
          });
        })        
      }else {
        notification.error({
          description: response.data.error_description? response.data.error_description:(response.data.message ? response.data.message:response.data.error),
          placement: "bottomRight",
        });
      }
      
    }

    // For other errors, reject the promise
    return Promise.reject(error);
  }
);




// defining a custom error handler for all APIs
/*const errorHandler = (error) => {
    const statusCode = error.response?.status
    console.log("error=",error);
    //if (error.code === "ERR_CANCELED") {
        notification.error({
            placement: "bottomRight",
            description: "API canceled!",
        })
        return Promise.resolve()
   // }

    // logging only errors that are not 401
    if (statusCode && statusCode !== 401) {
        console.error(error)
    }

    return Promise.reject(error)
}

// registering the custom error handler to the
// "api" axios instance
api.interceptors.response.use(undefined, (error) => {
    return errorHandler(error)
})*/
export default api;
