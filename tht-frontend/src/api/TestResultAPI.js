import api from "./configs/axiosConfigs";
//Api's for Fetching Manual Test Case Questions and The Options.
export const TestResultAPI = {
	getTestCases: async function (trid) {
		try {
			const response = await api.request({
				url: `/testcase-result`,
				method: "GET",
				params: { testRequestId: trid, manual:true, sort: "rank,asc" },
			});
			// console.log(response);
			return response.data;
		} catch (error) {
			throw error;
		}
	},
	getQuestions: async function (parentTestcaseResultId) {
		try {
			const response = await api.request({
				url: `/testcase-result`,
				method: "GET",
				params: { parentTestcaseResultId: parentTestcaseResultId },
			});
			// console.log(response);
			return response.data;
		} catch (error) {
			throw error;
		}
	},

	getTestCaseOptions: async function (testcaseId) {
		try {
			console.log(testcaseId);
			const response = await api.request({
				url: "/testcase-option",
				method: "GET",
				params: {
					testcaseId:testcaseId
				},
			});
			// console.log(response);
			return response.data;
		} catch (error) {
			throw error;
		}
	},
};
