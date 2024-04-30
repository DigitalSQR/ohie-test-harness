import { useEffect, useState } from "react";
import avatar from "../../../styles/images/defaultDP.jpeg";
import "./_header.scss";
import { store } from "../../../store/store";
import { log_out } from "../../../reducers/authReducer";
import { BellOutlined, DeleteOutlined } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getHighestPriorityRole } from "../../../utils/utils";
import { RefObjUriConstants } from "../../../constants/refObjUri_constants";
import { DOCUMENT_STATE_ACTIVE } from "../../../constants/document_constants";
import { DocumentAPI } from "../../../api/DocumentAPI";
import UseEventEmitter from "../EventEmitter/EventEmitter";
import { APP_EVENTS } from "../../../constants/event_constants";
import { NotificationAPI } from "../../../api/NotificationAPI";
import WebSocketService from "../../../api/WebSocketService";
import moment from "moment";
import {
  NOTIFICATION_STATUS_ARCHIVED,
  NOTIFICATION_STATUS_UNREAD,
} from "../../../constants/notification_constants";
import { notification } from "antd";

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

  const token = useSelector((store) => store.authSlice.access_token);

  const [notifications, setNotifications] = useState();

  //useState for the unread notification count
  const [unreadCount, setUnreadCount] = useState();
  const blocker = useSelector((state) => state.blockSlice.isBlocked)
  const blockerDesc = useSelector((state) => state.blockSlice.dynamicDescription)


  const { stompClient, webSocketConnect, webSocketDisconnect } =
    WebSocketService();

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

  const unreadNotificationCount = (data) => {
    const count = data?.filter(
      (notification) => notification.state == NOTIFICATION_STATUS_UNREAD
    ).length;
    setUnreadCount(count);
  };

  //Function to update the state of a notification
  const ArchiveNotificationById = async (id) => {
    try {
      await NotificationAPI.updateNotificationState(
        id,
        NOTIFICATION_STATUS_ARCHIVED
      );
      await fetchNotifications(); // Wait for notifications to be fetched after updating state
    } catch (error) {}
  };

  //Function to update the state of notifications in bulk
  const bulkArchiveNotifications = async () => {
    try {
      await NotificationAPI.bulkUpdateNotificationState(
        NOTIFICATION_STATUS_UNREAD,
        NOTIFICATION_STATUS_ARCHIVED
      );
      await fetchNotifications(); // Wait for notifications to be fetched after updating state
    } catch (error) {}
  };

  const fetchNotifications = () => {
    NotificationAPI.getAllNotifications()
      .then((responseData) => {
        setNotifications(responseData.data.content.reverse());
        unreadNotificationCount(responseData.data.content);
      })
      .catch((error) => {});
  };

  useEffect(() => {
    webSocketConnect();
    if (stompClient && stompClient.connected) {
      const destination = "/notification/" + userInfo?.id;
      stompClient.subscribe(
        destination,
        (msg) => {
          const parsedNotifications = JSON.parse(msg.body);
          setNotifications((prevNotifications) => [
            parsedNotifications,
            ...prevNotifications,
          ]);
          setUnreadCount((prevCount) => prevCount + 1);
        },
        { token: `Bearer ${token}` }
      );
    }
  }, [stompClient]);

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

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleDeleteNotification = (notificationId, event) => {
    // Handle delete logic here
    ArchiveNotificationById(notificationId);

    // Prevent event propagation to parent elements (including dropdown toggle)
    event.stopPropagation();
  };

  return (
    <div id="header">
      <header>
        <div className="d-flex align-items-center justify-content-between heading">
          <h5
            className={`pd-left${isSidebarOpen ? "-240" : ""} ${
              !isSidebarOpen ? "marginLeft" : ""
            } transition ps-30`}
          >
            {headerContent}
          </h5>
          <div className="d-flex align-items-center">
            <div className="dropdown">
              <div
                className="user-dropdown"
                data-bs-toggle="dropdown"
                aria-expanded="false"
                style={{ position: "relative", display: "inline-block" }}
              >
                <div style={{ marginRight: "30px", fontSize: "24px" }}>
                  <BellOutlined />
                </div>
                {unreadCount > 0 && (
                  <div className="notification-count">{unreadCount}</div>
                )}
              </div>
              <ul
                className="dropdown-menu notification-menu  mt-2"
                style={{ maxHeight: "90vh", overflowY: "auto" }}
              >
                <h5 className="notification-header  p-3">
                  <i class="bi bi-bell me-1 fw-bold"></i>Notifications
                  {unreadCount > 0 && (
                    <span
                      style={{
                        fontSize: "12px",
                        fontWeight: "bold",
                        float: "right",
                        textDecoration: "underline",
                        cursor: "pointer",
                      }}
                      onClick={bulkArchiveNotifications}
                    >
                      {" "}
                      Archive All Notifications
                    </span>
                  )}
                </h5>
                {notifications && notifications.length > 0 && (
                  <>
                    {notifications.map((notification, index) => {
                      const formattedDateTime = moment(
                        notification.meta.createdAt
                      ).format("Do MMMM, YYYY hh:mm:ss A");

                      return notification.state ===
                        NOTIFICATION_STATUS_UNREAD ? (
                        <li
                          key={notification.id}
                          className="list-group-item  notification-item"
                        >
                          <div>{notification.message}</div>
                          <div className="delete-icon">
                            <DeleteOutlined
                              onClick={(event) =>
                                handleDeleteNotification(notification.id, event)
                              }
                            />
                          </div>
                          <div className="time-stamp">{formattedDateTime}</div>
                        </li>
                      ) : null;
                    })}
                  </>
                )}
              </ul>
            </div>

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
                <span style={{ fontSize: "12px" }}>
                  {userInfo
                    ? capitalizeFirstLetter(getHighestPriorityRole(userInfo))
                    : ""}
                </span>
              </div>
              <ul className="dropdown-menu">
                <li
                onClick={() => {
                  if(blocker === 'blocked' && blockerDesc !== '') {
                    notification.warning({
                      className:"notificationWarning",
                      message:"Error",
                      description:blockerDesc,
                      placement:"bottomRight"
                    })
                    return;
                  } else {
                    // Navigate to user profile
                    navigate("/user-profile");
                  }
                }}
                >
                  <a className="dropdown-item">Update Profile</a>
                </li>
                <li
                  onClick={() => {
                    if(blocker === 'blocked' && blockerDesc !== '') {
                      notification.warning({
                        className:"notificationWarning",
                        message:"Error",
                        description:blockerDesc,
                        placement:"bottomRight"
                      })
                      return;
                    } else {
                    navigate("/login");
                    dispatch(log_out());
                  }}}
                >
                  <a className="dropdown-item" href="#">
                    Log Out
                  </a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </header>
    </div>
  );
}
