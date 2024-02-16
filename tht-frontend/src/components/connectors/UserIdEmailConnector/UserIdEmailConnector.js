import React, { useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
const UserIdEmailConnector = ({ userId }) => {
  const [user, setUser] = useState();

  useEffect(() => {
    UserAPI.getUserById(userId).then((user) => {
      setUser(user);
    });
  }, []);

  return (
    <span>
      {user?.email ? (
        <p className="user-email">{user?.email}</p>
      ) : (
        <Spin indicator={<LoadingOutlined className="loading-indicator" />} />
      )}
    </span>
  );
};

export default UserIdEmailConnector;
