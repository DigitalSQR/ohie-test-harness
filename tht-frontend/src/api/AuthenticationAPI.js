import api from "./configs/axiosConfigs";

export const AuthenticationAPI = {
  doLogin: async function (data) {
   
    try {
      
     
      const response = await api.request({
        url: `/oauth/token`,
        method: "POST",
        data: data,
      });
     
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
  refreshToken: async function (data) {
    try {
      const response = await api.request({
        url: `/oauth/token`,
        method: "POST",
        data: data,
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
  doLogout: async function (data) {
    try {
      const response = await api.request({
        url: `/users/logout`,
        method: "POST",
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
  viewUser: async function (userName) {
    try {
      const response = await api.request({
        url: `/users?name=${userName}`,
        method: "GET",
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
};
