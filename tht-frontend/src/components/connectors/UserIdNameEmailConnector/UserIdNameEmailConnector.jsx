import React, { Fragment, useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { LoadingOutlined } from "@ant-design/icons";
import { Spin } from "antd";
import "./UserIdNameEmailConnector.scss";
const UserIdNameEmailConnector = ({ userId, isLink }) => {
  const [user, setUser] = useState();
  useEffect(() => {
    UserAPI.getUserById(userId).then((user) => {
      setUser(user);
    });
  }, []);
  return (
    <React.Fragment id="nameEmailConnector">
      <td className="d-flex align-items-center">
        <span>
          {user?.name ? (
            <p className="user-name">{user?.name}</p>
          ) : (
            <Spin
              indicator={<LoadingOutlined className="loading-indicator" />}
            />
          )}
        </span>
        <td />
      </td>
      <td className="toLowerCase-words">
        <span>
          {user?.email ? (
            user?.email
          ) : (
            <Spin
              indicator={<LoadingOutlined className="loading-indicator" />}
            />
          )}
        </span>
      </td>
    </React.Fragment>
  );
};

export default UserIdNameEmailConnector;
