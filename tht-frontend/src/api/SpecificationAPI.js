import api from "./configs/axiosConfigs";
import { paramSerialize } from "../utils/utils";
export const SpecificationAPI = {
  getSpecificationsByComponentId: async function (params) {
    const response = await api.request({
      url: `/specification/search`,
      method: "GET",
      params,
      paramsSerializer: (params) => {
        return paramSerialize(params);
      },
    });
    return response.data;
  },

  createSpecification: async function (data) {
    return api
      .request({
        url: `/specification`,
        method: "POST",
        data,
      })
      .then((response) => response.data);
  },

  updateSpecification: async function (data) {
    return api
      .request({
        url: `/specification`,
        method: "PUT",
        data,
      })
      .then((response) => response.data);
  },

  getSpecificationById: async function (specificationId) {
    const response = await api.request({
      url: `/specification/${specificationId}`,
      method: "GET",
    });
    return response.data;
  },

  changeState: async function (specificationId, changeState) {
    const response = await api.request({
      url: `/specification/state/${specificationId}/${changeState}`,
      method: "PATCH",
    });
    return response;
  },

  changeRank: async function (specificationId, changeRank) {
    const response = await api.request({
      url: `/specification/rank/${specificationId}/${changeRank}`,
      method: "PATCH",
    });
    return response;
  },
};
