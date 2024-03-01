import api from "./configs/axiosConfigs";

export const CaptchaAPI = {
    getCaptcha: async function () {
		
			const response = await api.request({
				url: `/captcha`,
				method: "GET"
			});
			return response;
		
	}
}