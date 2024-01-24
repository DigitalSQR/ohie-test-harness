import React, { useEffect, useState } from "react";
import "./componentList.scss";
import { EditOutlined } from "@ant-design/icons";
import { ComponentAPI } from "../../../api/ComponentAPI";
import { notification } from "antd";

const ComponentList = () => {
  const [components, setComponents] = useState([]);

  useEffect(() => {
    ComponentAPI.getComponents()
      .then((res) => {
        setComponents(res.content);
      })
      .catch((err) => {
        notification.error({
          placement: "bottomRight",
          message: err.data?.message,
        });
      });
  }, []);

  return (
    <div id="wrapper">
      <div className="col-12">
        <div className="row mb-2 justify-content-between">
          <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
            <b>Components</b>
          </div>
        </div>
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Component Name</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {components.length === 0 ? (
                <tr>
                  <td className="text-center" colSpan="6">
                    There are no components registered.
                  </td>
                </tr>
              ) : (
                components.map((component) => (
                  <tr key={component?.id}>
                    <td>
                      {component?.name}
                      <p className="description">{component.description}</p>
                    </td>
                    <td className="d-flex">
                      <EditOutlined />
                      <span className="badges-green-dark">ACTIVE</span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default ComponentList;
