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
					size: "50",
					sort: "rank",
				},
			});
			// console.log(response);
			return response.data;
		} catch (error) {
			throw error;
		}
	},
	getQuestions: async function (parentTestcaseResultId,currentPage) {
		try {
			const response = await api.request({
				url: `/testcase-result`,
				method: "GET",
				params: {
					parentTestcaseResultId: parentTestcaseResultId,
					sort: "rank",
					size: 1,
					page:currentPage
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

	getTestCaseResultById: async function (testRequestId) {
		try {
		  const response = await api.request({
			url: "/testcase-result",
			method: "GET",
			params: {
			  testRequestId: testRequestId,
			},
		  });
		  return response.data;
		} catch (error) {
		  throw error;
		}
	  }
	  
};
