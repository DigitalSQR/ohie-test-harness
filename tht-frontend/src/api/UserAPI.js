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
        method: "PATCH",
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
    pageSize = 10,
    state
  ) {
    try {
      const params={
				sort: `${sortFieldName},${sortDirection}`,
			}
			if(pageNumber) params.pageNumber=pageNumber;
			if(pageSize) params.pageSize=pageSize;  
      if(state)params.state=state;
      const response = await api.request({
        url: `/user`,
        method: "GET",
        params
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
