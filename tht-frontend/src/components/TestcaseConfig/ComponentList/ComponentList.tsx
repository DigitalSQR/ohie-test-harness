import React, { useEffect, useState } from "react";
import "./componentList.scss";
import { useNavigate } from "react-router-dom";
import { EditOutlined } from "@ant-design/icons";
import { ComponentAPI } from "../../../api/ComponentAPI";
import { Switch, notification } from "antd";
import { useLoader } from "../../loader/LoaderContext";
import { ComponentDTO, MyContent } from "../../../dto/ComponentDTO";
import { ComponentsActionStateLabels } from "../../../constants/components_constants";

const ComponentList: React.FC = () => {
  const navigate = useNavigate();
  const [components, setComponents] = useState<MyContent[]>([]);
  const { showLoader, hideLoader } = useLoader();

  const [filterState, setFilterState] = useState("");
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
    var state = filterState;
    console.log(state);

    getAllComponents(state);
  }, [filterState]);

  const getAllComponents = (filterState) => {
    showLoader();
    ComponentAPI.getComponents(filterState)
      .then((res: ComponentDTO) => {
        console.log(res);
        hideLoader();
        setComponents(res.content);
      })
      .catch((err) => {
        notification.error({
          placement: "bottomRight",
          message: err.data?.message,
        });
      });
  };
  const componentRequestStates = [
    ...ComponentsActionStateLabels,
    { label: "All", value: "" },
  ];

  const handleToggleChange = (componentId, state) => {
    showLoader();
    const newState =
      state === "component.status.active"
        ? "component.status.inactive"
        : "component.status.active";

    ComponentAPI.changeState(componentId, newState)
      .then((res) => {
        hideLoader();
        console.log(res);

        let temp = components;
        const idx = temp.findIndex((t) => t.id === componentId);
        temp[idx] = res.data;

        if (filterState) {
          temp = temp.filter((t) => t.id !== componentId);
        }
        setComponents(temp);
      })
      .catch((error) => {
        throw error;
      });
  };

  return (
    <div id="wrapper">
      <div className="col-12">
        <div className="row mb-2 justify-content-between">
          <div className="col-lg-4 col-md-6 col-sm-7 col-xl-3 col-12">
            <div className="d-flex align-items-baseline">
              <span className="pe-3 text-nowrap">Status :</span>
              <div className="mb-3">
                <select
                  onChange={(e) => {
                    setFilterState(e.target.value);
                  }}
                  value={filterState}
                  className="form-select custom-select custom-select-sm"
                  aria-label="Default select example"
                >
                  {componentRequestStates.map((componentRequestState) => (
                    <option
                      value={componentRequestState.value}
                      key={componentRequestState.value}
                    >
                      {componentRequestState.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>
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
                        className="edit-icon"
                        onClick={() =>
                          handleUpdate(component.id, component.name)
                        }
                      />
                      <Switch
                        defaultChecked={
                          component?.state === "component.status.active"
                        }
                        onChange={(checked) =>
                          handleToggleChange(component.id, component.state)
                        }
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
