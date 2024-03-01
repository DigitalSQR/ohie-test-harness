import api from "./configs/axiosConfigs";
import { paramSerialize } from "../utils/utils";
//Api's for Fetching Manual Test Case Questions and The Options.
export const TestResultAPI = {
	getTestCases: async function (trid) {
	
			const response = await api.request({
				url: `/testcase-result`,
				method: "GET",
				params: {
					testRequestId: trid,
					manual: true,
					size: "1000",
					sort: "rank",
				},
			});
			return response.data;
		
	},
	getTestcaseResultStatus: async function (testcaseResultId, params) {
		if (!!params.manual) {
			params.manual = true;
		}
		if (!!params.automated) {
			params.automated = true;
		}
		if (!!params.required) {
			params.required = true;
		}
		if (!!params.recommended) {
			params.recommended = true;
		}
		if (!!params.workflow) {
			params.workflow = true;
		}
		if (!!params.functional) {
			params.functional = true;
		}
		
			const response = await api.request({
				url: `/testcase-result/status/${testcaseResultId}`,
				method: "GET",
				params,
			});
			return response.data;
		
	},
	getQuestions: async function (parentTestcaseResultId, currentPage) {
		
			const response = await api.request({
				url: `/testcase-result`,
				method: "GET",
				params: {
					parentTestcaseResultId: parentTestcaseResultId,
					sort: "rank",
					manual: true,
					size: 1,
					page: currentPage,
				},
			});
			return response.data;
		
	},

	getTestCaseOptions: async function (testcaseId) {
		
			const response = await api.request({
				url: "/testcase-option",
				method: "GET",
				params: {
					testcaseId: testcaseId,
				},
			});
			return response.data;
		
	},
	saveOptions: async function (testcaseResultId, selectedTestcaseOptionIds) {
		
			const params = {};
			if(selectedTestcaseOptionIds && selectedTestcaseOptionIds.length > 0){
				params.selectedTestcaseOptionId = selectedTestcaseOptionIds;
			}
			const response = await api.request({
				url: `testcase-result/submit/${testcaseResultId}`,
				method: "PATCH",
				params,
				paramsSerializer: params => {
				  return paramSerialize(params);
				}
			});
			return response.data;
		
	},
	fetchCasesForProgressBar: async function (params) {
		params.size = 1000;
		
			const response = await api.request({
				url: "/testcase-result",
				method: "GET",
				params: params,
			});
			return response;
		
	},
	startTests: async function (params) {
		
			const response = await api.request({
				url: `/test-request/start-testing-process/${params.testRequestId}`,
				method: "PUT",
				params
			});
		
	},
	getTestCaseResultByTestRequestId: async function (testRequestId, manual, automated) {
		
			const params = {
				testRequestId: testRequestId,
				sort: "rank",
				size: 1000,
			};

			if (!!manual) {
				params.manual = true;
			}

			if (!!automated) {
				params.automated = true;
			}

			const response = await api.request({
				url: "/testcase-result",
				method: "GET",
				params: params,
			});

			return response.data;
		
	},
	getTestCaseResultById: async function(testResultId){
		const response = await api.request({
			url: `/testcase-result/${testResultId}`,
			method: "GET"
		});

		return response.data;
	},
	patchTestCaseResult: async function(testcaseResultId, patchData){
		const response = await api.request({
			url: `/testcase-result/${testcaseResultId}`,
			method: "PATCH",
			headers: {
				"Content-Type":"application/json-patch+json"
			},
			data : patchData
		});
		
		return response.data;
	}


};
