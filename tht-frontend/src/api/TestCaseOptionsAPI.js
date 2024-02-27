import api from "./configs/axiosConfigs";
export const TestCaseOptionsAPI = {
  getTestCaseOptionsByTestcaseId: async function (testcaseId) {
    try {
      const response = await api.request({
        url: `/testcase-option`,
        method: "GET",
        params: {
          testcaseId,
          size: "1000",
					sort: "rank"
        },
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  getTestCaseOptionsByTestcaseOptionId: async function (testcaseOptionId) {
    try {
      const response = await api.request({
        url: `/testcase-option/${testcaseOptionId}`,
        method: "GET",
        params: {
          testcaseOptionId,
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
      console.log(response.data)
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
  changeState: async function (testcaseOptionId, changeState) {
    try {
      const response = await api.request({
        url: `/testcase-option/state/${testcaseOptionId}/${changeState}`,
        method: "PATCH",
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  patchTestcaseOption: async function(testcaseId, patchData){
		const response = await api.request({
			url: `/testcase-option/${testcaseId}`,
			method: "PATCH",
			headers: {
				"Content-Type":"application/json-patch+json"
			},
			data : patchData
		});
		
		return response.data;
  }
};
