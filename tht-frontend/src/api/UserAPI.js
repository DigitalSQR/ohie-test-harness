import api from "./configs/axiosConfigs";

export const UserAPI = {
	viewUser: async function () {
		try {
			const response = await api.request({
				url: `/user/principal`,
				method: "GET",
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	getUsers: async function () {
		try {
			const response = await api.request({
				url: `/user`,
				method: "GET",
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
};
