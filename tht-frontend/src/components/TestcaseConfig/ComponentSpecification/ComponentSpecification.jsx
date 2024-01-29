import React, { useEffect, useState } from "react";
import "./ComponentSpecification.scss";
import { useNavigate, useParams } from "react-router-dom";
import { EditOutlined} from "@ant-design/icons";
import { useLoader } from "../../loader/LoaderContext";
import { SpecificationAPI } from "../../../api/SpecificationAPI";
const ComponentSpecification = () => {
  const navigate = useNavigate();
  const { componentId } = useParams();
  const [activeTab, setActiveTab] = useState("Automation");
  const [specifications, setSpecifications] = useState();
  const { showLoader, hideLoader } = useLoader();
  const openTab = (tabName) => {
    setActiveTab(tabName);
    if (tabName === "Automation") {
      fetchData(false);
    } else {
      fetchData(true);
    }
  };
  useEffect(() => {
    fetchData(false);
  }, []);

  const handleEdit = (specificationId) => {
    if (activeTab === "Manual") {
      navigate(`/dashboard/manual-testcases/${specificationId}`);
    }
  };

  const fetchData = async (manual) => {
    showLoader();
    const resp = await SpecificationAPI.getSpecificationsByComponentId(
      componentId,
      manual
    );
    setSpecifications(resp.content);
    hideLoader();
  };
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
                      <EditOutlined
                        onClick={() => handleEdit(specification.id)}
                      />
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
