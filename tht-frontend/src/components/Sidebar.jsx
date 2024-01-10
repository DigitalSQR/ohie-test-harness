import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { log_out } from "../reducers/authReducer";
import { AuthenticationAPI } from "../api/AuthenticationAPI";
import { clearAuthInfo } from '../api/configs/axiosConfigs'
import "../scss/_sidebar.scss";
import logo from "../styles/images/logo-white.png"
import { useState } from 'react';
export default function Sidebar() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  /* 
   * This is to expand/shrink the wrapper when the side menu bar is toogled
   * To Do: Refactor the structure in such a way that margins are removed from the pages
   * and the outlet will directly render things in the middle of the page.  
  **/
  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
    let wrapper = document.getElementById("wrapper");
    wrapper.classList.toggle("expand");
  };

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

  return (
    //side-bar
    <div className={isSidebarOpen ? "sidebar-wrapper open" : "sidebar-wrapper shrink"} id="mySidenav">
      <div class="close-sidemenu-icon" onClick={toggleSidebar}><i class="bi bi-filter-left"></i></div>
      <div class="logo-white">
        <img src={logo} />
      </div>
      <ul class="side-menu">
        <li><a class="active" onClick={() => { navigate("/dashboard") }}><i class="bi bi-columns-gap menu-left-icon"></i><span> Dashboard </span></a></li>
        <li><a onClick={() => { navigate("/dashboard/application-status") }}><i class="bi bi-file-earmark-bar-graph menu-left-icon"></i><span> Application Status </span></a></li>
        <li><a class="active" onClick={() => { navigate("/dashboard/registration-request") }}><i class="bi bi-columns-gap menu-left-icon"></i>Registeration Request</a></li>
        <li><a onClick={() => { navigate("/dashboard/application-request") }}><i class="bi bi-file-earmark-bar-graph menu-left-icon"></i>Application Request</a></li>
        <li><a class="active" onClick={() => { navigate("/dashboard/application-request") }}><i class="bi bi-columns-gap menu-left-icon"></i>Register Application</a></li>
        <li><a onClick={() => { dispatch(log_out()); }}><i class="bi bi-file-earmark-bar-graph menu-left-icon"></i><span> Logout </span></a></li>

        {/* <li><a><i class="bi bi-file-earmark-bar-graph menu-left-icon"></i></a></li> */}
      </ul>
    </div>
    //side-bar
  )
}