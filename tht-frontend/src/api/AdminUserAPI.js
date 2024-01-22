import api from "./configs/axiosConfigs";

export const AdminUserAPI = {
  addUser: async function (data) {
    try {
      const response = await api.request({
        url: `/user`,
        method: "POST",
        data,
      });

      return response.data;
    } catch (error) {
      throw error;
    }
  },
  updateUserDetails: async function (data) {
    try {
      const response = await api.request({
        url: `/user/`,
        method: "PUT",
        data,
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  fetchUserDetails: async function (userId) {
    try {
      const response = await api.request({
        url: `/user/${userId}`,
        method: "GET",
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  updateUserState: async function (userId, changeState) {
    try {
      const response = await api.request({
        url: `/user/state/${userId}/${changeState}`,
        method: "PUT",
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  fetchAllUsers: async function (sortFieldName="createdAt", sortDirection="desc") {
    try {
      const response = await api.request({
        url: `/user`,
        method: "GET",
        params: {
          sort: `${sortFieldName},${sortDirection}`
        },
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};
