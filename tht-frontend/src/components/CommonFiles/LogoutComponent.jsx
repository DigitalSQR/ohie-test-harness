import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { log_out } from "../../reducers/authReducer";
import { AuthenticationAPI } from "../../api/AuthenticationAPI";
import { notification } from "antd";

const LogoutComponent = () => {
  const dispatch = useDispatch();

  useEffect(() => {                     
     AuthenticationAPI.doLogout().then((res)=>{
    if(res === true){
      dispatch(log_out());
    }
    if(res === false){
      notification.error({
        className: "notificationError",
        message: "Unable to logout! Please try again.",
        placement: "bottomRight",
      });
    }
  }).catch(()=>{});
  });
};

export default LogoutComponent;
