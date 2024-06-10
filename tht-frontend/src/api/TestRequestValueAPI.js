import api from "./configs/axiosConfigs";

export const TestRequestValueAPI = {
   updateTestRequestValues:async function(data){
    const response = await api.request({
      url:"/test-request-value/updateValue",
      method:"PUT",
      data
    })
  }
};
