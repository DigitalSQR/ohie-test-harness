import { useLocation, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import "./_sidebar.scss";
import logo from "../../../styles/images/logo-white.png";
import { Fragment, useEffect, useState } from "react";
import { USER_ROLES } from "../../../constants/role_constants";
import { store } from "../../../store/store";
import { set_header } from "../../../reducers/homeReducer";
import { useCurrentRoute } from "../../../routes/routes";
import { set_blocker } from "../../../reducers/blockedReducer";
import { notification } from "antd";
export default function Sidebar({ isSidebarOpen, setIsSidebarOpen }) {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const [activeMenuItem, setActiveMenuItem] = useState();
  const [user, setUser] = useState();
  const blocker = useSelector((state) => state.blockSlice.isBlocked)
  const blockerDesc = useSelector((state) => state.blockSlice.dynamicDescription)
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
    if(blocker === 'blocked' && blockerDesc !== '') {
      notification.warning({
        className:"notificationWarning",
        message:"Error",
        description:blockerDesc,
        placement:"bottomRight"
      })
      return;
    } 
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
        <div className="close-sidemenu-icon" onClick={toggleSidebar} id="#Sidebar-toggleSideBar">
          <i className="bi bi-filter-left"></i>
        </div>
        <div className="logo-white">
          <img src={logo} alt="Logo" />
        </div>
        <ul className="side-menu">
          <li>
            <a
            id="#Sidebar-dashboard"
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
          <>
            {(user?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) ||
              user?.roleIds?.includes(USER_ROLES.ROLE_ID_TESTER)) && (
              <li>
                <a
                id="#Sidebar-assessee"
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
                    aria-label="Assessees"
                    title="Assessees"
                    className="bi bi-person menu-left-icon"
                  ></i>
                  <span> Assessees </span>
                </a>
              </li>
            )}

            {(user?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) ||
              user?.roleIds?.includes(USER_ROLES.ROLE_ID_PUBLISHER) || user?.roleIds.includes(USER_ROLES.ROLE_ID_TESTER)) && (
              <li>
                <a
                  id="#Sidebar-applications"
                  className={
                    [
                      "/applications",
                      "/choose-test",
                      "/manual-testing",
                      "/automated-testing",
                    ].some((item) => activeMenuItem.includes(item))
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
            )}
            {user?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) && (
              <li>
                <a
                  id="#Sidebar-testcaseConfig"
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
                      aria-label="Testcase Configuration"
                      title="Testcase Configuration"
                      className="bi bi-gear menu-left-icon"
                    ></i>
                    <span> Testcase Configuration </span>
                  </a>
                </li>
              )}
              {user?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) && (
                <li>
                  <a
                  id="#Sidebar-UserManagement"
                  className={
                    ["/user-management", "/create-user"].some((item) =>
                      activeMenuItem.includes(item)
                    )
                      ? "active menu-like-item"
                      : "menu-like-item"
                  }
                  onClick={() => {
                    handleMenuItemClick("/user-management");
                  }}
                >
                  <i
                    aria-label="User Management"
                    title="User Management"
                    className="bi bi-person-gear menu-left-icon"
                  ></i>
                  <span> User Management </span>
                </a>
              </li>
            )}
          </>
          {user?.roleIds?.includes(USER_ROLES.ROLE_ID_ASSESSEE) && (
              <Fragment>
                {" "}
                <li>
                  <a
                  id="#Sidebar-testingRequests"
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
                      title="Verification Requests"
                      className="bi bi-file-earmark-plus menu-left-icon"
                    ></i>
                    <span> Verification Requests </span>
                  </a>
                </li>
              </Fragment>
            )}
        </ul>
      </div>
    </div>
  );
}
