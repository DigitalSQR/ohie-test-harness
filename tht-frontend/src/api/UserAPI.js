import api from "./configs/axiosConfigs";

export const UserAPI = {
  viewUser: async function () {
    try {
      const response = await api.request({
        url: `/user/principal`,
        method: "GET",
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
  getUsers: async function () {
    try {
      const response = await api.request({
        url: `/user`,
        method: "GET",
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
  changeState: async function (userId, state) {
    console.log("userapi " + userId, state);
    try {
      const response = await api.request({
        url: `/user/state/${userId}/${state}`,
        method: "PUT",
      });
      return response;
    } catch (error) {
      throw error;
    }
  },
  getUserByState: async function (
    sortFieldName = "createdAt",
    sortDirection = "desc",
    pageNumber = 1,
    pageSize = 10
  ) {
    try {
      console.log(sortFieldName, sortDirection);
      const response = await api.request({
        url: `/user`,
        method: "GET",
        params: {
          sort: `${sortFieldName},${sortDirection}`,
          page: pageNumber - 1,
          size: pageSize,
        },
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
  getUserById: async function (userId) {
    try {
      const response = await api.request({
        url: `/user/${userId}`,
        method: "GET",
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
};
