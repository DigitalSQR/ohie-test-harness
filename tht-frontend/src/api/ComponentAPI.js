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

  getComponents: async function (params) {
   
      
    const response = await api.request({
      url: `/component`,
      method: "GET",
      params,
    });
    return response.data;
    
  },
  getComponentById: async function (componentId) {
   
    const response = await api.request({
      url: `/component/${componentId}`,
      method: "GET",
    });
    return response.data;
    
  },
  changeState: async function (componentId, changeState) {
   
    const response = await api.request({
      url: `/component/state/${componentId}/${changeState}`,
      method: "PATCH",
    });
    return response;
    
  },
};
