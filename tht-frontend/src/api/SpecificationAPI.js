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
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};
