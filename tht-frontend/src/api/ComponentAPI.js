import api from "./configs/axiosConfigs";

export const ComponentAPI = {
	getCompoents: async function () {
		try {
			const response = await api.request({
				url: `/component`,
				method: "GET"
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	getComponentById: async function (componentId) {
		try {
			const response = await api.request({
				url: `/component/${componentId}`,
				method: "GET"
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	}
};
