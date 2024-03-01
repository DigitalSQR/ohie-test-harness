import api from "./configs/axiosConfigs";
export const TestCaseAPI = {
  getTestCasesBySpecificationId: async function (specificationId) {
   
      const response = await api.request({
        url: `/testcase`,
        method: "GET",
        params: {
          specificationId: specificationId,
        },
      });
      return response.data;
   
  },
  getTestCasesById: async function (testcaseId) {
   
      const response = await api.request({
        url: `/testcase/${testcaseId}`,
        method: "GET",
      });
      return response.data;
   
  },
  editTestCaseName: async function (data) {
    
      const response = await api.request({
        url: `/testcase`,
        method: "PUT",
        data,
      });
      return response.data;
   
  },
  changeState: async function (testcaseId, changeState) {
   
      const response = await api.request({
        url: `/testcase/state/${testcaseId}/${changeState}`,
        method: "PATCH",
      });
      return response;
   
  },
  patchTestcase: async function(testcaseId, patchData){
		const response = await api.request({
			url: `/testcase/${testcaseId}`,
			method: "PATCH",
			headers: {
				"Content-Type":"application/json-patch+json"
			},
			data : patchData
		});
		
		return response.data;
  }
};
