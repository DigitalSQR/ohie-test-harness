import React, { useEffect, useState } from "react";
import "./ComponentSpecification.scss";
import { useLocation, useNavigate } from "react-router-dom";
import { EditOutlined } from "@ant-design/icons";
import { useLoader } from "../../loader/LoaderContext";
import { SpecificationAPI } from "../../../api/SpecificationAPI";
import { Specification, SpecificationDTO } from "../../../dto/SpecificationDTO";
import { Switch } from "antd";

const ComponentSpecification: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("Automation");
  const [specifications, setSpecifications] = useState<Specification[] | undefined>();
  const { showLoader, hideLoader } = useLoader();
  const location = useLocation();
  const { componentId, name } = location.state;

  const openTab = (tabName: string) => {
    if (tabName !== activeTab) {
      setActiveTab(tabName);
      fetchData(tabName === "Manual");
    }
  };

  useEffect(() => {
    fetchData(false);
    console.log(componentId);
  }, []);

  const handleEdit = (specificationId: string) => {
    if (activeTab === "Manual") {
      navigate(`/dashboard/manual-testcases/${specificationId}`, {
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
      const resp: SpecificationDTO = await SpecificationAPI.getSpecificationsByComponentId(
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

  return (
    <div id="wrapper">
      <div className="bcca-breadcrumb">
        <div className="bcca-breadcrumb-item">{name}</div>
        <div
          className="bcca-breadcrumb-item"
          onClick={() => {
            navigate("/dashboard/testcase-config");
          }}
        >
          Components
        </div>
      </div>
      <div className="tabs mt-5">
        <button
          className={`tablinks ${activeTab === "Automation" ? "activeTab" : ""}`}
          onClick={() => openTab("Automation")}
        >
          Automation
        </button>
        <button
          className={`tablinks ${activeTab === "Manual" ? "activeTab" : ""}`}
          onClick={() => openTab("Manual")}
        >
          Manual
        </button>
      </div>
      <div className={`tabcontent ${activeTab === "Automation" ? "show" : ""}`}>
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
                    {activeTab === "Manual" && (
                      <span className="action-icon">
                        <EditOutlined onClick={() => handleEdit(specification.id)} />
                      </span>
                    )}
                    <Switch
                        defaultChecked={true}
                        // onChange={(checked) => handleToggleChange(specification.id, checked)}
                        checkedChildren="ACTIVE"
                        unCheckedChildren="INACTIVE"
                      />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default ComponentSpecification;
