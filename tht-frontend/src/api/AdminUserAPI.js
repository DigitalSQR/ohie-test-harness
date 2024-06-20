import { ROLE_ID_ADMIN, ROLE_ID_TESTER } from "../constants/role_constants";
import { paramSerialize } from "../utils/utils";
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
  validateUser: async function (validationTypeKey, data) {
			const response = await api.request({
				url: `/user/validate`,
				method: "POST",
				params: {
					validationTypeKey
				},
				data
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

  fetchAllUsers: async function (params) {
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
};
