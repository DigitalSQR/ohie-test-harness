import api from "./configs/axiosConfigs";
export const TestProcessAPI = {
  stopTestProcess: async function (testRequestId, params) {
    try {
      const response = await api.request({
        url: `/test-request/stop-testing-process/${testRequestId}`,
        method: "PUT",
        params
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};