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
      <td id="nameId">
        <span>
          {loading ? (
            <td>
              <Spin indicator={<LoadingOutlined className="loading-indicator" />} />
            </td>
          ) : (
            <span className="fw-bold">{user?.name}</span>
          )}
        </span>
      </td>
      <td>
        <span>
          {loading ? (
            <td>
              <Spin indicator={<LoadingOutlined className="loading-indicator" />} />
            </td>
          ) : (
            <span className="fw-bold">{user.companyName ? user.companyName : '–'}</span>
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
