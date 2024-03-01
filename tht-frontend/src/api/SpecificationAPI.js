import api from "./configs/axiosConfigs";
export const SpecificationAPI = {
  getSpecificationsByComponentId: async function (componentId, manual) {
   
      const response = await api.request({
        url: `/specification`,
        method: "GET",
        params: {
          manual: manual,
          componentId:componentId,
        },
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
};
