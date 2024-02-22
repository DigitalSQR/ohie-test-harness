import api from "./configs/axiosConfigs";
export const SpecificationAPI = {
  getSpecificationsByComponentId: async function (componentId, manual) {
    try {
      const response = await api.request({
        url: `/specification`,
        method: "GET",
        params: {
          manual: manual,
          componentId:componentId,
        },
      });
      console.log(response.data)
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  changeState: async function (specificationId, changeState) {
    try {
      const response = await api.request({
        url: `/specification/state/${specificationId}/${changeState}`,
        method: "PATCH",
      });
      return response;
    } catch (error) {
      throw error;
    }
  },
};
