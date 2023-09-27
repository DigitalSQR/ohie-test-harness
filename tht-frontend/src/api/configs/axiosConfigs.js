import axios from 'axios';


const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Replace with your API endpoint
});
api.defaults.headers.common['Authorization'] = `Basic dGh0OjZhYzJjN2Y2LTkwMzItNGQzNi04MzFmLTJjYzNhN2ZhOTEwYw==`;

// Function to set the authentication token in the request headers
export const setAuthToken = (token) => {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common['Authorization'];
  }
};
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
