import api from "./configs/axiosConfigs";
export const TestCaseOptionsAPI = {
  getTestCaseOptionsByTestcaseId: async function (testcaseId) {
    try {
        const response = await api.request({
          url: `/testcase-option`,
          method: "GET",
          params: {
            testcaseId:testcaseId
          },
        });
        return response.data;
      } catch (error) {
        throw error;
      }
  },
};
