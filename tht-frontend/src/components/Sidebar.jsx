import "../styles/Landing.css";
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { log_out } from "../reducers/authReducer";
import { AuthenticationAPI } from "../api/AuthenticationAPI";
import { clearAuthInfo } from '../api/configs/axiosConfigs'


export default function Sidebar({onComponentClick}){
    
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const handleLogout = async () => {
      
      AuthenticationAPI.doLogout()
        .then((response) => {
          dispatch(log_out());
          navigate("/login");
          clearAuthInfo();        
        })
        .catch((error) => {
          
        })   
  
    };
    const viewUser = async () => {
        AuthenticationAPI.viewUser("argus")
        .then((response) => {
        
        })
        .catch((error) => {
          // Handle the error here
  
        /*  notification.error({
            placement: "bottomRight",
            description: "Invalid username or password",
          });*/
        })   
    };
    return(
        <div className="wrapper d-flex">
        <div className="d-inline-flex flex-column flex-shrink-0 px-3 sidebar">
          <div className="menu-toggle text-end">
            <span className="menu-toggle-btn w-30"></span>
          </div>
          <ul className="nav nav-pills flex-column mb-auto">
            <li className="nav-item">
              <button className="nav-link" aria-current="page">
                <span className="report w-30"></span>
                Reports
              </button>
            </li>
            <li>
              <button  className="sidebar-items">
                <span className="history w-30"></span>
                History
              </button>
            </li>
            <li>
              <button  className="sidebar-items">
                <span className="profile w-30"></span>
                Profile
              </button>
            </li>
            <li>
              <button className="sidebar-items">
                <span className="settings w-30"></span>
                Settings
              </button>
            </li>
            <li>
              <button onClick={viewUser} className="sidebar-items">
                <span className="settings w-30"></span>
                View User
              </button>
            </li>
            <li>
              <button onClick={handleLogout} className="sidebar-items">
                <span className="settings w-30"></span>
                Log out
              </button>
            </li>
            <li>
              <button className="sidebar-items" onClick={()=>{
               navigate('/dashboard/user')}} >
                <span className="settings w-30"></span>
                Users
              </button>
            </li>
            <li>
              <button className="sidebar-items" onClick={()=>{navigate('/testcases');
            navigate('/dashboard/testcases')}}>
                <span className="settings w-30"></span>
                UploadCases
              </button>
            </li>
          </ul>
        </div>
        </div>
    )
}