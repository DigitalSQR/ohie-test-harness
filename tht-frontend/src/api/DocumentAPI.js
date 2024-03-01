// import { paramSerialize } from "../utils/utils";
import api from "./configs/axiosConfigs";

export const DocumentAPI = {
	uploadDocument: async function (data) {
		
			const response = await api.request({
				url: `/document`,
				method: "POST",
                data,
                headers: {
                    'content-type': 'multipart/form-data',
                },
			});
			return response.data;
		
	},
    downloadDocument: function (documentId, name) {
		
			return api.request({
				url: `/document/file/${documentId}`,
				method: "GET",
                responseType: 'arraybuffer'
                
			}).then((response) => {
                let blob = new Blob([response.data], { type:"application/pdf" })
                let link = document.createElement('a')
                link.href = window.URL.createObjectURL(blob)
                link.download = name || 'data.pdf';
                link.click()
            });
		
	},
	base64Document: function (documentId) {
		
			return api.request({
				url: `/document/base64/${documentId}`,
				method: "GET",            
			}).then((response) => {
                return response.data;
            })
		
	},
	changeDocumentState: async function (documentId, changeState) {
	
			const response = await api.request({
				url: `/document/state/${documentId}/${changeState}`,
				method: "PATCH",
			});
			return response.data;
		
	},
	getDocumentsByRefObjUriAndRefId: async function (refObjUri, refId, state, documentType) {
	
			const response = await api.request({
				url: `/document`,
				method: "GET",
                params: {
					refObjUri,
					refId,
					state,
					documentType
				},
			});
			return response.data;
		
	},
}