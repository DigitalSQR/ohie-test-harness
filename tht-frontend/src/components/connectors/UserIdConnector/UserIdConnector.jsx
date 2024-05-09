import React, { Fragment, useEffect, useState } from "react";
import { UserAPI } from "../../../api/UserAPI";
import { LoadingOutlined } from "@ant-design/icons";
import { Spin } from "antd";
import "./userIdConnector.scss";
const UserIdConnector = ({ userId, isLink }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    UserAPI.getUserById(userId).then((user) => {
      console.log(user)
      setUser(user)})
      .finally(() => setLoading(false));
  }, []);
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
          <span className="fw-bold">{user?.companyName}</span>
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

export default UserIdConnector;
