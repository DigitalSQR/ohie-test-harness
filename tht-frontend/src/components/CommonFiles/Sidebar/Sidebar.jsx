import { useLocation, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { log_out } from "../../../reducers/authReducer";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { clearAuthInfo } from "../../../api/configs/axiosConfigs";
import "./_sidebar.scss";
import logo from "../../../styles/images/logo-white.png";
import { Fragment, useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { USER_ROLES } from "../../../constants/role_constants";
import { store } from "../../../store/store";

export default function Sidebar() {
	const dispatch = useDispatch();
	const navigate = useNavigate();
	const location = useLocation();
	const [isSidebarOpen, setIsSidebarOpen] = useState(true);
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

	return (
		<div
			className={
				isSidebarOpen
					? "sidebar-wrapper open"
					: "sidebar-wrapper shrink"
			}
			id="mySidenav"
		>
			<div className="close-sidemenu-icon" onClick={toggleSidebar}>
				<i className="bi bi-filter-left"></i>
			</div>
			<div className="logo-white">
				<img src={logo} alt="Logo" />
			</div>

			<ul className="side-menu">
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
				{user?.roleIds?.includes(USER_ROLES.ROLE_ID_ADMIN) && (
					<>
						<li>
							<a
								className={
									activeMenuItem === "/dashboard/assessee"
										? "active menu-like-item"
										: "menu-like-item"
								}
								onClick={() =>
									handleMenuItemClick("/dashboard/assessee")
								}
							>
								<i
									aria-label="User Registration"
									title="User Registration"
									className="bi bi-columns-gap menu-left-icon"
								></i>
								<span> Assessee </span>
							</a>
						</li>
						<li>
							<a
								className={
									activeMenuItem ===
									"/dashboard/testing-requests"
										? "active menu-like-item"
										: "menu-like-item"
								}
								onClick={() =>
									handleMenuItemClick(
										"/dashboard/testing-requests"
									)
								}
							>
								<i
									aria-label="Testing Requests"
									title="Testing Requests"
									className="bi bi-file-earmark-bar-graph menu-left-icon"
								></i>
								<span> Testing Requests</span>
							</a>
						</li>
						<li>
							<a
								className={
									activeMenuItem === "/dashboard/applications"
										? "active menu-like-item"
										: "menu-like-item"
								}
								onClick={() =>
									handleMenuItemClick(
										"/dashboard/applications"
									)
								}
							>
								<i
									aria-label="Applications"
									title="Applications"
									className="bi bi-file-earmark-bar-graph menu-left-icon"
								></i>
								<span> Applications </span>
							</a>
						</li>
					</>
				)}
				{user?.roleIds?.includes(USER_ROLES.ROLE_ID_ASSESSEE) && (
					<Fragment>
						{" "}
						<li>
							<a
								className={
									activeMenuItem ===
									"/dashboard/testing-requests"
										? "active menu-like-item"
										: "menu-like-item"
								}
								onClick={() =>
									handleMenuItemClick(
										"/dashboard/testing-requests"
									)
								}
							>
								<i
									aria-label="Testing Requests"
									title="Testing Requests"
									className="bi bi-file-earmark-bar-graph menu-left-icon"
								></i>
								<span> Testing Requests</span>
							</a>
						</li>
					</Fragment>
				)}
				{/*   {user?.roleIds?.includes("role.admin") && (
          <li>
            <a
              className={
                activeMenuItem === "/dashboard/admin-users"
                  ? "active menu-like-item"
                  : "menu-like-item"
              }
              onClick={() => handleMenuItemClick("/dashboard/admin-users")}
            >
              <i className="bi bi-columns-gap menu-left-icon  "></i>
              <span> Admin Users</span>
            </a>
          </li>
        )}
      */}
				{/* <li>
          <a
            className="menu-like-item"
            onClick={() => {
              dispatch(log_out());
            }}
            aria-label="Logout"
            title="Logout"
          >
            <i
              aria-label="Logout"
              title="Logout"
              className="bi bi-box-arrow-right menu-left-icon"
            ></i>
            <span> Logout </span>
          </a>
        </li> */}
			</ul>
		</div>
	);
}
