import api  from "./configs/axiosConfigs";

export const AuthenticationAPI = { 
    doLogin: async function (data) {
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
    }
}