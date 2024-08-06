import { Fragment } from "react";
import Header from "../../CommonFiles/Header/Header";
import { useDispatch } from "react-redux";
import { log_out } from "../../../reducers/authReducer";
import { useNavigate } from "react-router-dom";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { notification } from "antd";

export default function WaitingPage() {
  const navigate = useNavigate();
    const dispatch = useDispatch();
    const ClickHandler = () => {
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
      }).catch(()=>{});    }
  return (
    <Fragment>
      <Header/>
      <div style={{display:'d-inline-flex',background:'white',height:'100%',alignItems:'center'}}>
        <h6 style={{textAlign:'center'}}>
          Hello User! Your credentials have been submitted for verification.
          We'll get back to you soon.
        </h6>
      
        <button onClick={ClickHandler} style={{marginLeft:'650px',border:'black'}} id="#waitingPage-return">Return</button>
        <button onClick={()=>{navigate('/user')}} style={{marginLeft:'650px',border:'black'}} id="#waitingPage-users"> Users</button>

      </div>
    </Fragment>
  );
}
