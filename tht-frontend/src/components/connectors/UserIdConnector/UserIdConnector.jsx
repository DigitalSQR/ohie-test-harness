import React, { useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { LoadingOutlined } from "@ant-design/icons";
import { Spin } from "antd";
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
        <p style={{ margin: "0px" }}>{user?.name}</p>
      ) : (
        <Spin indicator={<LoadingOutlined style={{ fontSize: 14 }} spin />} />
      )}
    </span>
  );
};

export default UserIdConnector;
