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
	getTestRequestsByState: async function (state, sortFieldName="createdAt", sortDirection="desc", currentPage, pageSize) {
		try {
			const response = await api.request({
				url: `/test-request`,
				method: "GET",
				params: {
					state,
					sort: `${sortFieldName},${sortDirection}`,
					page:currentPage,
					size:pageSize
				},
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
				method: "PUT"
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
};
