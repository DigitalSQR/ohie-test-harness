import api from "./configs/axiosConfigs";
export const TestCaseAPI = {
  getTestCasesBySpecificationId: async function (specificationId) {
    try {
        const response = await api.request({
          url: `/testcase`,
          method: "GET",
          params: {
            specificationId:specificationId
          },
        });
        return response.data;
      } catch (error) {
        throw error;
      }
  },
};
