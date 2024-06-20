import { paramSerialize } from "../utils/utils";
import api from "./configs/axiosConfigs";

export const UserAPI = {
  viewUser: async function () {
   
      const response = await api.request({
        url: `/user/principal`,
        method: "GET",
      });
      return response.data;
   
  },
  getUsers: async function () {
   
      const response = await api.request({
        url: `/user`,
        method: "GET",
      });
      return response.data;
   
  },
  changeState: async function (userId, state, message) {
    const response = await api.request({
      url: `/user/state/${userId}/${state}`,
      method: "PATCH",
      data: {
        message: message ? message : null,
      },
    });
    return response;
  },

  getUserByFilter: async function (
    params
  ) {
      const response = await api.request({
        url: `/user/search`,
        method: "GET",
        params,
        paramsSerializer: (params) => {
          return paramSerialize(params);
        },
      });
      return response.data;
   
  },


  getUserById: async function (userId) {
   
      const response = await api.request({
        url: `/user/${userId}`,
        method: "GET",
      });
      return response.data;
   
  },
  UpdateExistingUser: async function (data) {
    
      const response = await api.request({
        url: "/user",
        method: "PUT",
        data: data,
      });
      return response;
    
  },
  resetPassword: async function (data) {
   
      const response = await api.request({
        url: "/user/reset/password",
        method: "PATCH",
        data: data,
      });
      return response;
   
  },
  getUsersbyRole: async (params) => {
    
      const response = await api.request({
        url: "/user",
        method: "GET",
        params: params,
      });
      return response.data;
   
  },
};
