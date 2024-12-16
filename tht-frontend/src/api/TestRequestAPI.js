import { compose } from "redux";
import { paramSerialize } from "../utils/utils";
import api from "./configs/axiosConfigs";

export const TestRequestAPI = {
  createTestRequest: async function (data) {
    console.log(data);
    const response = await api.request({
      url: `/test-request`,
      method: "POST",
      data,
    });
    return response.data;
  },
  validateTestRequest: async function (validationTypeKey, data) {
    const response = await api.request({
      url: `/test-request/validate`,
      method: "POST",
      params: {
        validationTypeKey,
      },
      data,
    });
    return response.data;
  },
  getTestRequestsById: async function (testRequestId) {
    const response = await api.request({
      url: `/test-request/${testRequestId}`,
      method: "GET",
    });
    return response.data;
  },
  getTestRequestsByFilter: async function (params) {
    const response = await api.request({
      url: `/test-request/search`,
      method: "GET",
      params,
      paramsSerializer: (params) => {
        return paramSerialize(params);
      },
    });
    return response.data;
  },
  changeState: async function (id, state, message) {
    const response = await api.request({
      url: `/test-request/state/${id}/${state}`,
      method: "PATCH",
      data: {
        message: message ? message : null,
      },
    });
    return response.data;
  },
  validateChangeState: async function (id, state, message) {
    const response = await api.request({
      url: `/test-request/validate/state/${id}/${state}`,
      method: "PATCH",
      data: {
        message: message ? message : null,
      },
    });
    return response.data;
  },
  updateTestRequest: async function (data) {
    const response = await api.request({
      url: "/test-request",
      method: "PUT",
      data,
    });
  },
};
