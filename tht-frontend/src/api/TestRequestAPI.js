import { paramSerialize } from "../utils/utils";
import api from "./configs/axiosConfigs";

export const TestRequestAPI = {
	createTestRequest: async function (data) {
		try {
			const response = await api.request({
				url: `/test-request`,
				method: "POST",
                data
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	validateTestRequest: async function (validationTypeKey, data) {
		try {
			const response = await api.request({
				url: `/test-request/validate`,
				method: "POST",
				params: {
					validationTypeKey
				},
				data
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	getTestRequestsById: async function (testRequestId) {
		try {
			const response = await api.request({
				url: `/test-request/${testRequestId}`,
				method: "GET",
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	getTestRequestsByState: async function (state, sortFieldName, sortDirection, currentPage, pageSize) {
		try {
			const params={}
			if(!!sortFieldName){
				params.sort = `${sortFieldName},${sortDirection}`;
			}
			if(state) params.state = state;
			if(currentPage) params.page=currentPage;
			if(pageSize) params.size=pageSize;
			const response = await api.request({
				url: `/test-request`,
				method: "GET",
				params,
				paramsSerializer: params => {
				  return paramSerialize(params);
				}
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	changeState: async function (id, state) {
		try {
			const response = await api.request({
				url: `/test-request/state/${id}/${state}`,
				method: "PATCH"
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
};
