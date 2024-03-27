import React, { Fragment, useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { LoadingOutlined } from "@ant-design/icons";
import { Spin } from "antd";
import "./UserIdNameEmailConnector.scss";
const UserIdNameEmailConnector = ({ userId, isLink }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    UserAPI.getUserById(userId)
      .then((userData) => {
        setUser(userData);
      })
      .finally(() => setLoading(false));
  }, [userId]);

  return (
    <Fragment>
      <td className="d-flex align-items-center" id="nameId">
        <span>
          {loading ? (
            <td>
              <Spin indicator={<LoadingOutlined className="loading-indicator" />} />
            </td>
          ) : (
            <p className="user-name fw-bold">{user?.name}</p>
          )}
        </span>
      </td>
      <td className="toLowerCase-words">
        <span>
          {loading ? (
            <td>
              <Spin indicator={<LoadingOutlined className="loading-indicator" />} />
            </td>
          ) : (
            user?.email
          )}
        </span>
      </td>
    </Fragment>
  );
};

export default UserIdNameEmailConnector;
