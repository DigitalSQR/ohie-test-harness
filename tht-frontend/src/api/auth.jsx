// api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'https://api.example.com', // Replace with your API endpoint
});

// Function to set the authentication token in the request headers
export const setAuthToken = (token) => {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common['Authorization'];
  }
};

export default api;
