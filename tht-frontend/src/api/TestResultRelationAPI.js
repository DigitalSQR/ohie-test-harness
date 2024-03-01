import { ref } from "yup";
import api from "./configs/axiosConfigs";
//Api's for Fetching Manual Test Case Questions and The Options.

export const TestResultRelationAPI = {
	getTestcaseResultRelatedObject: async function (testcaseResultId, refObjUri) {
		
			const response = await api.request({
				url: `/testcase-result-relation/${testcaseResultId}/${refObjUri}`,
				method: "GET"
			});
			return response.data;
		
	},

	getTestcaseResultRelationInfosByTestcaseResultIdAndRefObjUri: async function (testcaseResultId, refObjUri) {
		
			const params = {};
			if(testcaseResultId) params.testcaseResultId = testcaseResultId;
			if(refObjUri) params.refObjUri = refObjUri;
	
			const response = await api.request({
				url: `/testcase-result-relation`,
				method: "GET",
				params
			});
			
			return response.data;
		
	}

	
}