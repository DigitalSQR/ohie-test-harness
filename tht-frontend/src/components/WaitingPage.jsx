import { Fragment } from "react";
import Header from "./Header";
import { useDispatch } from "react-redux";
import { log_out } from "../reducers/authReducer";
import fillinimage from '../styles/img/dystopian.jpg'
import { useNavigate } from "react-router-dom";

export default function WaitingPage() {
  const navigate = useNavigate();
    const dispatch = useDispatch();
    const ClickHandler = () => {
        dispatch(log_out());
        console.log('User has submitted info for verification')
    }
  return (
    <Fragment>
      <Header/>
      <div style={{display:'d-inline-flex',background:'white',height:'100%',alignItems:'center'}}>
        <h6 style={{textAlign:'center'}}>
          Hello User! Your credentials have been submitted for verification.
          We'll get back to you soon.
        </h6>
      
        <button onClick={ClickHandler} style={{marginLeft:'650px',border:'black'}}>Return</button>
        <button onClick={()=>{navigate('/dashboard/user')}} style={{marginLeft:'650px',border:'black'}}> Users</button>

      </div>
    </Fragment>
  );
}
