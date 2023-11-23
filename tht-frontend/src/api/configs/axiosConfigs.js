import axios from "axios";
import {
  refreshTokenSuccess,
  refreshTokenFailure,
} from "../../reducers/authReducer";
import { persistor, store } from '../../store/store';
const api = axios.create({
  baseURL: "http://localhost:8080/api", // Replace with your API endpoint
});
const defaultToken = `Basic dGh0OjZhYzJjN2Y2LTkwMzItNGQzNi04MzFmLTJjYzNhN2ZhOTEwYw==`;
api.defaults.headers.common["Authorization"] = defaultToken;

export const setDefaultToken = () => {
  api.defaults.headers.common["Authorization"] = defaultToken;
}

export const clearAuthInfo = () => {
  setDefaultToken();
  persistor.purge()
  .then(() => {
    console.log('Local storage purged successfully');
  })
  .catch(error => {
    console.error('Error purging local storage:', error);
  });
}

// Function to set the authentication token in the request headers
export const setAuthToken = (token) => {
  if (token) {
    api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common["Authorization"];
  }
};

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response.status === 401 && error.response.data.error === 'invalid_token') {
      error.config._retry = true;
      try {
        const refresh_token = store.getState().authSlice.refresh_token; // Use the correct reducer name
        const isKeepMeLogin = store.getState().authSlice.isKeepLogin;
        if (refresh_token != null && isKeepMeLogin === true) {
          const refreshTokenModel = {
            refresh_token: refresh_token+'',
            grant_type: "refresh_token", 
          };
          setDefaultToken();
          const response = await api.request({
            url: `/oauth/token`,
            method: "POST",
            data: new URLSearchParams(refreshTokenModel),
          });
          store.dispatch(refreshTokenSuccess(response.data));
          setAuthToken(response.data.access_token);
          error.config.headers['Authorization'] = `Bearer ${response.data.access_token}`;
          return api.request(error.config);          
        } else {
          store.dispatch(refreshTokenFailure());
          return Promise.reject(error);
       //   return Promise.reject(error);
        }
      } catch (refreshError) {
        store.dispatch(refreshTokenFailure());
        return Promise.reject(error);
      //  return Promise.reject(error);
      }
    }

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
