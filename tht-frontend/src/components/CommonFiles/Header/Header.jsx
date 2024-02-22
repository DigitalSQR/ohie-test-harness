import { Fragment, useEffect, useState } from "react";
import avatar from "../../../styles/images/avatar.jpg";
import "./_header.scss";
import { UserAPI } from "../../../api/UserAPI";
import { USER_ROLE_NAMES } from "../../../constants/role_constants";
import { store } from "../../../store/store";
import { log_out } from "../../../reducers/authReducer";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getHighestPriorityRole } from "../../../utils/utils";

export default function Header({ headerContent }) {
  const [userInfo, setUserInfo] = useState();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  useEffect(() => {
    const userInfo = store.getState().userInfoSlice;
    setUserInfo(userInfo);
  }, []);
  return (
    <Fragment>
      <header>
        <div className="pd-left-240 ps-30" id="header-col-1"></div>
        <div className="d-flex align-items-center justify-content-between heading">
          {/* <div className="bell-icon">
						<i className="bi bi-bell side-nav-toggle"></i>
					</div> */}
          <h5 className="mb-0">{headerContent}</h5>
          <div className="dropdown">
            <div
              className="user-dropdown"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <span className="user-pic">{/* <img src={avatar} /> */}</span>
              <span className="user-name">
                {userInfo?.name}
                <i className="bi bi-chevron-down"></i>
              </span>
              <span className="font-size-12">
                {userInfo ? getHighestPriorityRole(userInfo) : ""}
              </span>
            </div>
            <ul className="dropdown-menu">
              <li
                onClick={() => {
                  navigate("/dashboard/user-profile");
                }}
              >
                <a className="dropdown-item" href="#">
                  Update Profile
                </a>
              </li>
              <li
                onClick={() => {
                  navigate("/dashboard/reset-password");
                }}
              >
                <a className="dropdown-item" href="#">
                  Reset Password
                </a>
              </li>
              <li
                onClick={() => {
                  dispatch(log_out());
                }}
              >
                <a className="dropdown-item" href="#">
                  Log Out
                </a>
              </li>
            </ul>
          </div>
        </div>
      </header>
    </Fragment>
  );
}
