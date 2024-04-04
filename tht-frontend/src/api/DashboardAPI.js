import api from "./configs/axiosConfigs";

export const DashboardAPI={
    getDashBoard: function () {
        return api
          .request({
            url: `/test-request/dashboard`,
            method: "GET"
          })
          .then((response) => response.data);
      }
}
