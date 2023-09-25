// api.js
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

export default api;
