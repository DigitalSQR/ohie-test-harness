import React, { useEffect, useState } from "react";
import "./componentList.scss";
import { useNavigate } from "react-router-dom";
import { EditOutlined } from "@ant-design/icons";
import { ComponentAPI } from "../../../api/ComponentAPI";
import { Switch, notification } from "antd";
import { useLoader } from "../../loader/LoaderContext";
import { ComponentDTO, MyContent } from "../../../dto/ComponentDTO";

const ComponentList: React.FC = () => {
  const navigate = useNavigate();
  const [components, setComponents] = useState<MyContent[]>([]);
  const { showLoader, hideLoader } = useLoader();

  const handleUpdate = (componentId: string, name: string) => {
    console.log(componentId);
    navigate(`/dashboard/component-specification/${componentId}`, {
      state: {
        name,
        componentId,
      },
    });
  };

  useEffect(() => {
    showLoader();
    ComponentAPI.getComponents()
      .then((res: ComponentDTO) => {
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
            <h4 className="my-2">Components</h4>
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
                  <td className="text-center" colSpan={2}>
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
                        style={{ marginRight: "8px" }}
                        onClick={() =>
                          handleUpdate(component.id, component.name)
                        }
                      />
                      <Switch
                        defaultChecked={true}
                        // onChange={(checked) => handleToggleChange(component.id, checked)}
                        checkedChildren="ACTIVE"
                        unCheckedChildren="INACTIVE"
                      />
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