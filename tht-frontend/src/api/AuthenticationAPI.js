import Password from "antd/es/input/Password";
import api from "./configs/axiosConfigs";

export const AuthenticationAPI = {
	doLogin: async function (data, captchaInfo) {
		try {
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
		} catch (error) {
			throw error;
		}
	},
	refreshToken: async function (data) {
		try {
			const response = await api.request({
				url: `/oauth/token`,
				method: "POST",
				data: data,
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	doLogout: async function (data) {
		try {
			const response = await api.request({
				url: `/user/logout`,
				method: "POST",
			});
			return response.data;
		} catch (error) {
			throw error; // You can choose to re-throw the error or handle it in a specific way
		}
	},
	signup: async function (data, captchaInfo) {
		try {
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
		} catch (error) {
			throw error;
		}
	},
	forgotpassword: async function (email) {
		try {
			const response = await api.request({
				url: `/user/forgot/password`,
				params: {
					userEmail: email
				},
				method: "GET"
			});
			return response;
		} catch (error) {
			throw error;
		}
	},
	resetPassword: async function (data) {
		try {
			const response = await api.request({
				url: `/user/update/password/`,
				data: data,
				method: "POST"
			});
			return response;
		} catch (error) {
			throw error;
		}
	},
	verifyEmail: async function (base64UserEmail, base64TokenId) {
		try {
			const response = await api.request({
				url: `/user/verify/${base64UserEmail}/${base64TokenId}`,
				method: "POST"
			});
			return response;
		} catch (error) { throw error }
	}
}

