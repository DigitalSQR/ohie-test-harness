import React, { useState } from "react";
import { TrophyOutlined, EyeOutlined } from "@ant-design/icons";
import ComplianceByComponentModal from "../Modals/ComplianceByComponentModal";
const ComplianceByComponent = (props) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  return (
    <>
      <div
        className="row align-items-center"
        style={{ border: "1px solid black" }}
      >
        <div className="col-auto">
          <TrophyOutlined style={{ fontSize: "48px" }} />
        </div>

        <div className="col-lg-4 col-md-6 col-sm-8 col-12">
          <p className="mb-0" style={{ fontSize: "18px" }}>
            {props?.component}
          </p>
        </div>

        <div className="col">
          {props.data.appName.map((appName, index) => (
            <div key={index}>
              {index === 0 && (
                <>
                  <p
                    className="fw-bold"
                    style={{ fontSize: "20px", marginBottom: "8px", textTransform:"capitalize" }}
                  >
                    {appName}
                  </p>
                  <p style={{ fontSize: "18px", marginTop: "-8px" }}>
                    {props.data.compliancePercentage[index]}% Compliance
                  </p>
                </>
              )}
              {index !== 0 && !!props.show && (
                <>
                  <p
                    className="fw-bold"
                    style={{ fontSize: "20px", marginBottom: "8px" }}
                  >
                    {appName}
                  </p>
                  <p style={{ fontSize: "18px", marginTop: "-8px" }}>
                    {props.data.compliancePercentage[index]}% Compliance
                  </p>
                </>
              )}
            </div>
          ))}
        </div>

        <div className="col-auto cursor-pointer" style={{ fontSize: "30px" }}>
          <EyeOutlined onClick={() => setIsModalOpen(true)} />
        </div>
        <div>
          <ComplianceByComponentModal
            isModalOpen={isModalOpen}
            setIsModalOpen={setIsModalOpen}
            component={props?.component}
            data={props?.data}
          />
        </div>
      </div>
    </>
  );
};

export default ComplianceByComponent;
