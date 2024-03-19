import api from "./configs/axiosConfigs";
export const SpecificationAPI = {
  getSpecificationsByComponentId: async function (params) {
    const response = await api.request({
      url: `/specification`,
      method: "GET",
      params,
    });
    return response.data;
  },

  createSpecification: async function (data) {
    return api
      .request({
        url: `/specification`,
        method: "POST",
        data,
      })
      .then((response) => response.data);
  },

  updateSpecification: async function (data) {
    return api
      .request({
        url: `/specification`,
        method: "PUT",
        data,
      })
      .then((response) => response.data);
  },

  getSpecificationById: async function (specificationId) {
    const response = await api.request({
      url: `/specification/${specificationId}`,
      method: "GET",
    });
    return response.data;
  },

  changeState: async function (specificationId, changeState) {
    const response = await api.request({
      url: `/specification/state/${specificationId}/${changeState}`,
      method: "PATCH",
    });
    return response;
  },

  changeRank: async function (specificationId, changeRank) {
   
    const response = await api.request({
      url: `/specification/rank/${specificationId}/${changeRank}`,
      method: "PATCH",
    });
    return response;
    
  },
};
