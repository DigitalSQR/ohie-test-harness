import api from "./configs/axiosConfigs";

export const AdminUserAPI = {
  addUser: async function (data) {
   
      const response = await api.request({
        url: `/user`,
        method: "POST",
        data,
      });

      return response.data;
   
  },
  updateUserDetails: async function (data) {
   
      const response = await api.request({
        url: `/user/`,
        method: "PUT",
        data,
      });
      return response.data;
   
  },
  fetchUserDetails: async function (userId) {
    
      const response = await api.request({
        url: `/user/${userId}`,
        method: "GET",
      });
      return response.data;
    
  },
  updateUserState: async function (userId, changeState) {
   
      const response = await api.request({
        url: `/user/state/${userId}/${changeState}`,
        method: "PATCH",
      });
      return response.data;
   
  },

  fetchAllUsers: async function (sortFieldName="createdAt", sortDirection="desc") {
    
      const response = await api.request({
        url: `/user`,
        method: "GET",
        params: {
          sort: `${sortFieldName},${sortDirection}`,
        },
      });
      return response.data;
   
  },
};
