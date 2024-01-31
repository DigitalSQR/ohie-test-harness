import React, { useEffect, useState } from "react";
import "./componentList.scss";
import { useNavigate } from "react-router-dom";
import { EditOutlined } from "@ant-design/icons";
import { ComponentAPI } from "../../../api/ComponentAPI";
import { notification } from "antd";
import { useLoader } from "../../loader/LoaderContext";
const ComponentList = () => {
  const navigate = useNavigate();
  const [components, setComponents] = useState([]);
  const { showLoader, hideLoader } = useLoader();
  const handleUpdate = (componentId) => {
    navigate(`/dashboard/component-specification/${componentId}`);
  };
  useEffect(() => {
    showLoader();
    ComponentAPI.getComponents()
      .then((res) => {
        hideLoader();
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
                      <EditOutlined
                        onClick={() => handleUpdate(component.id)}
                      />
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
