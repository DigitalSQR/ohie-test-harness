import api from "./configs/axiosConfigs";
export const TestCaseOptionsAPI = {
  getTestCaseOptionsByTestcaseId: async function (testcaseId) {
    try {
      const response = await api.request({
        url: `/testcase-option`,
        method: "GET",
        params: {
          testcaseId,
        },
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  editTestCaseOptions: async function (data) {
    try {
      const response = await api.request({
        url: `/testcase-option`,
        method: "PUT",
        data,
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  editTestCaseOptionsState: async function (data) {
    try {
      const response = await api.request({
        url: `/testcase-option/state/{testcaseOptionId}/{changeState}`,
        method: "PATCH",
        data,
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};
