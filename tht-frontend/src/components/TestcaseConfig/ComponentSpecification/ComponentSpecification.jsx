import React, { useEffect, useState } from "react";
import "./ComponentSpecification.scss";
import { useParams } from "react-router-dom";
import { EditOutlined, DeleteOutlined } from "@ant-design/icons"; // Assuming you're using Ant Design icons
import { useLoader } from "../../loader/LoaderContext";
import { SpecificationAPI } from "../../../api/SpecificationAPI";
const ComponentSpecification = () => {
  const { componentId } = useParams();
  const [activeTab, setActiveTab] = useState("Automation");
  const [manualComponents, setManualComponents] = useState([{ name: "def" }]);
  const [specifications, setSpecifications]=useState();
  const [automationComponents, setAutomationComponents] = useState([
    { name: "abc" },
  ]);
  const { showLoader, hideLoader } = useLoader();
  const openTab = (tabName) => {
    setActiveTab(tabName);
    if (tabName === "Automation") {
      fetchData(false);
    } else {
      fetchData(true)
    } 
  };
  useEffect(() => {
    fetchData(false);
  }, []);

  const fetchData = async (manual) => {
    showLoader();
    const resp = await SpecificationAPI.getSpecificationsByComponentId(
      componentId,
      manual
    );
    setSpecifications(resp.content);
    hideLoader();
  };
  const components =
    activeTab === "Automation" ? automationComponents : manualComponents;
  return (
    <div id="wrapper">
      <div className="tabs">
        <button
          className={`tablinks ${
            activeTab === "Automation" ? "activeTab" : ""
          }`}
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
        <div className="table-responsive">
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
                    <span className="action-icon">
                      <EditOutlined />
                    </span>
                    <span className="badges-green-dark">ACTIVE</span>
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
