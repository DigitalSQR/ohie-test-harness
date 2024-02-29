// import { paramSerialize } from "../utils/utils";
import api from "./configs/axiosConfigs";

export const DocumentAPI = {
	uploadDocument: async function (data) {
		try {
			const response = await api.request({
				url: `/document`,
				method: "POST",
                data,
                headers: {
                    'content-type': 'multipart/form-data',
                },
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
    downloadDocument: function (documentId, name) {
		try {
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
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	base64Document: function (documentId) {
		try {
			return api.request({
				url: `/document/base64/${documentId}`,
				method: "GET",            
			}).then((response) => {
                return response.data;
            })
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	changeDocumentState: async function (documentId, changeState) {
		try {
			const response = await api.request({
				url: `/document/state/${documentId}/${changeState}`,
				method: "PATCH",
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	getDocumentsByRefObjUriAndRefId: async function (refObjUri, refId, state, documentType) {
		try {
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
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
}