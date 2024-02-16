import React, { useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { LoadingOutlined } from "@ant-design/icons";
import { Spin } from "antd";
import "./userIdConnector.scss";
const UserIdConnector = ({ userId, isLink }) => {
  const [user, setUser] = useState();
  // const [show, setShow] = useState(false);
  // const handleClose = () => setShow(false);
  useEffect(() => {
    UserAPI.getUserById(userId).then((user) => {
      setUser(user);
    });
  }, []);
  return (
    <span>
      {user?.name ? (
        <p className="user-name">{user?.name}</p>
      ) : (
        <Spin indicator={<LoadingOutlined className="loading-indicator" />} />
      )}
    </span>
  );
};

export default UserIdConnector;
