import api from "./configs/axiosConfigs";

export const GradeAPI = {
    getAllGrades: function(){
        return api.request({
            url: `/grade/all`,
            method: 'GET',
        });
    }
}