import React, { useEffect, useState } from "react";
import "./ComponentSpecification.scss";
import { useLocation, useNavigate } from "react-router-dom";
import { EditOutlined } from "@ant-design/icons";
import { useLoader } from "../../loader/LoaderContext";
import { SpecificationAPI } from "../../../api/SpecificationAPI";
import { Specification, SpecificationDTO } from "../../../dto/SpecificationDTO";
import { Switch, Tabs } from "antd";
import type { TabsProps } from "antd";
import { useDispatch } from "react-redux";
import { set_header } from "../../../reducers/homeReducer";

const ComponentSpecification: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("1");
  const [specifications, setSpecifications] = useState<
    Specification[] | undefined
  >();
  const { showLoader, hideLoader } = useLoader();
  const location = useLocation();
  const { componentId, name } = location.state;
  const dispatch = useDispatch();

  useEffect(() => {
    fetchData(false);
    dispatch(set_header(name));
    console.log(componentId);
  }, []);

  const handleEdit = (specificationId: string) => {
    if (activeTab === "2") {
      navigate(`/manual-testcases/${specificationId}`, {
        state: {
          specificationId,
          componentId,
          name,
        },
      });
    }
  };

  const fetchData = async (manual: boolean) => {
    showLoader();
    try {
      const resp: SpecificationDTO =
        await SpecificationAPI.getSpecificationsByComponentId(
          componentId,
          manual
        );
      setSpecifications(resp.content);
    } catch (error) {
      console.error("Error fetching specifications:", error);
      setSpecifications(undefined);
    } finally {
      hideLoader();
    }
  };

  const handleToggleChange = (specificationId:string, state:string) => {
    showLoader();
    const newState =
      state === "specification.status.active"
        ? "specification.status.inactive"
        : "specification.status.active";

        SpecificationAPI.changeState(specificationId, newState)
      .then((res) => {
        hideLoader();

        let temp = specifications;
        const idx = temp.findIndex((t) => t.id === specificationId);
        temp[idx] = res.data;
        
        setSpecifications(temp);
        console.log(res);
      })
      .catch((error) => {
        throw error;
      });
  };

  const helper = () => {
    return (
      <div className={`tabcontent ${activeTab === "1" ? "show" : ""}`}>
        {specifications && specifications?.length > 0 ? (
          <div className="table-responsive mt-3">
            <table className="data-table">
              <thead>
                <tr>
                  <th className="col-9">Specifications</th>
                  <th className="col-3">Actions</th>
                </tr>
              </thead>
              <tbody>
                {specifications?.map((specification) => (
                  <tr key={specification.name}>
                    <td>{specification.name}</td>
                    <td className="action-icons-container">
                      {activeTab === "2" && (
                        <span className="action-icon">
                          <EditOutlined
                            onClick={() => handleEdit(specification.id)}
                          />
                        </span>
                      )}
                      <Switch
                        defaultChecked={specification?.state === "specification.status.active"}
                        onChange={(checked) => handleToggleChange(specification.id, specification.state)}
                        checkedChildren="ACTIVE"
                        unCheckedChildren="INACTIVE"
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center mt-5  ">
            <h4>
              <i>No specifications found</i>
            </h4>
          </div>
        )}
      </div>
    );
  };

  const items: TabsProps["items"] = [
    {
      key: "1",
      label: "Automation",
      children: helper(),
    },
    {
      key: "2",
      label: "Manual",
      children: helper(),
    },
  ];

  const onChange = (key: string) => {
    setActiveTab(key);
    fetchData(key === "2");
  };

  return (
    <div id="componentSpecification">
    <div id="wrapper" className="component-specification-page">
      <div className="bcca-breadcrumb">
        <div className="bcca-breadcrumb-item">{name}</div>
        <div
          className="bcca-breadcrumb-item"
          onClick={() => {
            navigate("/testcase-config");
          }}
        >
          Components
        </div>
      </div>
      <Tabs
        className="mt-5"
        activeKey={activeTab}
        items={items}
        onChange={onChange}
      />
    </div>  
    </div>
  );
};

export default ComponentSpecification;
