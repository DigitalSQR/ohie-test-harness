import api from "./configs/axiosConfigs";

export const TestcaseVariableAPI = {
  getTestcaseVariablesByComponentId: async function (componentId) {
    const response = await api.request({
      url: `/testcase-variable/inputs`,
      method: "GET",
      params: {
        componentId
      },
    });
    return response.data;
  },
  getTestcaseVariablesById : async function (testcaseVariableId) {
    const response = await api.request({
      url: `/testcase-variable/${testcaseVariableId}`,
      method: "GET",
    });
    return response.data;
  },
};
