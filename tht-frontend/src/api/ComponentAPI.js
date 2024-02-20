import api from "./configs/axiosConfigs";

export const ComponentAPI = {
  getComponents: async function (state) {
    try {
      const params = {};
      if (state) params.state = state;
      const response = await api.request({
        url: `/component`,
        method: "GET",
        params,
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
  getComponentById: async function (componentId) {
    try {
      const response = await api.request({
        url: `/component/${componentId}`,
        method: "GET",
      });
      return response.data;
    } catch (error) {
      throw error; // You can choose to re-throw the error or handle it in a specific way
    }
  },
  changeState: async function (componentId, changeState) {
    try {
      const response = await api.request({
        url: `/component/state/${componentId}/${changeState}`,
        method: "PATCH",
      });
      return response;
    } catch (error) {
      throw error;
    }
  },
};
