import api from "./configs/axiosConfigs";

export const CaptchaAPI = {
    getCaptcha: async function () {
		try {
			const response = await api.request({
				url: `/captcha`,
				method: "GET"
			});
			return response;
		} catch (error) {
			throw error;
		}
	}
}