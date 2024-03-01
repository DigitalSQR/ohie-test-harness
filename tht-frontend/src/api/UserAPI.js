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
  changeState: async function (userId, state) {
   
      const response = await api.request({
        url: `/user/state/${userId}/${state}`,
        method: "PATCH",
      });
      return response;
   
  },
  getUserByState: async function (
    sortFieldName,
    sortDirection,
    pageNumber = 1,
    pageSize = 10,
    state,
    role
  ) {
   
      const params = {};

      if (!!sortFieldName) {
        params.sort = `${sortFieldName},${sortDirection}`;
      }

      if (pageNumber) params.page = pageNumber;
      if (pageSize) params.size = pageSize;
      if (state) params.state = state;
      if (role) params.role = role;
      const response = await api.request({
        url: `/user`,
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
