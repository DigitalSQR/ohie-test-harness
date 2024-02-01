import api from "./configs/axiosConfigs";
//Api's for Fetching Manual Test Case Questions and The Options.
export const TestResultAPI = {
	getTestCases: async function (trid) {
		try {
			const response = await api.request({
				url: `/testcase-result`,
				method: "GET",
				params: {
					testRequestId: trid,
					manual: true,
					size: "100",
					sort: "rank",
				},
			});
			// console.log(response);
			return response.data;
		} catch (error) {
			throw error;
		}
	},
	getQuestions: async function (parentTestcaseResultId, currentPage) {
		try {
			const response = await api.request({
				url: `/testcase-result`,
				method: "GET",
				params: {
					parentTestcaseResultId: parentTestcaseResultId,
					sort: "rank",
					size: 1,
					page: currentPage,
				},
			});
			return response.data;
		} catch (error) {
			throw error;
		}
	},

	getTestCaseOptions: async function (testcaseId) {
		try {
			const response = await api.request({
				url: "/testcase-option",
				method: "GET",
				params: {
					testcaseId: testcaseId,
				},
			});
			return response.data;
		} catch (error) {
			throw error;
		}
	},
	saveOptions: async function (testcaseResultId, selectedTestcaseOptionId) {
		try {
			const response = await api.request({
				url: `testcase-result/submit/${testcaseResultId}/${selectedTestcaseOptionId}`,
				method: "PATCH",
			});
			console.log("The saveOptions result is ", response.data);
			return response.data;
		} catch (error) {
			throw error;
		}
	},
	fetchCasesForProgressBar: async function (params) {
		try {
			const response = await api.request({
				url: "/testcase-result",
				method: "GET",
				params: params,
			});
			return response;
		} catch (error) {
			throw error;
		}
	},
	startTests: async function (params) {
		try {
			const response = await api.request({
				url: `/test-request/start-testing-process/${params.testRequestId}`,
				method: "PUT",
				params:{
					manual:params.manual,
					refId:params.testRequestId,
					refObjUri:params.TESTREQUEST_REFOBJURI,
					testRequestId:params.testRequestId
				}
			});
		} catch (error) {
			throw error;
		}
	},
	getTestCaseResultById: async function (testRequestId, manual) {
		try {
		  const params = {
			testRequestId: testRequestId,
			sort: "rank",
		  };
	  
		  if (manual !== null && manual !== undefined) {
			params.manual = manual;
		  }
	  
		  const response = await api.request({
			url: "/testcase-result",
			method: "GET",
			params: params,
		  });
	  
		  return response.data;
		} catch (error) {
		  throw error;
		}
	  }
	  

};
