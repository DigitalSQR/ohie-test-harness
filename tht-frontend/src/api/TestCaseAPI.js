import api from "./configs/axiosConfigs";
export const TestCaseAPI = {
  getTestCasesBySpecificationId: async function (specificationId) {
    try {
      const response = await api.request({
        url: `/testcase`,
        method: "GET",
        params: {
          specificationId: specificationId,
        },
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  getTestCasesById: async function (testcaseId) {
    try {
      const response = await api.request({
        url: `/testcase/${testcaseId}`,
        method: "GET",
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  editTestCaseName: async function (data) {
    try {
      const response = await api.request({
        url: `/testcase`,
        method: "PUT",
        data,
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};
