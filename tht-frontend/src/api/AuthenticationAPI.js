import Password from "antd/es/input/Password";
import api from "./configs/axiosConfigs";

export const AuthenticationAPI = {
	doLogin: async function (data, captchaInfo) {
		
			const headers = {};
			if (captchaInfo.captcha!=="") {
				headers.captchaCode = captchaInfo.code;
				headers.captcha = captchaInfo.captcha;
			}
			const response = await api.request({
				url: `/oauth/token`,
				method: "POST",
				data: data,
				headers: headers
			});
			return response.data;
		
	},
	refreshToken: async function (data) {
			const response = await api.request({
				url: `/oauth/token`,
				method: "POST",
				data: data,
			});
			return response.data;
	},
	doLogout: async function (data) {
			const response = await api.request({
				url: `/user/logout`,
				method: "POST",
			});
			return response.data;
	},
	signup: async function (data, captchaInfo) {
		const headers = {};
		if (captchaInfo.captcha!=="") {
			headers.captchaCode = captchaInfo.code;
			headers.captcha = captchaInfo.captcha;
		}
		const response = await api.request({
			url: `/user/register`,
			method: "POST",
			data: data,
			headers: headers
		});
		return response.data;
	},
	forgotpassword: async function (email) {
			const response = await api.request({
				url: `/user/forgot/password`,
				params: {
					userEmail: email
				},
				method: "GET"
			});
			return response;
	},
	resetPassword: async function (data) {
			const response = await api.request({
				url: `/user/update/password/`,
				data: data,
				method: "POST"
			});
			return response;
	},
	verifyEmail: async function (base64UserEmail, base64TokenId) {
			const response = await api.request({
				url: `/user/verify/${base64UserEmail}/${base64TokenId}`,
				method: "POST"
			});
			return response;
	},
	resendVerification: async function (email) {
			const response = await api.request({
				url: `/user/resend/verification`,
				params: {
					userEmail: email
				},
				method: "POST"
			});
			return response;
	},
}

