import { useLocation, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { log_out } from "../reducers/authReducer";
import { AuthenticationAPI } from "../api/AuthenticationAPI";
import { clearAuthInfo } from '../api/configs/axiosConfigs'
import "../scss/_sidebar.scss";
import logo from "../styles/images/logo-white.png"
import { useEffect, useState } from 'react';
export default function Sidebar() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const[activeMenuItem,setActiveMenuItem] = useState();

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

  const handleMenuItemClick = (path) => {
    setActiveMenuItem(path);
    navigate(path);
  };

  useEffect(()=>{
    setActiveMenuItem(location.pathname)
  },[location])
 

  return (
      <div className={isSidebarOpen ? "sidebar-wrapper open" : "sidebar-wrapper shrink"} id="mySidenav">
          <div className="close-sidemenu-icon" onClick={toggleSidebar}>
              <i className="bi bi-filter-left"></i>
          </div>
          <div className="logo-white">
              <img src={logo} alt="Logo" />
          </div>
          <ul className="side-menu">
              <li>
                  <a className={activeMenuItem === "/dashboard" ? "active" : ""} onClick={() => {handleMenuItemClick("/dashboard")}}>
                      <i className="bi bi-columns-gap menu-left-icon"></i>
                      <span> Dashboard </span>
                  </a>
              </li>
              <li>
                  <a className={activeMenuItem === "/dashboard/application-status" ? "active" : ""} onClick={() => handleMenuItemClick("/dashboard/application-status")}>
                      <i className="bi bi-file-earmark-bar-graph menu-left-icon"></i>
                      <span> Application Status </span>
                  </a>
              </li>
              <li>
                  <a className={activeMenuItem === "/dashboard/registration-request" ? "active" : ""} onClick={() => handleMenuItemClick("/dashboard/registration-request")}>
                      <i className="bi bi-columns-gap menu-left-icon"></i>
                      <span> Registration Request </span>
                  </a>
              </li>
              <li>
                  <a className={activeMenuItem === "/dashboard/application-request" ? "active" : ""} onClick={() => handleMenuItemClick("/dashboard/application-request") }>
                      <i className="bi bi-file-earmark-bar-graph menu-left-icon"></i>
                      <span> Application Request </span>
                  </a>
              </li>
              <li>
                  <a onClick={() => { dispatch(log_out()); }}>
                      <i className="bi bi-file-earmark-bar-graph menu-left-icon"></i>
                      <span> Logout </span>
                  </a>
              </li>
          </ul>
      </div>
  );
}
