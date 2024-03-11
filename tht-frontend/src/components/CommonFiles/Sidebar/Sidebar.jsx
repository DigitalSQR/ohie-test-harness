import { useLocation, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import "./_sidebar.scss";
import logo from "../../../styles/images/logo-white.png";
import { Fragment, useEffect, useState } from "react";
import { USER_ROLES } from "../../../constants/role_constants";
import { store } from "../../../store/store";
import { set_header } from "../../../reducers/homeReducer";
import { useCurrentRoute } from "../../../routes/routes";
export default function Sidebar({ isSidebarOpen, setIsSidebarOpen }) {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const [activeMenuItem, setActiveMenuItem] = useState();
  const [user, setUser] = useState();
  /*
   * This is to expand/shrink the wrapper when the side menu bar is toogled
   * To Do: Refactor the structure in such a way that margins are removed from the pages
   * and the outlet will directly render things in the middle of the page.
   **/
  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
    let wrapper = document.getElementById("wrapper");
    if (wrapper) {
      wrapper.classList.toggle("expand");
    }
  };

  const handleMenuItemClick = (path) => {
    setActiveMenuItem(path);
    navigate(path);
  };

  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUser(userInfo);
  }, []);

  useEffect(() => {
    setActiveMenuItem(location.pathname);
    let wrapper = document.getElementById("wrapper");
    if (wrapper) {
      isSidebarOpen
        ? wrapper.classList.remove("expand")
        : wrapper.classList.add("expand");
    }

  }, [location]);
  
  useCurrentRoute(function (route) {
    if (route && route.name) dispatch(set_header(route.name));
  });

  return (
    <div id="sideBar">
      <div
        className={isSidebarOpen ? "sidebar-wrapper" : "sidebar-wrapper shrink"}
        id="mySidenav"
      >
        <div className="close-sidemenu-icon" onClick={toggleSidebar}>
          <i className="bi bi-filter-left"></i>
        </div>
        <div className="logo-white">
          <img src={logo} alt="Logo" />
        </div>
        <ul className="side-menu" style={{ cursor: "pointer" }}>
          <li>
            <a
              className={
                activeMenuItem === "/dashboard"
                  ? "active menu-like-item"
                  : "menu-like-item"
              }
              onClick={() => {
                handleMenuItemClick("/dashboard");
              }}
            >
              <i
                aria-label="Dashboard"
                title="Dashboard"
                className="bi bi-columns-gap menu-left-icon"
              ></i>
              <span> Dashboard </span>
            </a>
          </li>
          {(user?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) ||
            user?.roleIds?.includes(USER_ROLES.ROLE_ID_TESTER)) && (
            <>
              <li>
                <a
                  className={
                    activeMenuItem === "/assessee"
                      ? "active menu-like-item"
                      : "menu-like-item"
                  }
                  onClick={() => {
                    handleMenuItemClick("/assessee");
                  }}
                >
                  <i
                    aria-label="User"
                    title="User"
                    className="bi bi-person menu-left-icon"
                  ></i>
                  <span> Assessees </span>
                </a>
              </li>
              
              <li>
                <a
                  className={
                    activeMenuItem === "/testing-requests"
                      ? "active menu-like-item"
                      : "menu-like-item"
                  }
                  onClick={() => {
                    handleMenuItemClick("/testing-requests");
                  }}
                >
                  <i
                    aria-label="Testing Requests"
                    title="Testing Requests"
                    className="bi bi-file-earmark-plus menu-left-icon"
                  ></i>
                  <span> Testing Requests</span>
                </a>
              </li>
              <li>
                <a
                  className={
                    activeMenuItem === "/applications"
                      ? "active menu-like-item"
                      : "menu-like-item"
                  }
                  onClick={() => {
                    handleMenuItemClick("/applications");
                  }}
                >
                  <i
                    aria-label="Applications"
                    title="Applications"
                    className="bi bi-file-earmark-bar-graph menu-left-icon"
                  ></i>
                  <span> Applications </span>
                </a>
              </li>
              {user?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) && (
                <li>
                  <a
                    className={
                      ["/testcase-config", "/validate-config"].some(item => activeMenuItem.includes(item))
                        ? "active menu-like-item"
                        : "menu-like-item"
                    }
                    onClick={() => {
                      handleMenuItemClick("/testcase-config");
                    }}
                  >
                    <i
                      aria-label="Testcase Config"
                      title="Testcase Config"
                      className="bi bi-gear-fill menu-left-icon"
                    ></i>
                    <span> Testcase Configuration </span>
                  </a>
                </li>
              )}
              {user?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) && (
                <li>
                  <a
                    className={
                      ["/admin-users", "/add-admin-user"].some(item => activeMenuItem.includes(item))
                        ? "active menu-like-item"
                        : "menu-like-item"
                    }
                    onClick={() => {
                      handleMenuItemClick("/admin-users");
                    }}
                  >
                    <i
                      aria-label="User"
                      title="User"
                      className="bi bi-person-gear menu-left-icon"
                    ></i>
                    <span> User Management </span>
                  </a>
                </li>
              )}
            </>
          )}
          {user?.roleIds?.includes(USER_ROLES.ROLE_ID_ASSESSEE) && (
              <Fragment>
                {" "}
                <li>
                  <a
                    className={
                      ["/testing-requests", "/register-application"].some(item => activeMenuItem.includes(item))
                        ? "active menu-like-item"
                        : "menu-like-item"
                    }
                    onClick={() => {
                      handleMenuItemClick("/testing-requests");
                    }}
                  >
                    <i
                      aria-label="Testing Requests"
                      title="Testing Requests"
                      className="bi bi-file-earmark-plus menu-left-icon"
                    ></i>
                    <span> Testing Requests </span>
                  </a>
                </li>
              </Fragment>
            )}
        </ul>
      </div>
    </div>
  );
}
