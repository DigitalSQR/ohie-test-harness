import api from "./configs/axiosConfigs";
export const TestCaseOptionsAPI = {
  getTestCaseOptionsByTestcaseId: async function (testcaseId) {
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
   
  },
  getTestCaseOptionsByTestcaseOptionId: async function (testcaseOptionId) {
      const response = await api.request({
        url: `/testcase-option/${testcaseOptionId}`,
        method: "GET",
        params: {
          testcaseOptionId,
        },
      });
      return response.data;
   
  },
  editTestCaseOptions: async function (data) {
      const response = await api.request({
        url: `/testcase-option`,
        method: "PUT",
        data,
      });
      return response.data;
    
  },
  editTestCaseOptionsState: async function (data) {
      const response = await api.request({
        url: `/testcase-option/state/{testcaseOptionId}/{changeState}`,
        method: "PATCH",
        data,
      });
      return response.data;
   
  },
  changeState: async function (testcaseOptionId, changeState) {
    
      const response = await api.request({
        url: `/testcase-option/state/${testcaseOptionId}/${changeState}`,
        method: "PATCH",
      });
      return response.data;
    
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
