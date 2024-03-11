import { useEffect, useState } from "react";
import avatar from "../../../styles/images/defaultDP.jpeg";
import "./_header.scss";
import { store } from "../../../store/store";
import { log_out } from "../../../reducers/authReducer";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getHighestPriorityRole } from "../../../utils/utils";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { DOCUMENT_STATE_ACTIVE } from "../../../constants/document_constants";
import { DocumentAPI } from "../../../api/DocumentAPI";
import UseEventEmitter from "../EventEmitter/EventEmitter";
import { APP_EVENTS } from "../../../constants/event_constants";

/**
 * Header Component
 *
 * This component renders the header bar of the application.
 * It displays the provided header content and handles the sidebar state.
 *
 * Props:
 *   - headerContent: The content to display in the header.
 *   - isSidebarOpen: A boolean indicating whether the sidebar is open or closed.
 *
 * Usage Example:
 *   <Header headerContent="My App" isSidebarOpen={true} />
 */
export default function Header({ headerContent, isSidebarOpen }) {
  // State and Hooks.
  // Holds information about the current user.
  const [userInfo, setUserInfo] = useState();

  // Holds the URL of the user's display picture.
  const [displayPictureUrl, setDisplayPictureUrl] = useState("");

  // A dispatch function for triggering Redux actions.
  const dispatch = useDispatch();

  // A function for navigating between routes.
  const navigate = useNavigate();

  // Retrieves the user ID from the Redux store.
  const userID = useSelector((store) => store.userInfoSlice.id);

  // Function: getUserInfo
  // Description: Fetches user information from the Redux store and updates the state with the retrieved data. It also fetches the user's display picture URL from the server.
  // Steps:
  //   1. Retrieves user information from the Redux store.
  //   2. Sets the state with the retrieved user information.
  //   3. Retrieves the user's display picture URL from the server by making an API call.
  //   4. Updates the state with the fetched display picture URL.
  const getUserInfo = () => {
    const userInfo = store.getState().userInfoSlice;
    setUserInfo(userInfo);
    DocumentAPI.getDocumentsByRefObjUriAndRefId(
      RefObjUriConstants.USER_REFOBJURI,
      userID,
      DOCUMENT_STATE_ACTIVE
    ).then(async (res) => {
      if (res.content.length !== 0) {
        const name = res.content[0].name;
        const id = res.content[0].id;

        const url = await DocumentAPI.base64Document(id, name);
        setDisplayPictureUrl(url);
      } else if (res.content.length === 0) {
        setDisplayPictureUrl();
      }
    });
  };

  const capitalizeFirstLetter = (word) => {
      return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
  };

  useEffect(() => {
    getUserInfo();
  }, []);

  // Called when REFRESH_USER_PROFILE events emits from any component.
  const handleUserProfileRefresh = () => {
    getUserInfo();
  };

  // Effect to register event to refresh user profile.
  useEffect(() => {
    UseEventEmitter.addListener(
      APP_EVENTS.REFRESH_USER_PROFILE,
      handleUserProfileRefresh
    );
    return () => {
      UseEventEmitter.removeListener(
        APP_EVENTS.REFRESH_USER_PROFILE,
        handleUserProfileRefresh
      );
    };
  }, []);

  return (
    <div id="header" style={{ cursor: "pointer" }}>
      <header>
        <div className="d-flex align-items-center justify-content-between heading">
          <h5
            className={`pd-left${isSidebarOpen ? "-240" : ""} ${
              !isSidebarOpen ? "marginLeft" : ""
            } transition ps-30`}
          >
            {headerContent}
          </h5>
          <div className="dropdown">
            <div
              className="user-dropdown"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <span className="user-pic">
                <img src={displayPictureUrl ? displayPictureUrl : avatar} />
              </span>
              <span className="user-name">
                {userInfo?.name}
                <i className="bi bi-chevron-down"></i>
              </span>
              <span style={{fontSize:"12px"}}>
                {userInfo ? capitalizeFirstLetter(getHighestPriorityRole(userInfo)) : ""}
              </span>
            </div>
            <ul className="dropdown-menu">
              <li
                onClick={() => {
                  navigate("/user-profile");
                }}
              >
                <a className="dropdown-item">Update Profile</a>
              </li>
              <li
                onClick={() => {
                  navigate("/login");
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
    </div>
  );
}
