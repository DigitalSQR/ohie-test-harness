import api from "./configs/axiosConfigs";

export const ComponentAPI = {
  createComponent: function (data) {
    return api
      .request({
        url: `/component`,
        method: "POST",
        data,
      })
      .then((response) => response.data);
  },

  updateComponent: function (data) {
    return api
      .request({
        url: `/component`,
        method: "PUT",
        data,
      })
      .then((response) => response.data);
  },

  getComponents: async function (
    sortFieldName,
    sortDirection,
    pageNumber,
    pageSize,
    state
  ) {
    try {
      const params = {};
      if (!!sortFieldName) {
        params.sort = `${sortFieldName},${sortDirection}`;
      }
      if (pageNumber) params.page = pageNumber;
      if (pageSize) params.size = pageSize;
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
